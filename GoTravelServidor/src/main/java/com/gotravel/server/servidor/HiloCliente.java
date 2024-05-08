package com.gotravel.server.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.Usuario;
import com.gotravel.server.model.Viaje;
import com.gotravel.server.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class HiloCliente extends Thread {

    private final Socket cliente;
    private final Protocolo protocolo;
    private final AppService service;
    private final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    private boolean sesionIniciada;

    public HiloCliente(Socket cliente, AppService service) {
        this.cliente = cliente;
        this.service = service;
        this.protocolo = new Protocolo();
        this.sesionIniciada = false;
    }

    @Override
    public void run() {

        DataOutputStream salida = null;
        DataInputStream entrada = null;

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .setLenient()
                .create();

        try {

            salida = new DataOutputStream(cliente.getOutputStream());
            entrada = new DataInputStream(cliente.getInputStream());

            while (!sesionIniciada) {

                // ESPERANDO_CREDENCIALES -> COMPROBANDO_CREDENCIALES
                protocolo.procesarMensaje(null);

                String output = "";
                String[] fromCliente = entrada.readUTF().split(";");

                String opcion = fromCliente[0];
                String email = fromCliente[1];
                String contrasena = fromCliente[2];

                Usuario u = null;

                if(opcion.equalsIgnoreCase("login")) {

                    u = service.findUsuarioByEmail(email);

                    // COMPROBANDO_CREDENCIALES -> if(entrada) ATENDIENDO_PETICIONES else (ESPERANDO_CREDENCIALES)
                    output = protocolo.procesarMensaje("" + (u != null && u.getContrasena().equals(contrasena)));

                } else if (opcion.equalsIgnoreCase("registro")) {

                    String nombre = fromCliente[3];

                    u = service.saveUsuario(new Usuario(nombre, email, contrasena));

                    // COMPROBANDO_CREDENCIALES -> if(entrada) ATENDIENDO_PETICIONES else (ESPERANDO_CREDENCIALES)
                    output = protocolo.procesarMensaje("" + (u != null));

                }

                if(output.equalsIgnoreCase("login")){
                    sesionIniciada = true;
                    String json = gson.toJson(u);
                    salida.writeUTF(json);

                    // Primero envío al usuario una confirmación de si tiene foto asociada o no
                    salida.writeBoolean(u.getFoto() != null);
                    salida.flush();

                    // Si la tiene, envía la foto
                    if(u.getFoto() != null) {
                        salida.writeInt(u.getFoto().length); // Envía la longitud del ByteArray
                        salida.write(u.getFoto()); // Envía el ByteArray
                        salida.flush();
                    }

                } else {
                    salida.writeUTF("");
                }

            }

            String data;
            while((data = entrada.readUTF()) != null) {

                String[] fromUser = data.split(";");
                String opcion = fromUser[0];
                int idUsuario = Integer.parseInt(fromUser[1]);

                // ATENDIENDO_PETICIONES -> if(entrada == chatear) CHATEANDO else ATENDIENDO_PETICIONES
                String output = protocolo.procesarMensaje(opcion);

                if(output.equalsIgnoreCase("peticion")){
                    String json = switch (opcion) {
                        case "viajes" -> {
                            List<Viaje> viajes = service.findViajesByUsuarioId(idUsuario);
                            yield gson.toJson(viajes);
                        }
                        case "proximoViaje" -> {
                            Viaje proximoViaje = service.findProximoViajeByUsuarioId(idUsuario);
                            yield gson.toJson(proximoViaje);
                        }
                        case "viajeActual" -> {
                            Viaje viajeActual = service.findViajeActualByUsuarioId(idUsuario);
                            yield gson.toJson(viajeActual);
                        }
                        case "update" -> {
                            String tabla = fromUser[2];
                            String jsonFromUser = entrada.readUTF();
                            String jsonFromServer = "";
                            System.out.println(jsonFromUser);
                            if(tabla.equalsIgnoreCase("usuario")) {
                                Usuario usuarioFromUser = gson.fromJson(jsonFromUser, Usuario.class);
                                usuarioFromUser = service.saveUsuario(usuarioFromUser);
                                jsonFromServer = gson.toJson(usuarioFromUser);
                            }
                            yield jsonFromServer;
                        }
                        case "updateContrasena" -> {
                            String contrasenaActual = fromUser[2];
                            String contrasenaNueva = fromUser[3];
                            Usuario u = service.findUsuarioById(idUsuario);
                            if(u.getContrasena().equals(contrasenaActual)) {
                                u.setContrasena(contrasenaNueva);
                                service.saveUsuario(u);
                                yield gson.toJson(u);
                            }
                            yield "";
                        }
                        case "uploadFoto" -> {
                            String tabla = fromUser[2];
                            int length = entrada.readInt(); // Lee el tamaño del array de bytes
                            byte[] byteArray = new byte[length];
                            entrada.readFully(byteArray); // Lee el array de bytes
                            if(tabla.equalsIgnoreCase("usuario")) {
                                Usuario u = service.findUsuarioById(idUsuario);
                                u.setFoto(byteArray);
                                u = service.saveUsuario(u);
                                yield gson.toJson(u);
                            }
                            yield "";

                        }
                        default -> "";
                    };

                    salida.writeUTF(json);
                }

            }

        } catch (IOException e) {

            LOG.warn("Se ha desconectado un usuario");
        } finally {
            if (cliente != null) {
                try {
                    if(entrada != null) {
                        entrada.close();
                    }
                    if(salida != null) {
                        salida.close();
                    }
                    cliente.close();
                } catch (IOException e) {
                    LOG.error("Error al cerrar la conexión con un cliente: {}", e.getMessage());
                }
            }
        }

    }

}

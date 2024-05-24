package com.gotravel.server.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.server.PayPal.PayPalToken;
import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.Etapa;
import com.gotravel.server.model.Metodopago;
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

    private final Protocolo protocolo;
    private final AppService service;
    private final Sesion sesion;
    private final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    private boolean sesionIniciada;
    private boolean terminar;
    private final List<HiloCliente> clientesConectados;

    public HiloCliente(Socket cliente, AppService service, List<HiloCliente> clientesConectados) {
        this.service = service;
        this.protocolo = new Protocolo(service);
        this.sesionIniciada = false;
        this.terminar = false;

        sesion = new Sesion(cliente);
        this.clientesConectados = clientesConectados;
        this.clientesConectados.add(this);

    }

    @Override
    public void run() {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .setLenient()
                .create();

        try {

            sesion.setSalida(new DataOutputStream(sesion.getCliente().getOutputStream()));
            sesion.setEntrada(new DataInputStream(sesion.getCliente().getInputStream()));

            while (!sesionIniciada) {

                String[] fromCliente = sesion.getEntrada().readUTF().split(";");

                String opcion = fromCliente[0];
                String email = fromCliente[1];
                String contrasena = fromCliente[2];

                Usuario u = null;

                if(opcion.equals("login")){
                    u = service.findUsuarioByEmail(email);
                } else if (opcion.equals("registro")) {
                    String nombre = fromCliente[3];
                    u = service.saveUsuario(new Usuario(nombre, email, contrasena));
                }

                String output = protocolo.procesarMensaje(gson.toJson(u) +  ";" + contrasena);

                // Si ha iniciado sesion, se manda un json que contiene al usuario
                // Si no ha iniciado sesion, se manda un mensaje de reintentar
                sesion.getSalida().writeUTF(output);

                // Si ha iniciado sesion, se pone la flag en true y se le pregunta al usuario por la foto
                if(u != null){
                    sesionIniciada = true;

                    sesion.setUsuario(u);

                    boolean tieneFoto = u.getFoto() != null;

                    // Primero envío al usuario una confirmación de si tiene foto asociada o no
                    sesion.getSalida().writeBoolean(tieneFoto);
                    sesion.getSalida().flush();

                    // Si la tiene, envía la foto
                    if(tieneFoto) {
                        sesion.getSalida().writeInt(u.getFoto().length); // Envía la longitud del ByteArray
                        sesion.getSalida().write(u.getFoto()); // Envía el ByteArray
                        sesion.getSalida().flush();
                    }

                }

            }

            while(!terminar) {

                String[] fromCliente = sesion.getEntrada().readUTF().split(";");
                String opcion = fromCliente[0];

                String output = protocolo.procesarMensaje(opcion);

                if(output.equals("peticion")){
                    int idUsuario = sesion.getUsuario().getId();
                    String json = switch (opcion) {
                        case "consultarViajes" -> {
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
                            String tabla = fromCliente[1];
                            String jsonFromUser = sesion.getEntrada().readUTF();
                            String jsonFromServer = "";
                            if (tabla.equalsIgnoreCase("usuario")) {
                                Usuario usuarioFromUser = gson.fromJson(jsonFromUser, Usuario.class);
                                usuarioFromUser = service.saveUsuario(usuarioFromUser);
                                sesion.setUsuario(usuarioFromUser);
                                jsonFromServer = gson.toJson(usuarioFromUser);
                            } else if (tabla.equalsIgnoreCase("viaje")) {
                                Viaje viajeFromUser = gson.fromJson(jsonFromUser, Viaje.class);
                                viajeFromUser.setUsuario(sesion.getUsuario());
                                viajeFromUser = service.saveViaje(viajeFromUser);
                                jsonFromServer = gson.toJson(viajeFromUser);
                            } else if (tabla.equalsIgnoreCase("etapa")) {
                                int idViaje = Integer.parseInt(fromCliente[2]);
                                Etapa etapaFromUser = gson.fromJson(jsonFromUser, Etapa.class);
                                etapaFromUser.setViaje(service.findViajeById(idViaje));
                                etapaFromUser = service.saveEtapa(etapaFromUser);
                                jsonFromServer = gson.toJson(etapaFromUser);
                            }
                            yield jsonFromServer;
                        }
                        case "updateContrasena" -> {
                            String contrasenaActual = fromCliente[1];
                            String contrasenaNueva = fromCliente[2];
                            Usuario u = service.findUsuarioById(idUsuario);
                            if(u.getContrasena().equals(contrasenaActual)) {
                                u.setContrasena(contrasenaNueva);
                                service.saveUsuario(u);
                                sesion.setUsuario(u);
                                yield gson.toJson(u);
                            }
                            yield "";
                        }
                        case "uploadFoto" -> {
                            String tabla = fromCliente[1];
                            int length = sesion.getEntrada().readInt(); // Lee el tamaño del array de bytes
                            byte[] byteArray = new byte[length];
                            sesion.getEntrada().readFully(byteArray); // Lee el array de bytes
                            if(tabla.equalsIgnoreCase("usuario")) {
                                Usuario u = service.findUsuarioById(idUsuario);
                                u.setFoto(byteArray);
                                u = service.saveUsuario(u);
                                sesion.setUsuario(u);
                                yield gson.toJson(u);
                            }
                            yield "";
                        }
                        case "save" -> {
                            String tabla = fromCliente[1];
                            String jsonFromUser = sesion.getEntrada().readUTF();
                            String jsonFromServer = "";
                            if(tabla.equalsIgnoreCase("viaje")) {
                                Viaje viajeFromUser = gson.fromJson(jsonFromUser, Viaje.class);
                                viajeFromUser.setUsuario(service.findUsuarioById(idUsuario));
                                Viaje v = service.saveViaje(viajeFromUser);
                                jsonFromServer = gson.toJson(v);
                            } else if(tabla.equalsIgnoreCase("etapa")) {
                                int idViaje = Integer.parseInt(fromCliente[2]);
                                Viaje v = service.findViajeById(idViaje);
                                Etapa etapaFromUser = gson.fromJson(jsonFromUser, Etapa.class);
                                etapaFromUser.setViaje(v);
                                etapaFromUser = service.saveEtapa(etapaFromUser);
                                jsonFromServer = gson.toJson(etapaFromUser);
                            }
                            yield jsonFromServer;
                        }
                        case "findById" -> {
                            String tabla = fromCliente[1];
                            String jsonFromServer = "";
                            if(tabla.equalsIgnoreCase("viaje")) {
                                int idViaje = Integer.parseInt(fromCliente[2]);
                                Viaje v = service.findViajeById(idViaje);
                                jsonFromServer = gson.toJson(v);
                            }
                            yield jsonFromServer;
                        }
                        case "findMetodosPago" -> {
                            List<Metodopago> metodosPago = service.findMetodosPagoByUsuarioId(idUsuario);
                            yield gson.toJson(metodosPago);
                        }
                        case "suscribirme" -> {
                            PayPalToken pt = new PayPalToken(
                                    token -> System.out.println("token: " + token)
                            );
                            yield "";
                        }
                        default -> "";
                    };

                    sesion.getSalida().writeUTF(json);
                } else if (output.equals("finalizado")) {
                    terminar = true;
                    clientesConectados.remove(this);
                    LOG.info("Se ha desconectado un usuario");
                }

            }

        } catch (IOException e) {
            clientesConectados.remove(this);
            LOG.warn("Se ha interrumpido la conexion con un usuario");
        } finally {
            if (sesion.getCliente() != null) {
                try {
                    if(sesion.getEntrada() != null) {
                        sesion.getEntrada().close();
                    }
                    if(sesion.getSalida() != null) {
                        sesion.getSalida().close();
                    }
                    sesion.getCliente().close();
                } catch (IOException e) {
                    LOG.error("Error al cerrar la conexión con un cliente: {}", e.getMessage());
                }
            }
        }

    }

}

package com.gotravel.server.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.Suscripcion;
import com.gotravel.server.model.*;
import com.gotravel.server.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HiloCliente extends Thread {

    private final Protocolo protocolo;
    private final AppService service;
    private final Sesion sesion;
    private final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    private boolean sesionIniciada;
    private boolean terminar;
    private final List<HiloCliente> clientesConectados;
    private ServerApplication server;

    public HiloCliente(Socket cliente, AppService service, List<HiloCliente> clientesConectados, ServerApplication server) {
        this.service = service;
        this.protocolo = new Protocolo(service);
        this.sesionIniciada = false;
        this.terminar = false;

        sesion = new Sesion(cliente);
        this.clientesConectados = clientesConectados;
        this.clientesConectados.add(this);

        this.server = server;

    }

    @Override
    public void run() {

        try {

            sesion.setSalida(new DataOutputStream(sesion.getCliente().getOutputStream()));
            sesion.setEntrada(new DataInputStream(sesion.getCliente().getInputStream()));

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .serializeNulls()
                    .setLenient()
                    .create();

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

                // Si se obtiene el usuario correctamente, se actualiza, por si se ha cambiado algo de la suscripción y los roles.
                if(!output.equals("reintentar") && !output.equals("oculto") && u != null) {

                    Suscripcion s = u.getSuscripcion();
                    if(s != null && s.getRenovar().equals("0") && s.getEstado().equals("ACTIVE")) { // SI NO HAY QUE RENOVARLA Y ESTÁ ACTIVA
                        LocalDate fechaFin = LocalDate.parse(s.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        if(fechaFin.isBefore(LocalDate.now()) || fechaFin.isEqual(LocalDate.now())) { // Y ADEMÁS LA FECHA DE FINAL ES PRESENTE O PASADA
                            s.setEstado("INACTIVE"); // SE CAMBIA A INACTIVA
                            service.saveSuscripcion(s); // SE ACTUALIZA LA SUSCRIPCION
                            for (int i = 0; i < u.getRoles().size(); i++) {
                                if(u.getRoles().get(i).getNombre().equals("Profesional")) {
                                    u.getRoles().remove(i); // SE LE QUITA EL ROL PROFESIONAL
                                }
                            }
                            u = service.saveUsuario(u); // SE ACTUALIZA EL USUARIO
                            output = gson.toJson(u);
                        }
                    } else if (s != null && s.getRenovar().equals("1") && s.getEstado().equals("ACTIVE")) { // SI HAY QUE RENOVARLA Y ESTÁ ACTIVA
                        // SE LANZA UN HILO QUE SE ENCARGA DE IR RENOVANDO LAS SUSCRIPCIONES Y ACTUALIZARLAS
                        new Thread(() -> {
                            LocalDate fechaFin = LocalDate.parse(s.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            while(fechaFin.isBefore(LocalDate.now()) || fechaFin.isEqual(LocalDate.now())) { // Y MIENTRAS ADEMÁS LA FECHA DE FINAL SEA PRESENTE O PASADA
                                fechaFin = fechaFin.plusMonths(1); // SE AÑADE UN MES A LA FECHA DE FINALIZACIÓN
                                Pago p = new Pago(s.getUsuario(), 4.99, fechaFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // SE CREA UN NUEVO PAGO
                                s.getPagos().add(p); // SE LE AÑADE EL PAGO A LA SUSCRIPCION
                            }
                            s.setFechaFinal(fechaFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            service.saveSuscripcion(s); // SE ACTUALIZA LA SUSCRIPCION
                        }).start();
                    }

                }

                // Si ha iniciado sesion, se manda un json que contiene al usuario
                // Si no ha iniciado sesion, se manda un mensaje de reintentar o de que el usuario está oculto
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

                System.out.println(Arrays.toString(fromCliente));

                String opcion = fromCliente[0];

                String output = protocolo.procesarMensaje(opcion);

                if(output.equals("peticion")){
                    int idUsuario = sesion.getUsuario().getId();
                    String json = switch (opcion) {
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
                            } else if(tabla.equalsIgnoreCase("servicio")) {
                                System.out.println(jsonFromUser);
                                Servicio servicioFromUser = gson.fromJson(jsonFromUser, Servicio.class);
                                servicioFromUser.setUsuario(sesion.getUsuario());
                                servicioFromUser = service.saveServicio(servicioFromUser);
                                jsonFromServer = gson.toJson(servicioFromUser);
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
                        case "findByUserId" -> {
                            String tabla = fromCliente[1];
                            String jsonFromServer = "";
                            if(tabla.equalsIgnoreCase("viaje")) {
                                List<Viaje> viajes = service.findViajesByUsuarioId(idUsuario);
                                jsonFromServer = gson.toJson(viajes);
                            } else if(tabla.equalsIgnoreCase("proximoViaje")){
                                Viaje proximoViaje = service.findProximoViajeByUsuarioId(idUsuario);
                                jsonFromServer = gson.toJson(proximoViaje);
                            } else if(tabla.equalsIgnoreCase("viajeActual")){
                                Viaje viajeActual = service.findViajeActualByUsuarioId(idUsuario);
                                yield gson.toJson(viajeActual);
                            } else if(tabla.equalsIgnoreCase("suscripcion")){
                                Suscripcion suscripcion = service.findSuscripcionByUsuarioId(idUsuario);
                                yield gson.toJson(suscripcion);
                            }
                            yield jsonFromServer;
                        }
                        case "findAll" -> {
                            String tabla = fromCliente[1];
                            String jsonFromServer = "";
                            if(tabla.equalsIgnoreCase("tipoServicio")) {
                                List<Tiposervicio> tiposServicio = service.findAllTiposServicio();
                                jsonFromServer = gson.toJson(tiposServicio);
                            }
                            yield jsonFromServer;
                        }
                        case "suscripcion" -> {
                            String eleccion = fromCliente[1];
                            String jsonFromServer = "";
                            if(eleccion.equals("crear")) {
                                Suscripcion s = gson.fromJson(fromCliente[2], Suscripcion.class);

                                s.setUsuario(sesion.getUsuario());
                                s.getPagos().get(0).setUsuario(sesion.getUsuario());
                                service.saveSuscripcion(s);

                                sesion.getUsuario().getRoles().add(service.findRol("Profesional"));
                                sesion.setUsuario(service.saveUsuario(sesion.getUsuario()));
                                jsonFromServer =  gson.toJson(sesion.getUsuario());
                            } else if (eleccion.equals("renovar")) {
                                String idSuscripcion = fromCliente[2];

                                Suscripcion s = service.findSuscripcionById(idSuscripcion);
                                s.setRenovar("1");
                                s.setEstado("ACTIVE");
                                LocalDate fechaFinal = LocalDate.parse(s.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                if(fechaFinal.isAfter(LocalDate.now()) || fechaFinal.isEqual(LocalDate.now())) {
                                    // Si está renovando una suscripción que ya ha caducado, se le pone a la fecha final un mes desde el día de hoy
                                    s.setFechaFinal(LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                }
                                jsonFromServer = gson.toJson(service.saveSuscripcion(s));
                            } else if (eleccion.equals("cancelar")) {
                                String idSuscripcion = fromCliente[2];

                                Suscripcion s = service.findSuscripcionById(idSuscripcion);
                                s.setRenovar("0");
                                jsonFromServer = gson.toJson(service.saveSuscripcion(s));
                            }
                            yield jsonFromServer;
                        }
                        default -> "";
                    };

                    sesion.getSalida().writeUTF(json);
                } else if (output.equals("finHilo")) {
                    terminar = true;
                    clientesConectados.remove(this);
                    LOG.info("Se ha desconectado un usuario");
                } else if (output.equals("finServer")) {
                    sesion.getSalida().writeUTF("cerrando");
                    sesion.getSalida().flush();
                    LOG.info("El servidor se cerrará en 10 segundos");
                    sleep(10000);
                    terminar = true;
                    clientesConectados.remove(this);
                    server.pararServidor();
                }

            }

        } catch (IOException e) {
            clientesConectados.remove(this);
            LOG.warn("Se ha interrumpido la conexion con un usuario");
        } catch (InterruptedException e) {
            clientesConectados.remove(this);
            LOG.warn("Se ha interrumpido el servidor: {}", e.getMessage());
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

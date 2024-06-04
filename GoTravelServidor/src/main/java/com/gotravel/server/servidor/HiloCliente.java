package com.gotravel.server.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.server.Mailitrap.EmailSender;
import com.gotravel.server.Paypal.Checkout;
import com.gotravel.server.Paypal.Subscriptions;
import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.Suscripcion;
import com.gotravel.server.model.*;
import com.gotravel.server.service.AppService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HiloCliente extends Thread {

    @Getter
    private final Protocolo protocolo;
    private final AppService service;
    private final Sesion sesion;
    private final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    private boolean sesionIniciada;
    private boolean terminar;
    private final List<HiloCliente> clientesConectados;
    private final ServerApplication server;

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

                if(opcion.equals("recuperarContrasena")){

                    Usuario u = service.findUsuarioByEmail(email);

                    if(u != null) {
                        int newPassword = (int) (Math.random() * 100000000);
                        String passwordCifrada = Sha256Encryptor.cifrarTexto("" + newPassword);

                        sesion.getSalida().writeBoolean(EmailSender.sendRecoveryEmail(email, "" + newPassword));

                        u.setContrasena(passwordCifrada);
                        service.saveUsuario(u);
                    }

                    sesionIniciada = true;
                    terminar = true;

                } else {
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

            }

            while(!terminar) {

                String[] fromCliente = sesion.getEntrada().readUTF().split(";");

                System.out.println("FromCliente " + Arrays.toString(fromCliente));

                String opcion = fromCliente[0];

                String output = protocolo.procesarMensaje(opcion);

                if(output.equals("chateando")) {

                    int idOtroUsuario = Integer.parseInt(fromCliente[1]);

                    String jsonFromUser;
                    while (protocolo.procesarMensaje((jsonFromUser = sesion.getEntrada().readUTF())).equals("chat")) {
                        Mensaje mensajeFromUser = gson.fromJson(jsonFromUser, Mensaje.class);
                        mensajeFromUser.setEmisor(sesion.getUsuario());
                        mensajeFromUser.setReceptor(service.findUsuarioById(idOtroUsuario));
                        mensajeFromUser = service.saveMensaje(mensajeFromUser);
                        sesion.getSalida().writeUTF(gson.toJson(mensajeFromUser));
                        for (HiloCliente hc : clientesConectados) {
                            if(hc.sesion.getUsuario().getId() == idOtroUsuario && hc.getProtocolo().estado == Estado.CHATEANDO){
                                hc.sesion.getSalida().writeUTF(gson.toJson(mensajeFromUser));
                                break;
                            }
                        }
                    }

                    sesion.getSalida().writeUTF("fin");


                } else {
                    switch (output) {
                        case "peticion" -> {
                            int idUsuario = sesion.getUsuario().getId();
                            String json = switch (opcion) {
                                case "update" -> {
                                    String tabla = fromCliente[1];
                                    String jsonFromUser = sesion.getEntrada().readUTF();
                                    String jsonFromServer = "";
                                    if (tabla.equalsIgnoreCase("usuario")) {
                                        Usuario usuarioFromUser = gson.fromJson(jsonFromUser, Usuario.class);
                                        usuarioFromUser.setRoles(sesion.getUsuario().getRoles());
                                        usuarioFromUser.setFoto(sesion.getUsuario().getFoto());
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
                                    } else if (tabla.equalsIgnoreCase("servicio")) {
                                        Servicio servicioFromUser = gson.fromJson(jsonFromUser, Servicio.class);
                                        servicioFromUser.setUsuario(sesion.getUsuario());
                                        servicioFromUser = service.saveServicio(servicioFromUser);
                                        jsonFromServer = gson.toJson(servicioFromUser);
                                    }
                                    yield jsonFromServer;
                                }
                                case "updateContrasena" -> {
                                    String contrasenaActual = fromCliente[1];
                                    String contrasenaNueva = fromCliente[2];
                                    Usuario u = service.findUsuarioById(idUsuario);
                                    if (u.getContrasena().equals(contrasenaActual)) {
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
                                    if (tabla.equalsIgnoreCase("usuario")) {
                                        Usuario u = service.findUsuarioById(idUsuario);
                                        u.setFoto(byteArray);
                                        u = service.saveUsuario(u);
                                        sesion.setUsuario(u);
                                        yield gson.toJson(u);
                                    } else if (tabla.equalsIgnoreCase("servicio")) {
                                        int idServicio = Integer.parseInt(fromCliente[2]);
                                        Servicio s = service.findServicioById(idServicio);
                                        s.getImagenes().add(new Imagen(byteArray, s));
                                        s = service.saveServicio(s);
                                        yield gson.toJson(s);
                                    }
                                    yield "";
                                }
                                case "save" -> {
                                    String tabla = fromCliente[1];
                                    String jsonFromUser = sesion.getEntrada().readUTF();
                                    String jsonFromServer = "";
                                    if (tabla.equalsIgnoreCase("viaje")) {
                                        Viaje viajeFromUser = gson.fromJson(jsonFromUser, Viaje.class);
                                        viajeFromUser.setUsuario(service.findUsuarioById(idUsuario));
                                        Viaje v = service.saveViaje(viajeFromUser);
                                        jsonFromServer = gson.toJson(v);
                                    } else if (tabla.equalsIgnoreCase("etapa")) {
                                        int idViaje = Integer.parseInt(fromCliente[2]);
                                        Viaje v = service.findViajeById(idViaje);
                                        Etapa etapaFromUser = gson.fromJson(jsonFromUser, Etapa.class);
                                        etapaFromUser.setViaje(v);
                                        etapaFromUser = service.saveEtapa(etapaFromUser);
                                        jsonFromServer = gson.toJson(etapaFromUser);
                                    } else if (tabla.equalsIgnoreCase("servicio")) {
                                        Servicio servicioFromUser = gson.fromJson(jsonFromUser, Servicio.class);
                                        servicioFromUser.setUsuario(sesion.getUsuario());
                                        servicioFromUser = service.saveServicio(servicioFromUser);
                                        jsonFromServer = gson.toJson(servicioFromUser);
                                    } else if (tabla.equalsIgnoreCase("resena")) {
                                        Resena resenaFromUser = gson.fromJson(jsonFromUser, Resena.class);
                                        resenaFromUser.setUsuario(sesion.getUsuario());
                                        resenaFromUser.setServicio(service.findServicioById(resenaFromUser.getId().getIdServicio()));
                                        resenaFromUser = service.saveResena(resenaFromUser);
                                        jsonFromServer = gson.toJson(resenaFromUser);
                                    }
                                    yield jsonFromServer;
                                }
                                case "findById" -> {
                                    String tabla = fromCliente[1];
                                    String jsonFromServer = "";
                                    if (tabla.equalsIgnoreCase("viaje")) {
                                        int idViaje = Integer.parseInt(fromCliente[2]);
                                        Viaje v = service.findViajeById(idViaje);
                                        jsonFromServer = gson.toJson(v);
                                    }
                                    if (tabla.equalsIgnoreCase("servicio")) {
                                        int idServicio = Integer.parseInt(fromCliente[2]);
                                        Servicio s = service.findServicioById(idServicio);
                                        jsonFromServer = gson.toJson(s);
                                    }
                                    if (tabla.equalsIgnoreCase("usuario")) {
                                        int idOtroUsuario = Integer.parseInt(fromCliente[2]);
                                        Usuario u = service.findUsuarioById(idOtroUsuario);

                                        sesion.getSalida().writeUTF(gson.toJson(u));

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

                                        jsonFromServer = "";
                                    }
                                    yield jsonFromServer;
                                }
                                case "findUsuarioByServicio" -> {
                                    int idServicio = Integer.parseInt(fromCliente[1]);
                                    Usuario u = service.findUsuarioByServicioId(idServicio);
                                    yield gson.toJson(u);
                                }
                                case "findResenasByServicio" -> {
                                    int idServicio = Integer.parseInt(fromCliente[1]);
                                    List<Resena> resenas = service.findResenasByServicioId(idServicio);
                                    sesion.getSalida().writeUTF(gson.toJson(resenas));
                                    for (Resena r : resenas) {
                                        boolean usuarioTieneFoto = r.getUsuario().getFoto() != null;
                                        sesion.getSalida().writeBoolean(usuarioTieneFoto);
                                        if (usuarioTieneFoto) {
                                            sesion.getSalida().writeInt(r.getUsuario().getFoto().length); // Envía la longitud del ByteArray
                                            sesion.getSalida().write(r.getUsuario().getFoto()); // Envía el ByteArray
                                            sesion.getSalida().flush();
                                        }
                                    }
                                    yield "";
                                }
                                case "findByUserId" -> {
                                    String tabla = fromCliente[1];
                                    String jsonFromServer = "";
                                    if (tabla.equalsIgnoreCase("viaje")) {
                                        List<Viaje> viajes = service.findViajesByUsuarioId(idUsuario);
                                        jsonFromServer = gson.toJson(viajes);
                                    } else if (tabla.equalsIgnoreCase("proximoViaje")) {
                                        Viaje proximoViaje = service.findProximoViajeByUsuarioId(idUsuario);
                                        jsonFromServer = gson.toJson(proximoViaje);
                                    } else if (tabla.equalsIgnoreCase("viajeActual")) {
                                        Viaje viajeActual = service.findViajeActualByUsuarioId(idUsuario);
                                        yield gson.toJson(viajeActual);
                                    } else if (tabla.equalsIgnoreCase("suscripcion")) {
                                        Suscripcion suscripcion = service.findSuscripcionByUsuarioId(idUsuario);
                                        yield gson.toJson(suscripcion);
                                    } else if (tabla.equalsIgnoreCase("servicio")) {
                                        List<Servicio> servicios = service.findServiciosByUsuarioId(idUsuario);
                                        jsonFromServer = gson.toJson(servicios);
                                    }
                                    yield jsonFromServer;
                                }
                                case "findImagesFromServicioId" -> {
                                    int idServicio = Integer.parseInt(fromCliente[1]);
                                    String numImagenes = fromCliente[2];
                                    if (numImagenes.equals("one")) {
                                        Imagen i = service.findFirstImageFromServicioId(idServicio);
                                        sesion.getSalida().writeUTF(gson.toJson(i));
                                        if (i != null) {
                                            sesion.getSalida().writeInt(i.getImagen().length); // Envía la longitud del ByteArray
                                            sesion.getSalida().write(i.getImagen()); // Envía el ByteArray
                                            sesion.getSalida().flush();
                                        }
                                    } else if (numImagenes.equals("all")) {
                                        List<Imagen> imagenes = service.findAllImagesFromServicioId(idServicio);

                                        sesion.getSalida().writeUTF(gson.toJson(imagenes));

                                        for (Imagen i : imagenes) {
                                            sesion.getSalida().writeInt(i.getImagen().length); // Envía la longitud del ByteArray
                                            sesion.getSalida().write(i.getImagen()); // Envía el ByteArray
                                            sesion.getSalida().flush();
                                        }
                                    }
                                    yield "";
                                }
                                case "findAllServiciosByFechasAndTipo" -> {
                                    int idEtapa = Integer.parseInt(fromCliente[1]);
                                    Etapa e = service.findEtapaById(idEtapa);
                                    List<Servicio> servicios =
                                            service.findAllServiciosByFechasAndTipo(
                                                    LocalDate.parse(e.getFechaInicio(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                                    LocalDate.parse(e.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                                    e.getTipo());
                                    yield gson.toJson(servicios);
                                }
                                case "findAll" -> {
                                    String tabla = fromCliente[1];
                                    String jsonFromServer = "";
                                    if (tabla.equalsIgnoreCase("tipoServicio")) {
                                        List<Tiposervicio> tiposServicio = service.findAllTiposServicio();
                                        jsonFromServer = gson.toJson(tiposServicio);
                                    }
                                    yield jsonFromServer;
                                }
                                case "findImageFromUserId" -> {
                                    int idOtroUsuario = Integer.parseInt(fromCliente[1]);

                                    byte[] foto = service.findUsuarioById(idOtroUsuario).getFoto();
                                    boolean tieneFoto = foto != null;
                                    sesion.getSalida().writeBoolean(tieneFoto);
                                    if(tieneFoto) {
                                        sesion.getSalida().writeInt(foto.length); // Envía la longitud del ByteArray
                                        sesion.getSalida().write(foto); // Envía el ByteArray
                                        sesion.getSalida().flush();
                                    }

                                    yield "";
                                }
                                case "isServicioContratado" -> {
                                    int idServicio = Integer.parseInt(fromCliente[1]);
                                    Contratacion c = service.findContratacionByServicioAndUsuario(idServicio, idUsuario);
                                    sesion.getSalida().writeBoolean(c != null);
                                    yield "";
                                }
                                case "contratarServicio" -> {
                                    Checkout paypal = new Checkout();

                                    int idServicio = Integer.parseInt(fromCliente[1]);
                                    int idEatapa = Integer.parseInt(fromCliente[2]);
                                    Servicio s = service.findServicioById(idServicio);
                                    Etapa e = service.findEtapaById(idEatapa);

                                    String url = paypal.crearPedido(s);
                                    sesion.getSalida().writeUTF(url);

                                    String idContratacion = sesion.getEntrada().readUTF();

                                    if(!idContratacion.equalsIgnoreCase("cancelar")) {
                                        Contratacion c = paypal.capturarPedido(idContratacion);

                                        c.setUsuario(sesion.getUsuario());
                                        c.getPago().setUsuario(sesion.getUsuario());
                                        c.setServicio(s);
                                        c.setEtapa(e);
                                        service.saveContratacion(c);
                                        yield "" + e.getViaje().getId();
                                    }

                                    yield "";

                                }
                                case "findContratacionesByEtapa" -> {
                                    int idEtapa = Integer.parseInt(fromCliente[1]);
                                    List<Servicio> servicios = service.findServiciosContratadosByEtapa(idEtapa);
                                    yield gson.toJson(servicios);
                                }
                                case "findAllServiciosContratados" -> {
                                    List<Servicio> servicios = service.findServiciosContratadosByUsuario(idUsuario);
                                    yield gson.toJson(servicios);
                                }
                                case "suscripcion" -> {
                                    String eleccion = fromCliente[1];
                                    String jsonFromServer = "";
                                    Subscriptions paypal = new Subscriptions();
                                    switch (eleccion) {
                                        case "crear" -> {
                                            Usuario u = sesion.getUsuario();

                                            String url = "";

                                            if(fromCliente.length > 2) {
                                                url = paypal.crearSuscripcion(u, "desktop");
                                            } else {
                                                url = paypal.crearSuscripcion(u, "android");
                                            }

                                            sesion.getSalida().writeUTF(url);

                                            String subscriptionId = sesion.getEntrada().readUTF();

                                            if(!subscriptionId.equalsIgnoreCase("cancelar")) {
                                                Suscripcion s = paypal.getSubscription(subscriptionId);

                                                s.setUsuario(sesion.getUsuario());
                                                s.getPagos().get(0).setUsuario(sesion.getUsuario());
                                                service.saveSuscripcion(s);

                                                sesion.getUsuario().getRoles().add(service.findRol("Profesional"));
                                                sesion.setUsuario(service.saveUsuario(sesion.getUsuario()));
                                                jsonFromServer = gson.toJson(sesion.getUsuario());
                                            }

                                        }
                                        case "renovar" -> {
                                            String idSuscripcion = fromCliente[2];

                                            Suscripcion s = service.findSuscripcionById(idSuscripcion);

                                            if (paypal.activateSubscription(idSuscripcion)) {
                                                s.setRenovar("1");
                                                s.setEstado("ACTIVE");
                                                LocalDate fechaFinal = LocalDate.parse(s.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                                if (fechaFinal.isAfter(LocalDate.now()) || fechaFinal.isEqual(LocalDate.now())) {
                                                    // Si está renovando una suscripción que ya ha caducado, se le pone a la fecha final un mes desde el día de hoy
                                                    s.setFechaFinal(LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                                }
                                            }

                                            jsonFromServer = gson.toJson(service.saveSuscripcion(s));
                                        }
                                        case "cancelar" -> {
                                            String idSuscripcion = fromCliente[2];

                                            Suscripcion s = service.findSuscripcionById(idSuscripcion);

                                            if (paypal.cancelSubscription(idSuscripcion)) {
                                                s.setRenovar("0");
                                            }

                                            jsonFromServer = gson.toJson(service.saveSuscripcion(s));
                                        }
                                    }
                                    yield jsonFromServer;
                                }
                                case "delete" -> {
                                    String tabla = fromCliente[1];
                                    String jsonFromServer = "";
                                    if (tabla.equalsIgnoreCase("imagen")) {
                                        int idImagen = Integer.parseInt(fromCliente[2]);
                                        sesion.getSalida().writeBoolean(service.deleteImagenById(idImagen));
                                    }
                                    yield jsonFromServer;
                                }
                                case "publicarServicio" -> {
                                    int idServicio = Integer.parseInt(fromCliente[1]);
                                    Servicio s = service.findServicioById(idServicio);
                                    s.setPublicado("1");
                                    s = service.saveServicio(s);
                                    sesion.getSalida().writeBoolean(s != null);
                                    yield "";
                                }
                                case "ocultarServicio" -> {
                                    int idServicio = Integer.parseInt(fromCliente[1]);
                                    Servicio s = service.findServicioById(idServicio);
                                    s.setPublicado("0");
                                    s = service.saveServicio(s);
                                    sesion.getSalida().writeBoolean(s != null);
                                    yield "";
                                }
                                case "findAllMensajes" -> {
                                    List<Mensaje> mensajes = service.findAllMensajesByUsuarioId(idUsuario);

                                    Map<Integer, Mensaje> conversaciones = new HashMap<>();
                                    for (Mensaje mensaje : mensajes) {
                                        int idOtroUsuario = mensaje.getEmisor().getId() == idUsuario ? mensaje.getReceptor().getId() : mensaje.getEmisor().getId();
                                        conversaciones.put(idOtroUsuario, mensaje);
                                    }

                                    yield gson.toJson(conversaciones);
                                }
                                case "findAllMensajesBetweenUsers" -> {
                                    int idOtroUsuario = Integer.parseInt(fromCliente[1]);
                                    List<Mensaje> mensajes = service.findAllMensajesBetweenUsers(idUsuario, idOtroUsuario);
                                    for (Mensaje m : mensajes) {
                                        m.setEmisor(service.findMinimalUserInfoById(m.getEmisor().getId()));
                                        m.setReceptor(service.findMinimalUserInfoById(m.getReceptor().getId()));
                                    }
                                    yield gson.toJson(mensajes);
                                }
                                default -> "";
                            };

                            if (!json.equals("null") && !json.isEmpty()) {
                                //LOG.info("{}: {}", opcion, json);
                            }
                            System.out.println("FromServer " + json);
                            if (!json.isEmpty()) {
                                sesion.getSalida().writeUTF(json);
                                sesion.getSalida().flush();
                            }
                        }
                        case "finHilo" -> {
                            terminar = true;
                            clientesConectados.remove(this);
                            LOG.info("Se ha desconectado un usuario");
                        }
                        case "finServer" -> {
                            sesion.getSalida().writeUTF("cerrando");
                            sesion.getSalida().flush();
                            LOG.info("El servidor se cerrará en 10 segundos");
                            sleep(10000);
                            terminar = true;
                            clientesConectados.remove(this);
                            server.pararServidor();
                        }
                    }
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
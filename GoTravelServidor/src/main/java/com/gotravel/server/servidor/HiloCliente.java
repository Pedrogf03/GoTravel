package com.gotravel.server.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.server.Mailitrap.EmailSender;
import com.gotravel.server.Paypal.Checkout;
import com.gotravel.server.Paypal.Subscriptions;
import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.*;
import com.gotravel.server.service.AppService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiloCliente extends Thread {

    @Getter
    private final Protocolo protocolo;
    private final AppService service;
    private final Sesion sesion;
    private final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);
    private boolean sesionIniciada;
    private boolean terminar;
    private final Map<Integer, HiloCliente> clientesConectados;
    private final ServerApplication server;
    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .setLenient()
            .create();

    public HiloCliente(Socket cliente, AppService service, Map<Integer, HiloCliente> clientesConectados, ServerApplication server) {
        this.service = service;
        this.protocolo = new Protocolo();
        this.sesionIniciada = false;
        this.terminar = false;
        sesion = new Sesion(cliente);
        this.clientesConectados = clientesConectados;
        this.server = server;
    }

    @Override
    public void run() {
        try {

            sesion.setSalida(new DataOutputStream(sesion.getCliente().getOutputStream()));
            sesion.setEntrada(new DataInputStream(sesion.getCliente().getInputStream()));

            while (!sesionIniciada) {
                String[] fromCliente = sesion.getEntrada().readUTF().split(";");
                String opcion = fromCliente[0];
                String email = fromCliente[1];
                if(opcion.equals("recuperarContrasena")){
                    recuperarContrasena(email);
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
                            u = suspenderSuscripcion(u, s);
                        } else if (s != null && s.getRenovar().equals("1") && s.getEstado().equals("ACTIVE")) { // SI HAY QUE RENOVARLA Y ESTÁ ACTIVA
                            renovarSuscripcion(s);
                        }
                    }
                    // Si ha iniciado sesion, se manda un json que contiene al usuario
                    // Si no ha iniciado sesion, se manda un mensaje de reintentar o de que el usuario está oculto
                    sesion.getSalida().writeUTF(output);
                    sesion.getSalida().flush();
                    // Si ha iniciado sesion, se pone la flag en true y se le pregunta al usuario por la foto
                    if(u != null){
                        sesionIniciada = true;
                        sesion.setUsuario(u);
                        enviarFotoUsuario(u);
                    }
                }
            }
            clientesConectados.put(sesion.getUsuario().getId(), this);
            while(!terminar) {

                String[] fromCliente = sesion.getEntrada().readUTF().split(";");
                String opcion = fromCliente[0];
                String output = protocolo.procesarMensaje(opcion);

                System.out.println(Arrays.toString(fromCliente));

                if(output.equals("chateando")) {

                    System.out.println("Dentro de chateando: " + Arrays.toString(fromCliente));

                    int idOtroUsuario = Integer.parseInt(fromCliente[1]);
                    chatear(idOtroUsuario);

                } else {
                    switch (output) {
                        case "peticion" -> {
                            int idUsuario = sesion.getUsuario().getId();
                            String json = switch (opcion) {
                                case "save" -> saveEntity(fromCliente); // --> GUARDAR ENTIDADES
                                case "update" -> updateEntity(fromCliente, idUsuario); // --> ACTUALIZAR ENTIDADES
                                case "delete" -> deleteEntity(fromCliente); // --> BORRAR ENTIDADES
                                case "uploadFoto" -> uploadFoto(fromCliente, idUsuario); // --> GUARDAR IMAGENES
                                case "findById" -> findByEntityId(fromCliente); // --> BUSCAR ENTIDAD POR SU ID
                                case "findByUserId" -> findEntityByUserId(fromCliente, idUsuario); // --> BUSCAR ENTIDAD POR ID DEL USUARIO
                                case "findByServicioId" -> findEntityByServicioId(fromCliente); // --> BUSCAR ENTIDAD POR EL ID DEL SERVICIO
                                case "findAll" -> findAllEntities(fromCliente, idUsuario); // --> BUSCAR TODAS LAS ENTIDADES
                                case "servicio" -> servicio(fromCliente, idUsuario); // --> DIFERENTES OPCIONES SOBRE UN SERVICIO
                                case "suscripcion" -> suscripciones(fromCliente); // --> DIFERENTES OPERACIONES SOBRE UNA SUSCRIPCION
                                case "findContratacionesByEtapa" -> { // --> BUSCAR TODOS LOS SERVICIOS CONTRATADOS EN UNA ETAPA
                                    int idEtapa = Integer.parseInt(fromCliente[1]);
                                    List<Servicio> servicios = service.findServiciosContratadosByEtapa(idEtapa);
                                    yield gson.toJson(servicios);
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
                            clientesConectados.remove(sesion.getUsuario().getId());
                            LOG.info("Se ha desconectado un usuario");
                        }
                        case "finServer" -> {
                            sesion.getSalida().writeUTF("cerrando");
                            sesion.getSalida().flush();
                            LOG.info("Cerrando servidor");
                            terminar = true;
                            clientesConectados.remove(sesion.getUsuario().getId());
                            server.pararServidor();
                        }
                    }
                }

            }

        } catch (IOException e) {
            clientesConectados.remove(sesion.getUsuario().getId());
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

    private String deleteEntity(String[] fromCliente) throws IOException {
        String tabla = fromCliente[1];
        String jsonFromServer = "";
        if (tabla.equalsIgnoreCase("imagen")) {
            int idImagen = Integer.parseInt(fromCliente[2]);
            sesion.getSalida().writeBoolean(service.deleteImagenById(idImagen));
            sesion.getSalida().flush();
        }
        return jsonFromServer;
    }

    private String suscripciones(String[] fromCliente) throws IOException {
        String eleccion = fromCliente[1];
        String jsonFromServer = "";
        Subscriptions paypal = new Subscriptions();
        switch (eleccion) {
            case "crear" -> jsonFromServer = crearSuscripcion(fromCliente, paypal);
            case "renovar" -> jsonFromServer = renovarSuscripcion(fromCliente, paypal);
            case "cancelar" -> jsonFromServer = cancelarSuscripcion(fromCliente, paypal);
        }
        return jsonFromServer;
    }

    private String cancelarSuscripcion(String[] fromCliente, Subscriptions paypal) throws IOException {
        String jsonFromServer;
        String idSuscripcion = fromCliente[2];
        Suscripcion s = service.findSuscripcionById(idSuscripcion);
        if (paypal.cancelSubscription(idSuscripcion)) {
            s.setRenovar("0");
        }
        jsonFromServer = gson.toJson(service.saveSuscripcion(s));
        return jsonFromServer;
    }

    private String renovarSuscripcion(String[] fromCliente, Subscriptions paypal) throws IOException {
        String jsonFromServer;
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
        return jsonFromServer;
    }

    private String crearSuscripcion(String[] fromCliente, Subscriptions paypal) throws IOException {
        Usuario u = sesion.getUsuario();
        String url = "";
        String jsonFromServer = "";
        if(fromCliente.length > 2) {
            url = paypal.crearSuscripcion(u, "desktop");
        } else {
            url = paypal.crearSuscripcion(u, "android");
        }
        sesion.getSalida().writeUTF(url);
        sesion.getSalida().flush();
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
        return jsonFromServer;
    }

    private String servicio(String[] fromCliente, int idUsuario) throws IOException {
        String option = fromCliente[1];
        int id = Integer.parseInt(fromCliente[2]);
        String jsonFromServer = "";
        if(option.equalsIgnoreCase("publicar")) {
            jsonFromServer = publicarServicio(id, "1");
        } else if (option.equalsIgnoreCase("archivar")){
            jsonFromServer = publicarServicio(id, "0");
        } else if (option.equalsIgnoreCase("isContratado")) {
            Contratacion c = service.findContratacionByServicioAndUsuario(id, idUsuario);
            sesion.getSalida().writeBoolean(c != null);
            sesion.getSalida().flush();
        } else if (option.equalsIgnoreCase("findAllByEtapa")) {
            Etapa e = service.findEtapaById(id);
            List<Servicio> servicios = service
                    .findAllServiciosByFechasAndTipoAndPais(
                            LocalDate.parse(e.getFechaInicio(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            LocalDate.parse(e.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            e.getTipo(),
                            e.getPais()
                    );
            jsonFromServer = gson.toJson(servicios);
        } else if (option.equalsIgnoreCase("contratar")) {
            int idEtapa = Integer.parseInt(fromCliente[2]);
            if(fromCliente.length > 4){
                return contratarServicio(id, idEtapa, "desktop");
            } else {
                return contratarServicio(id, idEtapa, "android");
            }
        }
        return jsonFromServer;
    }

    private String contratarServicio(int idServicio, int idEtapa, String tipoCliente) throws IOException {
        Checkout paypal = new Checkout();
        Servicio s = service.findServicioById(idServicio);
        Etapa e = service.findEtapaById(idEtapa);
        String url =  paypal.crearPedido(s, tipoCliente);
        sesion.getSalida().writeUTF(url);
        sesion.getSalida().flush();
        String idContratacion = sesion.getEntrada().readUTF();
        if(!idContratacion.equalsIgnoreCase("cancelar")) {
            Contratacion c = paypal.capturarPedido(idContratacion);
            c.setUsuario(sesion.getUsuario());
            c.getPago().setUsuario(sesion.getUsuario());
            c.setServicio(s);
            c.setEtapa(e);
            service.saveContratacion(c);
            return "" + e.getViaje().getId();
        }
        return "";
    }

    private String findAllEntities(String[] fromCliente, int idUsuario) {
        String tabla = fromCliente[1];
        String jsonFromServer = "";
        if (tabla.equalsIgnoreCase("tipoServicio")) {
            List<Tiposervicio> tiposServicio = service.findAllTiposServicio();
            jsonFromServer = gson.toJson(tiposServicio);
        } else if (tabla.equalsIgnoreCase("serviciosContratados")) {
            List<Servicio> servicios = service.findServiciosContratadosByUsuario(idUsuario);
            jsonFromServer = gson.toJson(servicios);
        }
        return jsonFromServer;
    }

    private String findEntityByServicioId(String[] fromCliente) throws IOException {
        String tabla = fromCliente[1];
        int idServicio = Integer.parseInt(fromCliente[2]);
        String jsonFromServer = "";
        if(tabla.equalsIgnoreCase("usuario")) {
            jsonFromServer = gson.toJson(service.findUsuarioByServicioId(idServicio));
        } else if (tabla.equalsIgnoreCase("resenas")) {
            List<Resena> resenas = service.findResenasByServicioId(idServicio);
            sesion.getSalida().writeUTF(gson.toJson(resenas));
            sesion.getSalida().flush();
            for (Resena r : resenas) {
                enviarFotoUsuario(r.getUsuario());
            }
        } else if (tabla.equalsIgnoreCase("imagen")) {
            String numImagenes = fromCliente[3];
            if (numImagenes.equals("one")) {
                Imagen i = service.findFirstImageFromServicioId(idServicio);
                sesion.getSalida().writeUTF(gson.toJson(i));
                sesion.getSalida().flush();
                if (i != null) {
                    enviarFoto(i.getImagen());
                }
            } else if (numImagenes.equals("all")) {
                List<Imagen> imagenes = service.findAllImagesFromServicioId(idServicio);
                sesion.getSalida().writeUTF(gson.toJson(imagenes));
                sesion.getSalida().flush();
                for (Imagen i : imagenes) {
                    enviarFoto(i.getImagen());
                }
            }
        }
        return jsonFromServer;
    }

    private void enviarFoto(byte[] i) throws IOException {
        sesion.getSalida().writeInt(i.length); // Envía la longitud del ByteArray
        sesion.getSalida().flush();
        sesion.getSalida().write(i); // Envía el ByteArray
        sesion.getSalida().flush();
    }

    private String findEntityByUserId(String[] fromCliente, int idUsuario) throws IOException {
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
            return gson.toJson(viajeActual);
        } else if (tabla.equalsIgnoreCase("suscripcion")) {
            Suscripcion suscripcion = service.findSuscripcionByUsuarioId(idUsuario);
            return gson.toJson(suscripcion);
        } else if (tabla.equalsIgnoreCase("servicio")) {
            List<Servicio> servicios = service.findServiciosByUsuarioId(idUsuario);
            jsonFromServer = gson.toJson(servicios);
        } else if (tabla.equalsIgnoreCase("imagen")) {
            int idOtroUsuario = Integer.parseInt(fromCliente[2]);
            enviarFotoUsuario(service.findUsuarioById(idOtroUsuario));
            return "";
        } else if (tabla.equalsIgnoreCase("mensajes")) {
            List<Mensaje> mensajes = service.findAllMensajesByUsuarioId(idUsuario);
            Map<Integer, Mensaje> conversaciones = new HashMap<>();
            for (Mensaje mensaje : mensajes) {
                int idOtroUsuario = mensaje.getEmisor().getId() == idUsuario ? mensaje.getReceptor().getId() : mensaje.getEmisor().getId();
                conversaciones.put(idOtroUsuario, mensaje);
            }
            jsonFromServer = gson.toJson(conversaciones);
        } else if (tabla.equalsIgnoreCase("mensajesBetweenUsers")) {
            int idOtroUsuario = Integer.parseInt(fromCliente[2]);
            List<Mensaje> mensajes = service.findAllMensajesBetweenUsers(idUsuario, idOtroUsuario);
            for (Mensaje m : mensajes) {
                m.setEmisor(service.findMinimalUserInfoById(m.getEmisor().getId()));
                m.setReceptor(service.findMinimalUserInfoById(m.getReceptor().getId()));
            }
            jsonFromServer = gson.toJson(mensajes);
        }
        return jsonFromServer;
    }

    private void chatear(int idOtroUsuario) throws IOException {
        String jsonFromUser;
        while (protocolo.procesarMensaje((jsonFromUser = sesion.getEntrada().readUTF())).equals("chat")) {
            Mensaje mensajeFromUser = gson.fromJson(jsonFromUser, Mensaje.class);
            mensajeFromUser.setEmisor(sesion.getUsuario());
            mensajeFromUser.setReceptor(service.findUsuarioById(idOtroUsuario));
            mensajeFromUser = service.saveMensaje(mensajeFromUser);
            sesion.getSalida().writeUTF(gson.toJson(mensajeFromUser));
            sesion.getSalida().flush();

            // -- ENVÍO DEL MENSAJE AL OTRO USUARIO DEL CHAT -- //
            HiloCliente hc = clientesConectados.get(idOtroUsuario);
            if(hc != null && hc.getProtocolo().estado == Estado.CHATEANDO) {
                hc.sesion.getSalida().writeUTF(gson.toJson(mensajeFromUser));
                sesion.getSalida().flush();
            }
        }

        sesion.getSalida().writeUTF("fin");
        sesion.getSalida().flush();
    }

    private void renovarSuscripcion(Suscripcion s) {
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

    private void recuperarContrasena(String email) throws IOException {
        Usuario u = service.findUsuarioByEmail(email);

        if(u != null) {
            int newPassword = (int) (Math.random() * 100000000);
            String passwordCifrada = Sha256Encryptor.cifrarTexto("" + newPassword);

            sesion.getSalida().writeBoolean(EmailSender.sendRecoveryEmail(email, "" + newPassword));
            sesion.getSalida().flush();

            u.setContrasena(passwordCifrada);
            service.saveUsuario(u);
        }

        sesionIniciada = true;
        terminar = true;
    }

    private String findByEntityId(String[] fromCliente) throws IOException {
        String tabla = fromCliente[1];
        String jsonFromServer = "";
        int idEntidad = Integer.parseInt(fromCliente[2]);
        if (tabla.equalsIgnoreCase("viaje")) {
            jsonFromServer = gson.toJson(service.findViajeById(idEntidad));
        }
        if (tabla.equalsIgnoreCase("servicio")) {
            jsonFromServer = gson.toJson(service.findServicioById(idEntidad));
        }
        if (tabla.equalsIgnoreCase("usuario")) {
            Usuario otroUsuario = service.findUsuarioById(idEntidad);
            sesion.getSalida().writeUTF(gson.toJson(otroUsuario));
            sesion.getSalida().flush();
            enviarFotoUsuario(otroUsuario);
            jsonFromServer = "";
        }
        return jsonFromServer;
    }

    private void enviarFotoUsuario(Usuario usuario) throws IOException {
        // Primero envío al usuario una confirmación de si tiene foto guardada o no
        boolean tieneFoto = usuario.getFoto() != null;
        sesion.getSalida().writeBoolean(tieneFoto);
        sesion.getSalida().flush();
        // Si la tiene, envía la foto
        if(tieneFoto) {
            enviarFoto(usuario.getFoto());
        }
    }

    private String publicarServicio(int idServicio, String number) {
        Servicio s = service.findServicioById(idServicio);
        s.setPublicado(number);
        s = service.saveServicio(s);
        return gson.toJson(s);
    }

    private String saveEntity(String[] fromCliente) throws IOException {
        String tabla = fromCliente[1];
        String jsonFromUser = sesion.getEntrada().readUTF();
        String jsonFromServer = "";
        if (tabla.equalsIgnoreCase("viaje")) {
            jsonFromServer = saveViaje(jsonFromUser);
        } else if (tabla.equalsIgnoreCase("etapa")) {
            int idViaje = Integer.parseInt(fromCliente[2]);
            jsonFromServer = saveEtapa(jsonFromUser, idViaje);
        } else if (tabla.equalsIgnoreCase("servicio")) {
            jsonFromServer = saveServicio(jsonFromUser);
        } else if (tabla.equalsIgnoreCase("resena")) {
            jsonFromServer = saveResena(jsonFromUser);
        }
        return jsonFromServer;
    }

    private String updateEntity(String[] fromCliente, int idUsuario) throws IOException {
        String tabla = fromCliente[1];
        String jsonFromUser = sesion.getEntrada().readUTF();
        String jsonFromServer = "";
        if (tabla.equalsIgnoreCase("usuario")) {
            jsonFromServer = saveUsuario(jsonFromUser);
        } else if (tabla.equalsIgnoreCase("viaje")) {
            jsonFromServer = saveViaje(jsonFromUser);
        } else if (tabla.equalsIgnoreCase("etapa")) {
            int idViaje = Integer.parseInt(fromCliente[2]);
            jsonFromServer = saveEtapa(jsonFromUser, idViaje);
        } else if (tabla.equalsIgnoreCase("servicio")) {
            jsonFromServer = saveServicio(jsonFromUser);
        } else if (tabla.equalsIgnoreCase("contrasena")) {
            String contrasenaActual = fromCliente[2];
            String contrasenaNueva = fromCliente[3];
            Usuario u = service.findUsuarioById(idUsuario);
            if (u.getContrasena().equals(contrasenaActual)) {
                u.setContrasena(contrasenaNueva);
                jsonFromServer = saveUsuario(gson.toJson(u));
            }
        }
        return jsonFromServer;
    }

    private String saveServicio(String jsonFromUser) {
        Servicio servicioFromUser = gson.fromJson(jsonFromUser, Servicio.class);
        servicioFromUser.setUsuario(sesion.getUsuario());
        servicioFromUser = service.saveServicio(servicioFromUser);
        return gson.toJson(servicioFromUser);
    }

    private String saveEtapa(String jsonFromUser, int idViaje) {
        Etapa etapaFromUser = gson.fromJson(jsonFromUser, Etapa.class);
        etapaFromUser.setViaje(service.findViajeById(idViaje));
        etapaFromUser = service.saveEtapa(etapaFromUser);
        return gson.toJson(etapaFromUser);
    }

    private String saveViaje(String jsonFromUser) {
        Viaje viajeFromUser = gson.fromJson(jsonFromUser, Viaje.class);
        viajeFromUser.setUsuario(sesion.getUsuario());
        viajeFromUser = service.saveViaje(viajeFromUser);
        return gson.toJson(viajeFromUser);
    }

    private String saveUsuario(String jsonFromUser) {
        Usuario usuarioFromUser = gson.fromJson(jsonFromUser, Usuario.class);
        usuarioFromUser.setRoles(sesion.getUsuario().getRoles());
        usuarioFromUser.setFoto(sesion.getUsuario().getFoto());
        usuarioFromUser = service.saveUsuario(usuarioFromUser);
        sesion.setUsuario(usuarioFromUser);
        return gson.toJson(usuarioFromUser);
    }

    private String saveResena(String jsonFromUser) {
        Resena resenaFromUser = gson.fromJson(jsonFromUser, Resena.class);
        resenaFromUser.setUsuario(sesion.getUsuario());
        resenaFromUser.setServicio(service.findServicioById(resenaFromUser.getId().getIdServicio()));
        System.out.println(service.findServicioById(resenaFromUser.getId().getIdServicio()));
        resenaFromUser = service.saveResena(resenaFromUser);
        return gson.toJson(resenaFromUser);
    }

    private String uploadFoto(String[] fromCliente, int idUsuario) throws IOException {
        String tabla = fromCliente[1];
        byte[] byteArray = leerFoto();
        String jsonFromServer = "";
        if (tabla.equalsIgnoreCase("usuario")) {
            Usuario u = service.findUsuarioById(idUsuario);
            u.setFoto(byteArray);
            jsonFromServer = saveUsuario(gson.toJson(u));
        } else if (tabla.equalsIgnoreCase("servicio")) {
            int idServicio = Integer.parseInt(fromCliente[2]);
            Servicio s = service.findServicioById(idServicio);
            s.getImagenes().add(new Imagen(byteArray, s));
            jsonFromServer = gson.toJson(service.saveServicio(s));
        }
        return jsonFromServer;
    }

    private byte[] leerFoto() throws IOException {
        int length = sesion.getEntrada().readInt(); // Lee el tamaño del array de bytes
        byte[] byteArray = new byte[length];
        sesion.getEntrada().readFully(byteArray); // Lee el array de bytes
        return byteArray;
    }

    private Usuario suspenderSuscripcion(Usuario u, Suscripcion s) {
        LocalDate fechaFin = LocalDate.parse(s.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(fechaFin.isBefore(LocalDate.now()) || fechaFin.isEqual(LocalDate.now())) { // Si la fecha de final es presente o pasada
            s.setEstado("INACTIVE"); // Se pone inactiva la suscripcion
            service.saveSuscripcion(s); // Se actualiza en la base de datos
            for (int i = 0; i < u.getRoles().size(); i++) {
                if(u.getRoles().get(i).getNombre().equals("Profesional")) {
                    u.getRoles().remove(i); // Se le quita el usuario el rol profesional
                }
            }
            u = service.saveUsuario(u); // Se actualiza el ususario en base de datos
        }
        return u;
    }

}
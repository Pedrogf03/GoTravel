package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.GoTravel;
import com.gotravel.Model.Rol;
import com.gotravel.Model.Suscripcion;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fonts;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class SuscribirseScreen implements Initializable {

    private static HttpServer server;

    @FXML
    private Text p;

    @FXML
    private Text p1;

    @FXML
    private Text p2;

    @FXML
    private Text p3;

    @FXML
    private Button subButton;

    @FXML
    private Text subtitle1;

    @FXML
    private Text subtitle2;

    @FXML
    private Text subtitle3;

    @FXML
    private Text subtitle4;

    @FXML
    private Text title;

    @FXML
    void navigateUp() throws IOException {
        GoTravel.setRoot("perfil");
    }

    @FXML
    void suscribirse() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            new Thread(() -> {
                Suscripcion s = findSuscripcionByUsuarioId();
                if(s != null) {
                    activarSuscripcion(s.getId());
                } else {
                    crearSuscripcion();
                }
            }).start();

        }

    }

    private void crearSuscripcion() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                new Thread(() -> {
                    try {

                        GoTravel.getSesion().getSalida().writeUTF("suscripcion;crear;desktop");
                        GoTravel.getSesion().getSalida().flush();

                        String url = GoTravel.getSesion().getEntrada().readUTF();

                        if(!url.isBlank()) {
                            Platform.runLater(() -> GoTravel.getHost().showDocument(url));

                            server = HttpServer.create(new InetSocketAddress(8080), 0);
                            server.createContext("/subscription_returnurl", new SubscriptionHandler());
                            server.createContext("/subscription_cancelurl", new CancelHandler());
                            server.setExecutor(null);
                            server.start();

                        }

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
                    }
                }).start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        title.setFont(Fonts.titleMedium);
        subtitle1.setFont(Fonts.titleSmall);
        subtitle2.setFont(Fonts.titleSmall);
        subtitle3.setFont(Fonts.titleSmall);
        subtitle4.setFont(Fonts.titleSmall);

        p.setFont(Fonts.labelMedium);
        p1.setFont(Fonts.labelMedium);
        p2.setFont(Fonts.labelMedium);
        p3.setFont(Fonts.labelMedium);

        subButton.setFont(Fonts.titleMedium);

    }

    private Suscripcion findSuscripcionByUsuarioId()  {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;suscripcion");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        return gson.fromJson(jsonFromServer, Suscripcion.class);

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        GoTravel.setRoot("landing");
                        return null;
                    }
                }).get();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

        return null;

    }

    private void activarSuscripcion(String subscriptionId)  {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                new Thread(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("suscripcion;renovar;" + subscriptionId);
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Suscripcion s = gson.fromJson(jsonFromServer, Suscripcion.class);

                        if (s != null) {
                            GoTravel.setRoot("suscripcion");
                        }

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
                    }
                }).start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

    }

    static class SubscriptionHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();
            String[] params = query.split("&");

            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length > 1 && keyValue[0].equals("subscription_id")) {
                    String subscriptionId = keyValue[1];

                    try {
                        new Thread(() -> {
                            Gson gson = new GsonBuilder()
                                    .serializeNulls()
                                    .setLenient()
                                    .create();

                            try {
                                GoTravel.getSesion().getSalida().writeUTF(subscriptionId);
                                GoTravel.getSesion().getSalida().flush();

                                String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                                Usuario usuario = gson.fromJson(jsonFromServer, Usuario.class);

                                if(usuario != null) {
                                    byte[] foto = GoTravel.getSesion().getUsuario().getFoto();
                                    usuario.setFoto(foto);
                                    for(Rol r : usuario.getRoles()) {
                                        if(r.getNombre().equalsIgnoreCase("profesional")) {
                                            usuario.setProfesional(true);
                                        } else if (r.getNombre().equalsIgnoreCase("administrador")) {
                                            usuario.setAdministrador(true);
                                        }
                                    }
                                    GoTravel.getSesion().setUsuario(usuario);
                                    GoTravel.setRoot("home");
                                }

                            } catch (IOException e) {
                                System.err.println(e.getMessage());
                                try {
                                    GoTravel.setRoot("landing");
                                } catch (IOException ex) {
                                    System.err.println(e.getMessage());
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }

                    server.stop(0);

                }
            }

        }

    }

    static class CancelHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            try {
                Executors.newSingleThreadExecutor().submit(() -> {

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("cancelar");
                        GoTravel.getSesion().getSalida().flush();

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
                    }
                }).get();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

            server.stop(0);

        }

    }

}

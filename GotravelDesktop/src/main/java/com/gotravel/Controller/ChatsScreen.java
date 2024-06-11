package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.ImageApi.ImageApi;
import com.gotravel.Model.Imagen;
import com.gotravel.Model.Mensaje;
import com.gotravel.Model.Servicio;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fonts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;

public class ChatsScreen implements Initializable {

    private Usuario u = GoTravel.getSesion().getUsuario();
    private Map<Integer, Mensaje> conversaciones;
    private List<Mensaje> mensajes;
    private Usuario otroUsuario;

    private boolean escuchando = false;

    @FXML
    private VBox chats;

    @FXML
    private VBox navPanel;

    @FXML
    private VBox mensajesChat;

    @FXML
    private Button serviciosButton;

    @FXML
    private TextField contenidoMensaje;

    @FXML
    void navigateToChats() throws IOException {
        pararEscuchaDeMensajes();
        GoTravel.setRoot("chats");
    }

    @FXML
    void navigateToHome() throws IOException {
        pararEscuchaDeMensajes();
        GoTravel.setRoot("home");
    }

    @FXML
    void navigateToPerfil() throws IOException {
        pararEscuchaDeMensajes();
        GoTravel.setRoot("perfil");
    }

    @FXML
    void navigateToServicios() throws IOException {
        pararEscuchaDeMensajes();
        GoTravel.setRoot("servicios");
    }

    @FXML
    void navigateToViajes() throws IOException {
        pararEscuchaDeMensajes();
        GoTravel.setRoot("viajes");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(!u.isProfesional()) {
            navPanel.getChildren().remove(serviciosButton);
        }

        conversaciones = findAllMensajesByUsuarioId();

        if(conversaciones != null) {

            mensajesChat.setPadding(new Insets(20, 20, 20, 20));

            for (Map.Entry<Integer, Mensaje> entry : conversaciones.entrySet()) {
                Mensaje mensaje = entry.getValue();
                Usuario usuarioAMostrar = mensaje.getEmisor().getId() == u.getId() ? mensaje.getReceptor() : mensaje.getEmisor();

                HBox hbox = new HBox(15);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setStyle("-fx-background-color: white; -fx-padding: 10;");
                hbox.setMaxWidth(Double.MAX_VALUE);
                HBox.setMargin(hbox, new Insets(10, 10, 10, 10));

                ImageView imageView = new ImageView();
                if(usuarioAMostrar.getFoto() != null) {
                    imageView.setImage(new Image(new ByteArrayInputStream(usuarioAMostrar.getFoto())));
                } else {
                    imageView = new ImageView(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("userNoFoto.png"))));
                }
                imageView.setFitHeight(60);
                imageView.setFitWidth(60);

                VBox vbox = new VBox(5);
                Label nombreLabel = new Label(usuarioAMostrar.getNombre() + " " + usuarioAMostrar.getApellidos());
                nombreLabel.setFont(Fonts.titleSmall);
                Label ultimoMensajeLabel = new Label(mensaje.getTexto());
                ultimoMensajeLabel.setFont(Fonts.labelSmall);
                vbox.getChildren().addAll(nombreLabel, ultimoMensajeLabel);

                hbox.getChildren().addAll(imageView, vbox);

                hbox.setCursor(Cursor.HAND);
                hbox.setOnMouseClicked(event -> {

                    chatear(usuarioAMostrar);

                });

                chats.getChildren().add(hbox);
            }

        } else {
            try {
                GoTravel.setRoot("landing");
            } catch (IOException e) {
                System.err.println(e.getCause() + ": " + e.getMessage());
            }
        }


    }

    public void chatear(Usuario usuarioAMostrar) {
        try {
            mensajesChat.getChildren().clear();

            pararEscuchaDeMensajes();

            otroUsuario = usuarioAMostrar;
            mensajes = findAllMensajesBetweenUsers(usuarioAMostrar.getId());

            for (Mensaje m : mensajes) {
                VBox vboxMensaje = new VBox(5);
                Label textoMensaje = new Label(m.getTexto());
                textoMensaje.setFont(Fonts.labelMedium);
                Label horaMensaje = new Label(m.getHora());
                horaMensaje.setFont(Fonts.labelSmall);
                vboxMensaje.getChildren().addAll(textoMensaje, horaMensaje);

                HBox hboxMensaje = new HBox();
                hboxMensaje.getChildren().add(vboxMensaje);

                if (m.getEmisor().getId() == u.getId()) {
                    hboxMensaje.setAlignment(Pos.CENTER_RIGHT);
                    vboxMensaje.setAlignment(Pos.CENTER_RIGHT);
                    mensajesChat.getChildren().add(hboxMensaje);
                } else {
                    vboxMensaje.setStyle("-fx-background-color: #3D5F90;");
                    mensajesChat.getChildren().add(hboxMensaje);
                }
            }

            iniciarEscuchaDeMensajes();
        } catch (InterruptedException e) {
            System.err.println(e.getCause() + " " + e.getMessage());
        }
    }

    private Map<Integer, Mensaje> findAllMensajesByUsuarioId() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;mensajes");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Type type = new TypeToken<Map<Integer, Mensaje>>() {}.getType();
                        Map<Integer, Mensaje> conversaciones = gson.fromJson(jsonFromServer, type);

                        for (Map.Entry<Integer, Mensaje> c : conversaciones.entrySet()) {
                            if (c.getValue().getEmisor().getId() == u.getId()) {
                                c.getValue().getReceptor().setFoto(getFotoByUserId(c.getValue().getReceptor().getId()));
                            } else {
                                c.getValue().getEmisor().setFoto(getFotoByUserId(c.getValue().getEmisor().getId()));
                            }
                        }

                        return conversaciones;

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

    private Usuario findUserById(int id) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findById;usuario;" + id);
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Usuario otroUsuario = gson.fromJson(jsonFromServer, Usuario.class);

                        if(GoTravel.getSesion().getEntrada().readBoolean()) {
                            int length = GoTravel.getSesion().getEntrada().readInt();
                            byte[] byteArray = new byte[length];
                            GoTravel.getSesion().getEntrada().readFully(byteArray);
                            otroUsuario.setFoto(byteArray);
                        }

                        GoTravel.getSesion().getSalida().writeUTF("chat;" + id);
                        GoTravel.getSesion().getSalida().flush();

                        return otroUsuario;

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

    private List<Mensaje> findAllMensajesBetweenUsers(int idOtroUsuario) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;mensajesBetweenUsers;" + idOtroUsuario);
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        System.out.println(jsonFromServer);
                        Type type = new TypeToken<List<Mensaje>>() {}.getType();

                        return gson.<List<Mensaje>>fromJson(jsonFromServer, type);

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

    private byte[] getFotoByUserId(int id) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;imagen;" + id);
                        GoTravel.getSesion().getSalida().flush();

                        if(GoTravel.getSesion().getEntrada().readBoolean()) {
                            int length = GoTravel.getSesion().getEntrada().readInt();
                            byte[] byteArray = new byte[length];
                            GoTravel.getSesion().getEntrada().readFully(byteArray);
                            return byteArray;
                        } else {
                            return null;
                        }

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

    @FXML
    void enviarMensaje() {

    }

    private synchronized void iniciarEscuchaDeMensajes() throws InterruptedException {
        new Thread(() -> {
            try {

                GoTravel.getSesion().getSalida().writeUTF("chat;" + otroUsuario.getId());
                GoTravel.getSesion().getSalida().flush();
                escuchando = true;

                while (escuchando && !Thread.currentThread().isInterrupted() && GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                    if(!jsonFromServer.equals("fin")) {
                        Mensaje mensajeFromServer = gson.fromJson(jsonFromServer, Mensaje.class);

                        if(mensajeFromServer.getReceptor().getId() == otroUsuario.getId() || mensajeFromServer.getEmisor().getId() == otroUsuario.getId()) {
                            final VBox vboxMensaje = new VBox(5);
                            final Label textoMensaje = new Label(mensajeFromServer.getTexto());
                            textoMensaje.setFont(Fonts.labelMedium);
                            final Label horaMensaje = new Label(mensajeFromServer.getHora());
                            horaMensaje.setFont(Fonts.labelSmall);
                            vboxMensaje.getChildren().addAll(textoMensaje, horaMensaje);

                            final HBox hboxMensaje = new HBox();
                            hboxMensaje.getChildren().add(vboxMensaje);

                            Platform.runLater(() -> {
                                if (mensajeFromServer.getEmisor().getId() == u.getId()) {
                                    hboxMensaje.setAlignment(Pos.CENTER_RIGHT);
                                    vboxMensaje.setAlignment(Pos.CENTER_RIGHT);
                                    mensajesChat.getChildren().add(hboxMensaje);
                                } else {
                                    vboxMensaje.setStyle("-fx-background-color: #3D5F90;");
                                    mensajesChat.getChildren().add(hboxMensaje);
                                }
                            });
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getCause() + " " + e.getMessage());
            }
        }).start();
    }

    private void pararEscuchaDeMensajes() {
        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {
            try {
                GoTravel.getSesion().getSalida().writeUTF("dejarChat");
                GoTravel.getSesion().getSalida().flush();
                escuchando = false;
            } catch (IOException e) {
                System.err.println(e.getCause() + " " + e.getMessage());
            }
        }
    }



}

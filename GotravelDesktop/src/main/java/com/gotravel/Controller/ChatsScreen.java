package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
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

    @FXML
    private FlowPane flowPane;

    @FXML
    private VBox navPanel;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button serviciosButton;

    @FXML
    void navigateToChats() throws IOException {
        GoTravel.setRoot("chats");
    }

    @FXML
    void navigateToHome() throws IOException {
        GoTravel.setRoot("home");
    }

    @FXML
    void navigateToPerfil() throws IOException {
        GoTravel.setRoot("perfil");
    }

    @FXML
    void navigateToServicios() throws IOException {
        GoTravel.setRoot("servicios");
    }

    @FXML
    void navigateToViajes() throws IOException {
        GoTravel.setRoot("viajes");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(!u.isProfesional()) {
            navPanel.getChildren().remove(serviciosButton);
        }

        conversaciones = findAllMensajesByUsuarioId();

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        if(conversaciones != null) {

            flowPane.setPadding(new Insets(20, 20, 20, 20));

            for (Map.Entry<Integer, Mensaje> entry : conversaciones.entrySet()) {
                Mensaje mensaje = entry.getValue();
                Usuario usuarioAMostrar = mensaje.getEmisor().getId() == u.getId() ? mensaje.getReceptor() : mensaje.getEmisor();

                HBox hbox = new HBox(15);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPrefSize(500, 100);
                hbox.setStyle("-fx-background-color: white; -fx-padding: 10;");

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

                    try {
                        ChatScreen.setOtroUsuario(usuarioAMostrar);
                        ChatScreen.setPrevScreen("chats");
                        GoTravel.setRoot("chat");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

                FlowPane.setMargin(hbox, new Insets(10, 10, 10, 10));
                flowPane.getChildren().add(hbox);
            }

        } else {
            try {
                GoTravel.setRoot("landing");
            } catch (IOException e) {
                System.err.println(e.getCause() + ": " + e.getMessage());
            }
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

    private byte[] getFotoByUserId(int id) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
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
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

        return null;
    }

}

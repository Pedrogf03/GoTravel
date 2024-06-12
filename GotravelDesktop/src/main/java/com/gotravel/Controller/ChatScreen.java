package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Mensaje;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fechas;
import com.gotravel.Utils.Fonts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.converter.DefaultStringConverter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ChatScreen implements Initializable {

    @Setter
    private static Usuario otroUsuario;
    private Usuario u = GoTravel.getSesion().getUsuario();
    private List<Mensaje> mensajes;
    private boolean escuchando = true;

    @Setter
    private static String prevScreen;

    @FXML
    private TextField mensajeField;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vboxMensajes;

    @FXML
    private ImageView userImg;

    @FXML
    private Text usernameText;

    @FXML
    void enviarMensaje() {

        Mensaje m = new Mensaje(mensajeField.getText(), LocalDate.now().format(Fechas.formatoFromDb), LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            new Thread(() -> {

                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .setLenient()
                        .create();

                    try {

                        GoTravel.getSesion().getSalida().writeUTF(gson.toJson(m));
                        GoTravel.getSesion().getSalida().flush();

                        mensajeField.setText("");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }).start();

        }

    }

    @FXML
    void navigateUp() throws IOException {
        pararEscuchaDeMensajes();
        GoTravel.setRoot(prevScreen);
    }

    private void pararEscuchaDeMensajes() {

        new Thread(() -> {

            try {

                GoTravel.getSesion().getSalida().writeUTF("dejarChat");
                GoTravel.getSesion().getSalida().flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        getMensajes();

        if(mensajes != null) {

            for(Mensaje m : mensajes) {
                VBox vbox = new VBox();
                vbox.setPadding(new Insets(10));

                Label texto = new Label(m.getTexto());
                texto.setFont(Fonts.labelMedium);

                Label hora = new Label(m.getHora());
                hora.setFont(Fonts.labelSmall);

                vbox.getChildren().addAll(texto, hora);

                HBox hbox = new HBox();
                hbox.getChildren().add(vbox);

                if (u.getId() == m.getEmisor().getId()) {
                    vbox.setStyle("-fx-background-color: #FFFFFF;");
                    texto.setStyle("-fx-text-fill: black;");
                    hora.setStyle("-fx-text-fill: black;");
                    hbox.setAlignment(Pos.CENTER_RIGHT);
                    vbox.setAlignment(Pos.CENTER_RIGHT);
                } else if (u.getId() == m.getReceptor().getId()) {
                    vbox.setStyle("-fx-background-color: #3D5F90;");
                    texto.setStyle("-fx-text-fill: white;");
                    hora.setStyle("-fx-text-fill: white;");
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    vbox.setAlignment(Pos.CENTER_LEFT);
                }

                vbox.setMinWidth(Region.USE_PREF_SIZE);
                vbox.setMinHeight(Region.USE_PREF_SIZE);
                vbox.setMaxWidth(Region.USE_PREF_SIZE);
                vbox.setMaxHeight(Region.USE_PREF_SIZE);

                vboxMensajes.getChildren().add(hbox);
            }

            Platform.runLater(() -> scrollPane.setVvalue(1.0));

            if(otroUsuario.getFoto() != null) {
                userImg.setImage(new Image(new ByteArrayInputStream(otroUsuario.getFoto())));
            } else {
                userImg.setImage(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("userNoFoto.png"))));
            }
            usernameText.setFont(Fonts.titleMedium);
            usernameText.setText(otroUsuario.getNombre());

        }

        mensajeField.setTextFormatter(new TextFormatter<>(new DefaultStringConverter(), "", change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 500) {
                return null;
            } else {
                return change;
            }
        }));

    }

    private void getMensajes() {

        List<Mensaje> m = findAllMensajesBetweenUsers();
        if(m != null) {
            mensajes = m;
            iniciarEscuchaDeMensajes();
        }

    }

    private void iniciarEscuchaDeMensajes() {

        new Thread(() -> {

            try {

                GoTravel.getSesion().getSalida().writeUTF("chat;" + otroUsuario.getId());
                GoTravel.getSesion().getSalida().flush();

                while (escuchando && Thread.currentThread().isAlive() && GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                    if(jsonFromServer.equals("fin")) {
                        escuchando = false;
                    } else {
                        Mensaje m = gson.fromJson(jsonFromServer, Mensaje.class);

                        if(m.getReceptor().getId() == u.getId() || m.getEmisor().getId() == u.getId()) {
                            Platform.runLater(() -> {
                                VBox vbox = new VBox();
                                vbox.setPadding(new Insets(10));

                                Label texto = new Label(m.getTexto());
                                texto.setFont(Fonts.labelMedium);

                                Label hora = new Label(m.getHora());
                                hora.setFont(Fonts.labelSmall);

                                vbox.getChildren().addAll(texto, hora);

                                HBox hbox = new HBox();
                                hbox.getChildren().add(vbox);

                                if (u.getId() == m.getEmisor().getId()) {
                                    vbox.setStyle("-fx-background-color: #FFFFFF;");
                                    texto.setStyle("-fx-text-fill: black;");
                                    hora.setStyle("-fx-text-fill: black;");
                                    hbox.setAlignment(Pos.CENTER_RIGHT);
                                    vbox.setAlignment(Pos.CENTER_RIGHT);
                                } else if (u.getId() == m.getReceptor().getId()) {
                                    vbox.setStyle("-fx-background-color: #3D5F90;");
                                    texto.setStyle("-fx-text-fill: white;");
                                    hora.setStyle("-fx-text-fill: white;");
                                    hbox.setAlignment(Pos.CENTER_LEFT);
                                    vbox.setAlignment(Pos.CENTER_LEFT);
                                }

                                vbox.setMinWidth(Region.USE_PREF_SIZE);
                                vbox.setMinHeight(Region.USE_PREF_SIZE);
                                vbox.setMaxWidth(Region.USE_PREF_SIZE);
                                vbox.setMaxHeight(Region.USE_PREF_SIZE);

                                vboxMensajes.getChildren().add(hbox);

                                scrollPane.setVvalue(1.0);

                            });
                        }

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

    }

    private List<Mensaje> findAllMensajesBetweenUsers() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;mensajesBetweenUsers;" + otroUsuario.getId());
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Type type = new TypeToken<List<Mensaje>>() {}.getType();
                        return gson.<List<Mensaje>>fromJson(jsonFromServer, type);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        GoTravel.setRoot("landing");
                        return null;
                    }
                }).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }

        return null;

    }

}

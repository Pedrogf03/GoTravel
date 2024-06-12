package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.GoTravel;
import com.gotravel.ImageApi.ImageApi;
import com.gotravel.Model.Usuario;
import com.gotravel.Model.Viaje;
import com.gotravel.Utils.Fonts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class HomeScreen implements Initializable {

    private Viaje viaje;

    private Usuario u = GoTravel.getSesion().getUsuario();

    @FXML
    private HBox buttons;

    @FXML
    private HBox containerTelefono;

    @FXML
    private Label email;

    @FXML
    private ImageView imageHome;

    @FXML
    private VBox infoUser;

    @FXML
    private VBox navPanel;

    @FXML
    private Label nombre;

    @FXML
    private Button nuevoServicio;

    @FXML
    private Button nuevoViaje;

    @FXML
    private Label rol;

    @FXML
    private Button serviciosButton;

    @FXML
    private Label tfno;

    @FXML
    private Label titleViajeMostrar;

    @FXML
    private ImageView userFoto;

    @FXML
    private VBox viajeMostrar;

    @FXML
    void crearServicio() throws IOException {
        GoTravel.setRoot("crearServicio");
    }

    @FXML
    void crearViaje() throws IOException {
        GoTravel.setRoot("crearViaje");
    }

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
            buttons.getChildren().remove(nuevoServicio);
        }

        viaje = findViajeActualByUsuarioId();

        if(viaje != null) {
            titleViajeMostrar.setText("Viaje en curso");
        } else {
            viaje = findProximoViajeByUsuarioId();
            if(viaje != null) titleViajeMostrar.setText("Viaje en curso");
        }

        if(viaje != null) {
            titleViajeMostrar.setFont(Fonts.titleMedium);

            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(10));

            Label nombreLabel = new Label(viaje.getNombre());
            nombreLabel.setFont(Fonts.titleSmall);
            nombreLabel.setStyle("-fx-text-fill: #ffffff;");

            Label fechasLabel = new Label(viaje.inicio() + " - " + viaje.fin());
            fechasLabel.setFont(Fonts.labelMedium);
            fechasLabel.setStyle("-fx-text-fill: #ffffff;");

            Label etapasLabel = new Label("Etapas: " + viaje.getEtapas().size());
            etapasLabel.setFont(Fonts.labelMedium);
            etapasLabel.setStyle("-fx-text-fill: #ffffff;");

            vbox.getChildren().addAll(nombreLabel, fechasLabel, etapasLabel);

            if (viaje.getDescripcion() != null && !viaje.getDescripcion().isEmpty()) {
                Label descripcionLabel = new Label(viaje.getDescripcion());
                descripcionLabel.setFont(Fonts.labelMedium);
                descripcionLabel.setStyle("-fx-text-fill: #ffffff;");
                vbox.getChildren().add(1, descripcionLabel);
            }

            viajeMostrar.getChildren().add(vbox);
        } else {
            titleViajeMostrar.setFont(Fonts.titleSmall);
            titleViajeMostrar.setText("No tienes ningún viaje planificado");
        }

        if(u.getFoto() != null){
            userFoto.setImage(new Image(new ByteArrayInputStream(u.getFoto())));
        } else {
            userFoto.setImage(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("userNoFoto.png"))));
        }

        userFoto.setFitHeight(200);
        userFoto.setFitWidth(200);
        userFoto.setPreserveRatio(false);

        if (u.getApellidos() != null) {
            nombre.setText(u.getNombre() + "\n" + u.getApellidos());
        } else {
            nombre.setText(u.getNombre());
        }
        nombre.setFont(Fonts.labelMedium);

        email.setText(u.getEmail());
        email.setFont(Fonts.labelMedium);

        if(!u.getTfno().isBlank()) {
            tfno.setText(u.getTfno());
            tfno.setFont(Fonts.labelMedium);
        } else {
            infoUser.getChildren().remove(containerTelefono);
        }

        rol.setText(u.getRoles().get(0).getNombre());
        rol.setFont(Fonts.labelMedium);

        nuevoServicio.setFont(Fonts.titleMedium);
        nuevoViaje.setFont(Fonts.titleMedium);

        try {
            imageHome.setImage(new Image(new ByteArrayInputStream(Objects.requireNonNull(ImageApi.getImage()))));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Viaje findProximoViajeByUsuarioId() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;proximoViaje");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        return gson.fromJson(jsonFromServer, Viaje.class);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        GoTravel.setRoot("landing");
                        return null;
                    }
                }).get();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return null;
            }

        } else {
            return null;
        }

    }

    private Viaje findViajeActualByUsuarioId() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;viajeActual");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        return gson.fromJson(jsonFromServer, Viaje.class);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        GoTravel.setRoot("landing");
                        return null;
                    }
                }).get();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return null;
            }

        } else {
            return null;
        }

    }

}

package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Imagen;
import com.gotravel.Model.Servicio;
import com.gotravel.Model.Viaje;
import com.gotravel.Utils.Fechas;
import com.gotravel.Utils.Fonts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class ViajesScreen implements Initializable {

    private static String busqueda = "";
    private List<Viaje> viajesFinalizados = new ArrayList<>();
    private List<Viaje> viajesActuales = new ArrayList<>();

    private List<Viaje> viajes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(!GoTravel.getSesion().getUsuario().isProfesional()) {
            navPanel.getChildren().remove(serviciosButton);
        }

        try {
            List<Viaje> allViajes = findViajesByUsuarioId();
            if(allViajes != null) {
                if(busqueda != null) {
                    for(Viaje v : allViajes) {
                        if(v.getNombre().toLowerCase().contains(busqueda.toLowerCase())) {
                            if(LocalDate.parse(v.fin(), Fechas.formatoFinal).isBefore(LocalDate.now())) {
                                viajesFinalizados.add(v);
                            } else {
                                viajesActuales.add(v);
                            }
                        }
                    }
                } else {
                    for(Viaje v : allViajes) {
                        if(LocalDate.parse(v.fin(), Fechas.formatoFinal).isBefore(LocalDate.now())) {
                            viajesFinalizados.add(v);
                        } else {
                            viajesActuales.add(v);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            try {
                GoTravel.setRoot("landing");
            } catch (IOException ex) {
                System.err.println(e.getMessage());
            }
        }

        viajes = viajesActuales;

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        filtro.getItems().addAll("Actuales", "Finalizados");
        filtro.setValue("Actuales");

        filtro.setStyle("-fx-font-size: 15; -fx-background-color: #ffffff;");

        botonFiltro.setFont(Fonts.labelMedium);

        mostrarViajes();

    }

    private List<Viaje> findViajesByUsuarioId() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;viaje");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Type type = new TypeToken<List<Viaje>>() {}.getType();
                        return gson.<List<Viaje>>fromJson(jsonFromServer, type);

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

    private void mostrarViajes() {

        flowPane.getChildren().clear();

        for(Viaje v : viajes) {
            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPrefSize(350, 200);
            vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            vbox.setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");
            vbox.setCursor(Cursor.HAND);

            Label nombreLabel = new Label(v.getNombre());
            nombreLabel.setFont(Fonts.labelMedium);
            Label fechasLabel = new Label();
            if(v.fin().isBlank()) {
                fechasLabel.setText(v.inicio());
            } else {
                fechasLabel.setText(v.inicio() + " - " + v.fin());
            }
            fechasLabel.setFont(Fonts.labelSmall);

            vbox.getChildren().addAll(nombreLabel, fechasLabel);

            flowPane.getChildren().add(vbox);
            FlowPane.setMargin(vbox, new Insets(10, 10, 10, 10));
            vbox.setOnMouseClicked(event -> {
                try {
                    ViajeScreen.setViajeId(v.getId());
                    GoTravel.setRoot("viaje");
                } catch (IOException e) {
                    System.err.println(e.getCause() + ": " + e.getMessage());
                }
            });
        }
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

    @FXML
    private Button botonFiltro;

    @FXML
    private TextField buscador;

    @FXML
    private ChoiceBox<String> filtro;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox navPanel;

    @FXML
    private Button serviciosButton;

    @FXML
    void buscarViajes() throws IOException {
        busqueda = buscador.getText();
        GoTravel.setRoot("viajes");
    }

    @FXML
    void filtrar() {
        if (filtro.getValue().equals("Finalizados")) {
            viajes = viajesFinalizados;
        } else if (filtro.getValue().equals("Actuales")) {
            viajes = viajesActuales;
        }
        mostrarViajes();
    }

}

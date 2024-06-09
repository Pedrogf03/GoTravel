package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.GoTravel;
import com.gotravel.Model.Etapa;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class ViajeScreen implements Initializable {

    @Setter
    private static int viajeId;
    private Viaje v;

    @FXML
    private Text descViaje;

    @FXML
    private Text fechasViaje;

    @FXML
    private Text nombreViaje;

    @FXML
    private Text precioViaje;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private HBox hboxEtapas;

    @FXML
    private Button editarViajeButton;

    @FXML
    private Button nuevaEtapaButton;

    @FXML
    void navigateUp() throws IOException {
        GoTravel.setRoot("viajes");
    }

    private void mostrarEtapas() {

        hboxEtapas.getChildren().clear();

        LocalDate fechaActual = LocalDate.now();

        for(Etapa e : v.getEtapas()) {
            LocalDate fechaInicio = LocalDate.parse(e.getFechaInicio(), Fechas.formatoFromDb);
            LocalDate fechaFinal = LocalDate.parse(e.getFechaFinal(), Fechas.formatoFromDb);

            VBox vboxEtapa = new VBox();
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER);

            boolean esEtapaActual = !fechaActual.isBefore(fechaInicio) && !fechaActual.isAfter(fechaFinal);
            if (esEtapaActual) {
                hbox.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 10;");
            } else {
                hbox.setStyle("-fx-background-color: #3D5F90; -fx-padding: 10;");
            }

            VBox vboxInfo = new VBox();
            vboxInfo.getChildren().add(new Label(e.getNombre()));
            vboxInfo.getChildren().add(new Label("Tipo " + e.getTipo()));
            vboxInfo.getChildren().add(new Label(fechaInicio + " - " + fechaFinal));
            vboxInfo.getChildren().add(new Label("Precio total: " + e.getCosteTotal() + "€"));
            vboxInfo.getChildren().forEach(nodo -> {
                if(nodo instanceof Label) {
                    ((Label)nodo).setFont(Fonts.labelMedium);
                    ((Label)nodo).setTextFill(esEtapaActual ? Color.BLACK : Color.WHITE);
                }
            });

            Button btnImagen = new Button();
            if (esEtapaActual) {
                btnImagen.setStyle("-fx-background-color: #3D5F90; -fx-cursor: hand;");
            } else {
                btnImagen.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            }
            ImageView imageView = new ImageView(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("arrow-down-solid.png"))));
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);
            btnImagen.setGraphic(imageView);

            hbox.getChildren().addAll(vboxInfo, btnImagen);
            vboxEtapa.getChildren().add(hbox);
            VBox.setMargin(hbox, new Insets(0, 10, 0, 10));

            hboxEtapas.getChildren().add(vboxEtapa);
        }

        if(LocalDate.parse(v.fin(), Fechas.formatoFinal).isBefore(fechaActual)) {
            editarViajeButton.setDisable(true);
            editarViajeButton.setVisible(false);
            nuevaEtapaButton.setDisable(true);
            nuevaEtapaButton.setVisible(false);
        } else {
            editarViajeButton.setFont(Fonts.titleSmall);
            nuevaEtapaButton.setFont(Fonts.titleSmall);
        }

    }

    @FXML
    void editarViaje() throws IOException {
        CrearViajeScreen.setViaje(v);
        GoTravel.setRoot("crearViaje");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        v = findViajeById();

        if(v != null) {

            nombreViaje.setText(v.getNombre());
            descViaje.setText(v.getDescripcion());
            precioViaje.setText("Precio total: " + v.getCosteTotal() + "€");
            fechasViaje.setText("Desde el " + v.inicio() + " hasta el " + v.fin());

            nombreViaje.setFont(Fonts.titleMedium);
            descViaje.setFont(Fonts.labelMedium);
            precioViaje.setFont(Fonts.labelMedium);
            fechasViaje.setFont(Fonts.labelMedium);

            mostrarEtapas();

        } else {
            try {
                GoTravel.setRoot("landing");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }


    }

    private Viaje findViajeById() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findById;viaje;" + viajeId);
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
            }

        }

        return null;

    }
}

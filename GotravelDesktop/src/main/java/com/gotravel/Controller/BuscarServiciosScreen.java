package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Imagen;
import com.gotravel.Model.Servicio;
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
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class BuscarServiciosScreen implements Initializable {

    private static String busqueda = "";

    @Setter
    private static int etapaId;

    private List<Servicio> servicios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            servicios = new ArrayList<>();
            if(!busqueda.isBlank()) {
                for (Servicio s : getServicios()) {
                    if(s.getNombre().contains(busqueda)) {
                        servicios.add(s);
                    }
                }
            } else {
                servicios.addAll(getServicios());
            }
        } catch (NullPointerException e) {
            try {
                GoTravel.setRoot("landing");
            } catch (IOException ex) {
                System.err.println(e.getMessage());
            }
        }

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        mostrarServicios();

    }

    private List<Servicio> getServicios() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("servicio;findAllByEtapa;" + etapaId);
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Type type = new TypeToken<List<Servicio>>() {}.getType();

                        List<Servicio> servicios = gson.fromJson(jsonFromServer, type);

                        for(Servicio s : servicios) {
                            s.setImagenes(new ArrayList<>());
                            s.getImagenes().add(getFirstImagenFromServicio(s.getId()));
                        }

                        return servicios;

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

    private Imagen getFirstImagenFromServicio(int id) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByServicioId;imagen;" + id + ";one");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Imagen imagen = gson.fromJson(jsonFromServer, Imagen.class);

                        if(imagen != null) {
                            int length = GoTravel.getSesion().getEntrada().readInt();
                            byte[] byteArray = new byte[length];
                            GoTravel.getSesion().getEntrada().readFully(byteArray);
                            imagen.setImagen(byteArray);
                        }

                        return imagen;

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

    private void mostrarServicios() {

        flowPane.getChildren().clear();

        for(Servicio s : servicios) {
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setPrefSize(600, 200);
            hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            hbox.setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");
            hbox.setCursor(Cursor.HAND);

            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(s.getImagenes().get(0).getImagen())));
            imageView.setFitHeight(240);
            imageView.setFitWidth(240);
            imageView.setPreserveRatio(false);

            VBox vbox = new VBox(5);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPrefWidth(360);

            Label nombreLabel = new Label(s.getNombre());
            nombreLabel.setFont(Fonts.labelMedium);
            Label fechasLabel = new Label();
            if(s.getFinal().isBlank()) {
                fechasLabel.setText(s.getInicio() + " - " + s.getHora());
            } else {
                fechasLabel.setText(s.getInicio() + " - " + s.getFinal());
            }
            fechasLabel.setFont(Fonts.labelSmall);

            vbox.getChildren().addAll(nombreLabel, fechasLabel);

            hbox.getChildren().addAll(imageView, vbox);

            flowPane.getChildren().add(hbox);
            FlowPane.setMargin(hbox, new Insets(10, 10, 10, 10));
            hbox.setOnMouseClicked(event -> {
                ServicioScreen.setServicioId(s.getId());
                try {
                    ServicioScreen.setEtapaId(etapaId);
                    GoTravel.setRoot("servicio");
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            });
        }
    }

    @FXML
    private TextField buscador;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    void buscarServicios() throws IOException {
        busqueda = buscador.getText();
        GoTravel.setRoot("servicios");
    }

}

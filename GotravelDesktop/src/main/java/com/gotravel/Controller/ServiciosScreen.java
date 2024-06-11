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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;


public class ServiciosScreen implements Initializable {

    private static String busqueda = "";
    private List<Servicio> serviciosPublicos = new ArrayList<>();
    private List<Servicio> serviciosOcultos = new ArrayList<>();

    private List<Servicio> servicios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            if(!busqueda.isBlank()) {
                for (Servicio s : getServicios()) {
                    if(s.getNombre().contains(busqueda)) {
                        if(s.getPublicado().equalsIgnoreCase("0")) {
                            serviciosOcultos.add(s);
                        } else {
                            serviciosPublicos.add(s);
                        }
                    }
                }
            } else {
                for (Servicio s : getServicios()) {
                    if(s.getPublicado().equalsIgnoreCase("0")) {
                        serviciosOcultos.add(s);
                    } else {
                        serviciosPublicos.add(s);
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

        servicios = serviciosPublicos;

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        filtro.getItems().addAll("Publicados", "Ocultos");
        filtro.setValue("Publicados");

        filtro.setStyle("-fx-font-size: 15; -fx-background-color: #ffffff;");

        botonFiltro.setFont(Fonts.labelMedium);

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
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;servicio");
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

            ImageView imageView;
            if(s.getImagenes().get(0) != null) {
                imageView = new ImageView(new Image(new ByteArrayInputStream(s.getImagenes().get(0).getImagen())));
            } else {
                imageView = new ImageView(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("imageNotFound.jpg"))));
            }

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
                    GoTravel.setRoot("servicio");
                } catch (IOException e) {
                    System.err.println(e.getCause() + ": " + e.getMessage());
                }
            });
        }
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
    void buscarServicios() throws IOException {
        busqueda = buscador.getText();
        GoTravel.setRoot("servicios");
    }

    @FXML
    void filtrar() {
        if (filtro.getValue().equals("Publicados")) {
            servicios = serviciosPublicos;
        } else if (filtro.getValue().equals("Ocultos")) {
            servicios = serviciosOcultos;
        }
        mostrarServicios();
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

}

package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Imagen;
import com.gotravel.Model.Servicio;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fonts;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class AdminPanelServicios implements Initializable {

    @Setter
    private static Usuario usuario;
    @Setter
    private static String busqueda = "";
    private List<Servicio> servicios = new ArrayList<>();

    @FXML
    private TextField buscador;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    void buscarServicios() throws IOException {
        busqueda = buscador.getText();
        GoTravel.setRoot("adminPanelServicios");
    }

    @FXML
    void navigateUp() throws IOException {
        GoTravel.setRoot("adminPanel");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(!busqueda.isBlank()) {
            for (Servicio s : getServiciosByUser()) {
                if(s.getNombre().contains(busqueda)) {
                    servicios.add(s);
                }
            }
        } else {
            servicios = getServiciosByUser();
        }

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        mostrarServicios();

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
            if(s.getImagenes().get(0) == null){
                imageView = new ImageView(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("imageNotFound.jpg"))));
            } else {
                imageView = new ImageView(new Image(new ByteArrayInputStream(s.getImagenes().get(0).getImagen())));
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

            Button ocultarButton = getButton(s);

            vbox.getChildren().add(ocultarButton);

            hbox.getChildren().addAll(imageView, vbox);

            flowPane.getChildren().add(hbox);
            FlowPane.setMargin(hbox, new Insets(10, 10, 10, 10));
            hbox.setOnMouseClicked(event -> {
                try {
                    ServicioScreen.setServicioId(s.getId());
                    ServicioScreen.setPrevScreen("adminPanelServicios");
                    GoTravel.setRoot("servicio");
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            });
        }
    }

    private Button getButton(Servicio s) {
        Button ocultarButton = new Button(s.getOculto().equals("0") ? "Ocultar" : "Devolver acceso");
        ocultarButton.setFont(Fonts.titleSmall);
        ocultarButton.setStyle("-fx-background-color: #3D5F90; -fx-text-fill: #ffffff;");
        ocultarButton.setOnAction(event -> {

            ocultarServicio(s);

        });
        return ocultarButton;
    }

    private void ocultarServicio(Servicio s) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                new Thread(() -> {

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        if(s.getOculto().equals("0")){
                            s.setOculto("1");
                        } else {
                            s.setOculto("0");
                        }
                        s.setImagenes(null);

                        GoTravel.getSesion().getSalida().writeUTF("update;servicio;" + usuario.getId());
                        GoTravel.getSesion().getSalida().flush();

                        GoTravel.getSesion().getSalida().writeUTF(gson.toJson(s));
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Servicio servicio = gson.fromJson(jsonFromServer, Servicio.class);

                        if(servicio != null) {
                            busqueda = "";
                            GoTravel.setRoot("adminPanelServicios");
                        }

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }


    }

    private List<Servicio> getServiciosByUser() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;servicio;" + usuario.getId());
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
}

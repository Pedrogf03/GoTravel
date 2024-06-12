package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Resena;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fonts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
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

public class AdminPanelResenas implements Initializable {

    @Setter
    private static Usuario usuario;
    private List<Resena> resenas = new ArrayList<>();

    @FXML
    private FlowPane flowPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    void navigateUp() throws IOException {
        GoTravel.setRoot("adminPanel");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        resenas = getResenas();

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        mostrarResenas();

    }

    private void mostrarResenas() {

        flowPane.getChildren().clear();

        for (Resena resena : resenas) {

            VBox vboxResena = new VBox();
            vboxResena.setStyle("-fx-background-color: white; -fx-padding: 10;");
            vboxResena.setMaxSize(500, 200);
            vboxResena.setMinSize(500, 200);
            vboxResena.setPrefSize(500, 200);
            vboxResena.setAlignment(Pos.CENTER);

            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            ImageView imageView = new ImageView();
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);

            if (resena.getUsuario().getFoto() != null) {
                imageView.setImage(new Image(new ByteArrayInputStream(resena.getUsuario().getFoto())));
            } else {
                imageView.setImage(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("userNoFoto.png"))));
            }

            Text nombreUsuario = new Text(resena.getUsuario().getNombre());
            nombreUsuario.setFont(Fonts.labelMedium);

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            HBox hboxPuntuacion = new HBox();
            hboxPuntuacion.setAlignment(Pos.CENTER_RIGHT);

            for (int i = 0; i < resena.getPuntuacion(); i++) {
                ImageView estrella = new ImageView(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("star-solid.png"))));
                estrella.setFitHeight(20);
                estrella.setFitWidth(20);
                hboxPuntuacion.getChildren().add(estrella);
            }

            hbox.getChildren().addAll(imageView, nombreUsuario, region, hboxPuntuacion);

            VBox vboxContenido = new VBox();
            Text textoContenido = new Text(resena.getContenido());
            textoContenido.setFont(Fonts.labelMedium);
            vboxContenido.getChildren().add(textoContenido);

            Button ocultarButton = getButton(resena);

            vboxResena.getChildren().addAll(hbox, vboxContenido, ocultarButton);

            flowPane.getChildren().add(vboxResena);
        }

    }

    private Button getButton(Resena r) {
        Button ocultarButton = new Button(r.getOculto().equals("0") ? "Ocultar" : "Mostrar");
        ocultarButton.setFont(Fonts.titleSmall);
        ocultarButton.setStyle("-fx-background-color: #3D5F90; -fx-text-fill: #ffffff;");
        ocultarButton.setOnAction(event -> {

            ocultarResena(r);

        });
        return ocultarButton;

    }

    private void ocultarResena(Resena r) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                new Thread(() -> {

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        if(r.getOculto().equals("0")){
                            r.setOculto("1");
                        } else {
                            r.setOculto("0");
                        }

                        GoTravel.getSesion().getSalida().writeUTF("update;resena");
                        GoTravel.getSesion().getSalida().flush();

                        GoTravel.getSesion().getSalida().writeUTF(gson.toJson(r));
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Resena resena = gson.fromJson(jsonFromServer, Resena.class);

                        if(resena != null) {
                            GoTravel.setRoot("adminPanelResenas");
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

    private List<Resena> getResenas() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;resena;" + usuario.getId());
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Type type = new TypeToken<List<Resena>>() {}.getType();

                        List<Resena> resenas = gson.fromJson(jsonFromServer, type);

                        for(Resena r : resenas) {
                            boolean usuarioTieneFoto = GoTravel.getSesion().getEntrada().readBoolean();
                            if(usuarioTieneFoto) {
                                int length = GoTravel.getSesion().getEntrada().readInt();
                                byte[] foto = new byte[length];
                                GoTravel.getSesion().getEntrada().readFully(foto);
                                r.getUsuario().setFoto(foto);
                            }
                        }

                        return resenas;

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

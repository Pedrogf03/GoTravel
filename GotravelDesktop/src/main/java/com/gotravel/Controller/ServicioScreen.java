package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.*;
import com.gotravel.Utils.Fonts;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class ServicioScreen implements Initializable {

    @Setter
    private static int servicioId;
    private Servicio s;
    private int fotoActual = 0;
    private int etapaId;

    private static HttpServer server;

    @FXML
    private HBox botones;

    @FXML
    private Button addFotoButton;

    @FXML
    private Button editarButton;

    @FXML
    private Button ocultarButton;

    @FXML
    private Button publicarButton;

    @FXML
    private Button chatearButton;

    @FXML
    private Button contratarButton;

    @FXML
    private Label descripcionServicio;

    @FXML
    private Label fechasServicio;

    @FXML
    private ImageView imagenServicio;

    @FXML
    private Label nombreServicio;

    @FXML
    private Label precioServicio;

    @FXML
    private Label tipoServicio;

    @FXML
    private Label direccionServicio;

    @FXML
    private Button anteriorButton;

    @FXML
    private Button siguienteButton;

    @FXML
    void anteriorFoto() {
        fotoActual--;
        if(fotoActual < 0) {
            fotoActual = s.getImagenes().size()-1;
        }
        imagenServicio.setImage(new Image(new ByteArrayInputStream(s.getImagenes().get(fotoActual).getImagen())));
    }

    @FXML
    void siguienteFoto() {
        fotoActual++;
        if(fotoActual >= s.getImagenes().size()) {
            fotoActual = 0;
        }
        imagenServicio.setImage(new Image(new ByteArrayInputStream(s.getImagenes().get(fotoActual).getImagen())));
    }

    @FXML
    void navigateUp() throws IOException {
        GoTravel.setRoot("servicios");
    }

    @FXML
    private VBox content;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        s = findServicioById();

        if(!s.getImagenes().isEmpty()) {
            imagenServicio.setImage(new Image(new ByteArrayInputStream(s.getImagenes().get(0).getImagen())));
        } else {
            siguienteButton.setDisable(true);
            siguienteButton.setVisible(false);

            anteriorButton.setDisable(true);
            anteriorButton.setVisible(false);
        }

        nombreServicio.setFont(Fonts.titleMedium);
        nombreServicio.setText(s.getNombre());

        descripcionServicio.setFont(Fonts.labelMedium);
        descripcionServicio.setText(s.getDescripcion());

        fechasServicio.setFont(Fonts.labelMedium);
        fechasServicio.setText(s.getInicio() + " - " + (s.getFinal().isBlank() ? s.getHora() : s.getFinal()));

        tipoServicio.setFont(Fonts.labelMedium);
        tipoServicio.setText(s.getTipoServicio().getNombre());

        precioServicio.setFont(Fonts.labelMedium);
        precioServicio.setText(s.getPrecio() + "€");

        direccionServicio.setFont(Fonts.labelMedium);
        Direccion d = s.getDireccion();
        direccionServicio.setText(d.getLinea1() + ", " + (d.getLinea2().isBlank() ? "" : d.getLinea2()) + d.getCiudad() + ", " + d.getEstado() + ", " + d.getPais() + ", " + d.getCp());

        if(GoTravel.getSesion().getUsuario().getId() == s.getUsuario().getId()) {

            botones.getChildren().remove(contratarButton);
            botones.getChildren().remove(chatearButton);

            if(s.getPublicado().equals("0")) {
                botones.getChildren().remove(ocultarButton);
            } else {
                botones.getChildren().remove(publicarButton);
            }

        } else {

            botones.getChildren().remove(addFotoButton);
            botones.getChildren().remove(editarButton);
            botones.getChildren().remove(ocultarButton);
            botones.getChildren().remove(publicarButton);

        }

        for(Node node : botones.getChildren()) {
            if(node instanceof Button) {
                ((Button)node).setFont(Fonts.labelMedium);
            }
        }

        for(Resena resena : s.getResenas()) {

            VBox vboxResena = new VBox();
            vboxResena.setStyle("-fx-background-color: white; -fx-padding: 10;");
            vboxResena.setMaxWidth(600);
            vboxResena.setAlignment(Pos.CENTER);

            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            ImageView imageView = new ImageView();
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);

            if(resena.getUsuario().getFoto() != null) {
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

            for(int i = 0; i < resena.getPuntuacion(); i++) {
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

            vboxResena.getChildren().addAll(hbox, vboxContenido);

            content.getChildren().add(vboxResena);
        }

    }

    private Servicio findServicioById() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findById;servicio;" + servicioId);
                        GoTravel.getSesion().getSalida().flush();

                        String servicioFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Servicio servicio = gson.fromJson(servicioFromServer, Servicio.class);

                        if(servicio != null) {
                            servicio.setImagenes(getAllImagenesFromServicio());
                            servicio.setContratado(isServicioContratado());

                            GoTravel.getSesion().getSalida().writeUTF("findByServicioId;usuario;" + servicioId);
                            GoTravel.getSesion().getSalida().flush();

                            String usuarioFromServer = GoTravel.getSesion().getEntrada().readUTF();
                            Usuario usuario = gson.fromJson(usuarioFromServer, Usuario.class);

                            if(usuario != null) {
                                servicio.setUsuario(usuario);
                            }

                            GoTravel.getSesion().getSalida().writeUTF("findByServicioId;resenas;" + servicioId);
                            GoTravel.getSesion().getSalida().flush();

                            String resenasFromServer = GoTravel.getSesion().getEntrada().readUTF();
                            Type type = new TypeToken<List<Resena>>() {}.getType();
                            List<Resena> resenas = gson.fromJson(resenasFromServer, type);

                            for(Resena r : resenas) {
                                boolean usuarioTieneFoto = GoTravel.getSesion().getEntrada().readBoolean();
                                if(usuarioTieneFoto) {
                                    int length = GoTravel.getSesion().getEntrada().readInt();
                                    byte[] foto = new byte[length];
                                    GoTravel.getSesion().getEntrada().readFully(foto);
                                    r.getUsuario().setFoto(foto);
                                }
                            }

                            servicio.setResenas(resenas);

                            return servicio;
                        }
                        return null;

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

    private boolean isServicioContratado() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {

                    try {

                        GoTravel.getSesion().getSalida().writeUTF("servicio;isContratado;" + servicioId);
                        GoTravel.getSesion().getSalida().flush();

                        return GoTravel.getSesion().getEntrada().readBoolean();

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        GoTravel.setRoot("landing");
                        return false;
                    }

                }).get();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

        return false;

    }

    private List<Imagen> getAllImagenesFromServicio() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        GoTravel.getSesion().getSalida().writeUTF("findByServicioId;imagen;" + servicioId + ";all");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Type type = new TypeToken<List<Imagen>>() {}.getType();
                        List<Imagen> imagenes = gson.fromJson(jsonFromServer, type);

                        for(Imagen i : imagenes) {
                            int length = GoTravel.getSesion().getEntrada().readInt();
                            byte[] foto = new byte[length];
                            GoTravel.getSesion().getEntrada().readFully(foto);
                           i.setImagen(foto);
                        }

                        return imagenes;

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
    void subirFoto() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
            );

            File file = fileChooser.showOpenDialog(null);

            if(file != null) {

                try {

                    byte[] fileContent = Files.readAllBytes(file.toPath());

                    new Thread(() -> {

                        Gson gson = new GsonBuilder()
                                .serializeNulls()
                                .setLenient()
                                .create();

                        try {

                            GoTravel.getSesion().getSalida().writeUTF("uploadFoto;servicio;" + servicioId);
                            GoTravel.getSesion().getSalida().flush();

                            GoTravel.getSesion().getSalida().writeInt(fileContent.length);
                            GoTravel.getSesion().getSalida().write(fileContent);
                            GoTravel.getSesion().getSalida().flush();

                            String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                            Servicio servicio = gson.fromJson(jsonFromServer, Servicio.class);

                            if(servicio != null) {
                                GoTravel.setRoot("servicio");
                            }

                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                            try {
                                GoTravel.setRoot("landing");
                            } catch (IOException ex) {
                                System.err.println(e.getMessage());
                            }
                        }

                    }).start();

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }

        }

    }

    @FXML
    void deleteFoto() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {

                new Thread(() -> {

                    try {

                        GoTravel.getSesion().getSalida().writeUTF("delete;imagen;" + s.getImagenes().get(fotoActual).getId());
                        GoTravel.getSesion().getSalida().flush();

                        boolean confirm = GoTravel.getSesion().getEntrada().readBoolean();

                        if(confirm){
                            GoTravel.setRoot("servicio");
                        }

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
                    }

                }).start();

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

    }

    @FXML
    void publicarServicio() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {

                new Thread(() -> {

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        GoTravel.getSesion().getSalida().writeUTF("servicio;publicar;" + servicioId);
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Servicio servicioFromServer = gson.fromJson(jsonFromServer, Servicio.class);

                        if(servicioFromServer != null){
                            GoTravel.setRoot("servicio");
                        }

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
                    }

                }).start();

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

    }

    @FXML
    void archivarServicio() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {

                new Thread(() -> {

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        GoTravel.getSesion().getSalida().writeUTF("servicio;archivar;" + servicioId);
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Servicio servicioFromServer = gson.fromJson(jsonFromServer, Servicio.class);

                        if(servicioFromServer != null){
                            GoTravel.setRoot("servicio");
                        }

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
                    }

                }).start();

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

    }

    private void addResena() {

        // TODO

    }

    @FXML
    void contratarServicio() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {

                new Thread(() -> {

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        GoTravel.getSesion().getSalida().writeUTF("servicio;contratar;" + servicioId + ";" + etapaId + ";desktop");
                        GoTravel.getSesion().getSalida().flush();

                        String url = GoTravel.getSesion().getEntrada().readUTF();

                        if(!url.isBlank()) {
                            Platform.runLater(() -> {
                                GoTravel.getHost().showDocument(url);
                            });

                            server = HttpServer.create(new InetSocketAddress(8080), 0);
                            server.createContext("/checkout_returnurl", new CheckoutHandler());
                            server.createContext("/checkout_cancelurl", new SuscribirseScreen.CancelHandler());
                            server.setExecutor(null);
                            server.start();

                        }

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
                    }

                }).start();

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

    }

    static class CheckoutHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();
            String[] params = query.split("&");

            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length > 1 && keyValue[0].equals("token")) {
                    String contratacionId = keyValue[1];

                    try {
                        new Thread(() -> {
                            Gson gson = new GsonBuilder()
                                    .serializeNulls()
                                    .setLenient()
                                    .create();

                            try {
                                GoTravel.getSesion().getSalida().writeUTF(contratacionId);
                                GoTravel.getSesion().getSalida().flush();

                                int idViaje = Integer.parseInt(GoTravel.getSesion().getEntrada().readUTF());

                                // TODO ViajeScreen.setViajeId(idViaje);
                                //  GoTravel.setRoot("viaje");

                            } catch (IOException e) {
                                System.err.println(e.getMessage());
                                try {
                                    GoTravel.setRoot("landing");
                                } catch (IOException ex) {
                                    System.err.println(e.getMessage());
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }

                    server.stop(0);

                }
            }

        }

    }

    @FXML
    void navigateToChat() {
        // TODO ChatScreen.setOtroUsuario(s.getUsuario());
        //        GoTravel.setRoot("chat");
    }

}

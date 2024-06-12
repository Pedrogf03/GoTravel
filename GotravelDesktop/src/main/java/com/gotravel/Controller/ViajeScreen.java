package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Etapa;
import com.gotravel.Model.Imagen;
import com.gotravel.Model.Servicio;
import com.gotravel.Model.Viaje;
import com.gotravel.Utils.Fechas;
import com.gotravel.Utils.Fonts;
import com.gotravel.Utils.Paises;
import com.gotravel.Utils.Regex;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class ViajeScreen implements Initializable {

    @Setter
    private static int viajeId;
    private Viaje v;
    private Etapa etapaActualizar;

    @FXML
    private Button guardarEtapaButton;

    @FXML
    private Text descViaje;

    @FXML
    private Button editarViajeButton;

    @FXML
    private DatePicker fechaFinal;

    @FXML
    private Label fechaFinalLabel;

    @FXML
    private DatePicker fechaInicio;

    @FXML
    private Label fechaInicioLabel;

    @FXML
    private Text fechasViaje;

    @FXML
    private HBox hboxEtapas;

    @FXML
    private TextField nombreEtapa;

    @FXML
    private Label nombreEtapaLabel;

    @FXML
    private Text nombreViaje;

    @FXML
    private Button nuevaEtapaButton;

    @FXML
    private Text nuevaEtapaTitle;

    @FXML
    private Text precioViaje;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ChoiceBox<String> tipoEtapa;

    @FXML
    private Label tipoEtapaLabel;

    @FXML
    private ChoiceBox<String> pais;

    @FXML
    private Label paisLabel;

    @FXML
    private AnchorPane etapaDialog;

    @FXML
    private Text errorMsg;

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
            vboxEtapa.setPrefSize(600, 200);
            HBox hbox = new HBox(25);
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
            vboxInfo.getChildren().add(new Label("Destino " + e.getPais()));
            vboxInfo.getChildren().add(new Label(e.inicio() + " - " + e.fin()));
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

            VBox vboxContrataciones = new VBox();
            vboxContrataciones.setAlignment(Pos.CENTER);
            final boolean[] expanded = {false};

            btnImagen.setOnAction(actionEvent -> {
                expanded[0] = !expanded[0];
                if(expanded[0]) {
                    imageView.setImage(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("arrow-up-solid.png"))));

                    vboxContrataciones.getChildren().clear();

                    mostrarContrataciones(e, vboxContrataciones, esEtapaActual, LocalDate.parse(e.getFechaFinal(), Fechas.formatoFromDb).isBefore(LocalDate.now()));

                    vboxEtapa.getChildren().add(vboxContrataciones);
                } else {
                    imageView.setImage(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("arrow-down-solid.png"))));
                    vboxEtapa.getChildren().remove(vboxContrataciones);
                }
            });

            hbox.getChildren().addAll(vboxInfo, btnImagen);
            vboxEtapa.getChildren().add(hbox);
            VBox.setMargin(hbox, new Insets(0, 10, 0, 10));
            VBox.setMargin(vboxContrataciones, new Insets(0, 10, 0, 10));

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

    private void mostrarContrataciones(Etapa etapa, VBox vboxContrataciones, boolean esEtapaActual, boolean etapaPasada) {

        if(!etapaPasada) {
            Button btnBuscarServicios = new Button("Buscar servicios");
            btnBuscarServicios.setFont(Fonts.labelMedium);

            btnBuscarServicios.setOnAction(e -> {
                try {
                    BuscarServiciosScreen.setEtapaId(etapa.getId());
                    GoTravel.setRoot("buscarServicios");
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            });

            Button btnEditarEtapa = new Button("Editar etapa");
            btnEditarEtapa.setFont(Fonts.labelMedium);

            btnEditarEtapa.setOnAction(e -> {
                etapaActualizar = etapa;
                nombreEtapa.setText(etapaActualizar.getNombre());
                fechaInicio.setValue(LocalDate.parse(etapaActualizar.getFechaInicio(), Fechas.formatoFromDb));
                fechaFinal.setValue(LocalDate.parse(etapaActualizar.getFechaFinal(), Fechas.formatoFromDb));
                tipoEtapa.setValue(etapaActualizar.getTipo());
                crearEtapa();
            });

            if (esEtapaActual) {
                btnEditarEtapa.setStyle("-fx-background-color: #3D5F90; -fx-text-fill: #ffffff; -fx-cursor: hand;");
                btnBuscarServicios.setStyle("-fx-background-color: #3D5F90; -fx-text-fill: #ffffff; -fx-cursor: hand;");
            } else {
                btnEditarEtapa.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #3D5F90; -fx-cursor: hand;");
                btnBuscarServicios.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #3D5F90; -fx-cursor: hand;");
            }

            HBox hboxBotones = new HBox(10, btnBuscarServicios, btnEditarEtapa);
            hboxBotones.setAlignment(Pos.CENTER);

            vboxContrataciones.getChildren().add(hboxBotones);
        }

        if(etapa.getContrataciones().isEmpty()) {
            vboxContrataciones.getChildren().add(new Label("Esta etapa no tiene servicios contratados"));
        } else {
            for(Servicio s : etapa.getContrataciones()) {
                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPrefSize(300, 100);
                hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                hbox.setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");
                hbox.setCursor(Cursor.HAND);

                ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(s.getImagenes().get(0).getImagen())));
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
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

                vboxContrataciones.getChildren().add(hbox);
                VBox.setMargin(hbox, new Insets(10, 10, 10, 10));
                hbox.setOnMouseClicked(event -> {
                    try {
                        ServicioScreen.setServicioId(s.getId());
                        ServicioScreen.setPrevScreen("viaje");
                        GoTravel.setRoot("servicio");
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                });
            }
        }

        if (esEtapaActual) {
            vboxContrataciones.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 10;");
        } else {
            vboxContrataciones.setStyle("-fx-background-color: #3D5F90; -fx-padding: 10;");
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

            nuevaEtapaTitle.setFont(Fonts.titleMedium);

            nombreEtapaLabel.setFont(Fonts.labelMedium);
            fechaFinalLabel.setFont(Fonts.labelMedium);
            fechaInicioLabel.setFont(Fonts.labelMedium);
            tipoEtapaLabel.setFont(Fonts.labelMedium);
            paisLabel.setFont(Fonts.labelMedium);

            fechaInicio.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });

            fechaFinal.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });

            pais.getItems().addAll(Paises.paises);

            tipoEtapa.getItems().addAll("Estancia", "Transporte");

            guardarEtapaButton.setFont(Fonts.labelMedium);

        } else {
            try {
                GoTravel.setRoot("landing");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

    }

    @FXML
    void crearEtapa() {
        etapaDialog.setDisable(false);
        etapaDialog.setVisible(true);
    }

    @FXML
    void cerrarDialog() {
        etapaDialog.setDisable(true);
        etapaDialog.setVisible(false);
        etapaActualizar = null;
        nombreEtapa.setText("");
        fechaInicio.setValue(null);
        fechaFinal.setValue(null);
        tipoEtapa.setValue(null);
        pais.setValue(null);
    }

    @FXML
    void saveEtapa() {

        if(nombreEtapa.getText().isBlank() || fechaInicio.getValue() == null || fechaFinal.getValue() == null || tipoEtapa.getValue() == null || pais.getValue() == null) {
            errorMsg.setText("Por favor rellena todos los campos");
        } else {

            Etapa etapa;
            if(etapaActualizar != null) {
                etapa = etapaActualizar;
                etapa.setNombre(nombreEtapa.getText());
                etapa.setFechaInicio(fechaInicio.getValue().format(Fechas.formatoFromDb));
                etapa.setFechaFinal(fechaFinal.getValue().format(Fechas.formatoFromDb));
                etapa.setTipo(tipoEtapa.getValue());
                etapa.setPais(pais.getValue());
            } else {
                etapa = new Etapa(nombreEtapa.getText(), fechaInicio.getValue().format(Fechas.formatoFromDb), fechaFinal.getValue().format(Fechas.formatoFromDb), tipoEtapa.getValue(),  pais.getValue(), 0.0);
            }

            if(!(etapa.getNombre().isBlank() || etapa.getNombre().isEmpty()) && Regex.regexCamposAlfaNum.matcher(etapa.getNombre()).matches() && etapa.getNombre().length() <= 45) {

                LocalDate inicio = LocalDate.parse(etapa.inicio(), Fechas.formatoFinal);
                LocalDate fin = LocalDate.parse(etapa.fin(), Fechas.formatoFinal);

                if(!fin.isBefore(inicio)) {

                    Viaje v = findViajeById();

                    if(inicio.isAfter(LocalDate.parse(v.getFechaInicio(), Fechas.formatoFromDb)) || inicio.isEqual(LocalDate.parse(v.getFechaInicio(), Fechas.formatoFromDb))) {

                        if(fin.isBefore(LocalDate.parse(v.getFechaFin(), Fechas.formatoFromDb)) || fin.isEqual(LocalDate.parse(v.getFechaFin(), Fechas.formatoFromDb))) {

                            for(Etapa etapaExistente : v.getEtapas()) {

                                if(etapa.getId() != etapaExistente.getId()){
                                    LocalDate inicioExistente = LocalDate.parse(etapaExistente.inicio(), Fechas.formatoFinal);
                                    LocalDate finExistente = LocalDate.parse(etapaExistente.fin(), Fechas.formatoFinal);

                                    if (inicio.isAfter(finExistente) && inicioExistente.isAfter(fin)) {
                                        errorMsg.setText("La nueva etapa se superpone con una etapa existente");
                                        return;
                                    }
                                }

                            }

                            if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

                                try {
                                    Executors.newSingleThreadExecutor().submit(() -> {
                                        Gson gson = new GsonBuilder()
                                                .serializeNulls()
                                                .setLenient()
                                                .create();

                                        try {
                                            if(etapaActualizar != null) {
                                                GoTravel.getSesion().getSalida().writeUTF("update;etapa;" + viajeId);
                                            } else {
                                                GoTravel.getSesion().getSalida().writeUTF("save;etapa;" + viajeId);
                                            }
                                            GoTravel.getSesion().getSalida().writeUTF(gson.toJson(etapa));
                                            GoTravel.getSesion().getSalida().flush();

                                            String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                                            Etapa etapaFromServer = gson.fromJson(jsonFromServer, Etapa.class);

                                            if (etapaFromServer != null) {
                                                ViajeScreen.setViajeId(viajeId);
                                                GoTravel.setRoot("viaje");
                                            }

                                        } catch (IOException e) {
                                            System.err.println(e.getMessage());
                                            try {
                                                GoTravel.setRoot("landing");
                                            } catch (IOException ex) {
                                                System.err.println(e.getMessage());
                                            }
                                        }
                                    }).get();
                                } catch (Exception e) {
                                    System.err.println(e.getMessage());
                                }

                            }

                        } else {
                            errorMsg.setText("El final no puede ser posterior a la fecha de final del viaje");
                        }

                    } else {
                        errorMsg.setText("El inicio de la etapa no puede ser antes que la fecha de inicio del viaje");
                    }

                } else {
                    errorMsg.setText("La fecha de final no puede ser anterior a la inicial");
                }

            } else {
                errorMsg.setText("El nombre no es válido");
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
                        Viaje viaje = gson.fromJson(jsonFromServer, Viaje.class);

                        for(Etapa e : viaje.getEtapas()) {
                            GoTravel.getSesion().getSalida().writeUTF("findContratacionesByEtapa;" + e.getId());
                            GoTravel.getSesion().getSalida().flush();

                            String contratacionesFromServer = GoTravel.getSesion().getEntrada().readUTF();
                            Type type = new TypeToken<List<Servicio>>() {}.getType();
                            List<Servicio> servicios = gson.fromJson(contratacionesFromServer, type);

                            for(Servicio s : servicios) {
                                List<Imagen> imagenes = new ArrayList<>();
                                imagenes.add(getFirstImagenFromServicio(s.getId()));
                                s.setImagenes(imagenes);
                            }

                            e.setContrataciones(servicios);

                        }

                        return viaje;

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

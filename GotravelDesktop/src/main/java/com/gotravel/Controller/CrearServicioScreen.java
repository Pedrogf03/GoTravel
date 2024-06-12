package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Direccion;
import com.gotravel.Model.Servicio;
import com.gotravel.Model.TipoServicio;
import com.gotravel.Utils.Fechas;
import com.gotravel.Utils.Fonts;
import com.gotravel.Utils.Paises;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import jfxtras.scene.control.CalendarTimePicker;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import static com.gotravel.Utils.Regex.regexCamposAlfaNum;
import static com.gotravel.Utils.Regex.regexCp;

public class CrearServicioScreen implements Initializable {

    @FXML
    private ChoiceBox<String> choicePaises;

    @FXML
    private ChoiceBox<TipoServicio> choiceTipos;

    @FXML
    private TextField ciudad;

    @FXML
    private TextField codigoPostal;

    @FXML
    private Button crearServicioButton;

    @FXML
    private TextField descripcion;

    @FXML
    private TextField estado;

    @FXML
    private DatePicker fechaFinal;

    @FXML
    private DatePicker fechaInicio;

    @FXML
    private CalendarTimePicker hora;

    @FXML
    private TextField linea1;

    @FXML
    private TextField linea2;

    @FXML
    private TextField nombre;

    @FXML
    private TextField precio;

    @FXML
    private Text titulo;

    @FXML
    private VBox vboxDir;

    @FXML
    private VBox vboxFechas;

    @FXML
    private VBox vboxInfo;

    @FXML
    private Text errorMsg;

    @FXML
    void crearServicio() {

        if(nombre.getText().isBlank() || precio.getText().isBlank() || choiceTipos.getValue() == null || fechaInicio.getValue() == null || (fechaFinal.getValue() == null && hora.getCalendar() == null) || linea1.getText().isBlank() || ciudad.getText().isBlank() || estado.getText().isBlank() || choicePaises.getValue() == null || codigoPostal.getText().isBlank()) {
            errorMsg.setText("Por favor rellena todos los campos obligatorios");
        } else {

            Direccion d = validarDireccion(linea1.getText(), linea2.getText().isBlank() ? null : linea2.getText(), ciudad.getText(), estado.getText(), choicePaises.getValue(), codigoPostal.getText() );
            if(d != null) {

                if(regexCamposAlfaNum.matcher(nombre.getText()).matches() && nombre.getText().length() <= 45) {

                    if(regexCamposAlfaNum.matcher(descripcion.getText()).matches() || descripcion.getText().isBlank()) {

                        try {

                            Double coste = Double.parseDouble(precio.getText().replace(",", "."));

                            if(fechaFinal.getValue() != null && fechaFinal.getValue().isBefore(fechaInicio.getValue())) {
                                errorMsg.setText("La fecha de final no puede ser anterior a la fecha de inicio");
                                return;
                            }

                            String horaS = null;

                            if(hora.getCalendar() != null) {
                                horaS = LocalDateTime.ofInstant(hora.getCalendar().toInstant(), ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                            }

                            Servicio servicio = new Servicio(nombre.getText(), descripcion.getText().isBlank() ? null : descripcion.getText(), coste, fechaInicio.getValue().format(Fechas.formatoFromDb), fechaFinal.getValue() != null ? fechaFinal.getValue().format(Fechas.formatoFromDb) : null, horaS, choiceTipos.getValue(), d);

                            if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

                                try {
                                    new Thread(() -> {

                                        Gson gson = new GsonBuilder()
                                                .serializeNulls()
                                                .setLenient()
                                                .create();

                                        try {

                                            GoTravel.getSesion().getSalida().writeUTF("save;servicio");
                                            GoTravel.getSesion().getSalida().flush();

                                            GoTravel.getSesion().getSalida().writeUTF(gson.toJson(servicio));
                                            GoTravel.getSesion().getSalida().flush();

                                            String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                                            Servicio s = gson.fromJson(jsonFromServer, Servicio.class);

                                            if(s != null) {
                                                ServicioScreen.setServicioId(s.getId());
                                                ServicioScreen.setPrevScreen("servicios");
                                                GoTravel.setRoot("servicio");
                                            }

                                        } catch (IOException e) {
                                            System.err.println(e.getCause() + ": " + e.getMessage());
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

                        } catch (NumberFormatException e) {
                            errorMsg.setText("El precio no es válido");
                        }

                    } else {
                        errorMsg.setText("La descripción no es válida");
                    }

                } else {
                    errorMsg.setText("El nombre no es válido");
                }

            }

        }

    }

    private Direccion validarDireccion(String linea1, String linea2, String ciudad, String estado, String pais, String cp) {

        if (regexCamposAlfaNum.matcher(linea1).matches() && linea1.length() <= 200) {

            if (linea2 == null || regexCamposAlfaNum.matcher(linea2).matches() && linea2.length() <= 200) {

                if (regexCamposAlfaNum.matcher(ciudad).matches() && ciudad.length() <= 100) {

                    if (regexCamposAlfaNum.matcher(estado).matches() && estado.length() <= 100) {

                        if (regexCamposAlfaNum.matcher(pais).matches() && pais.length() <= 100) {

                            if (regexCp.matcher(cp).matches()) {

                                errorMsg.setText("");
                                return new Direccion(linea1, linea2, ciudad, estado, pais, cp);

                            } else {
                                errorMsg.setText("El código postal no es válido");
                            }

                        } else {
                            errorMsg.setText("El país no es válido");
                        }

                    } else {
                        errorMsg.setText("El estado no es válido");
                    }

                } else {
                    errorMsg.setText("La ciudad no es válida");
                }

            } else {
                errorMsg.setText("La linea 2 no es válida");
            }

        } else {
            errorMsg.setText("La linea 1 no es válida");
        }

        return null;

    }

    @FXML
    void navigateUp() throws IOException {
        GoTravel.setRoot("home");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        titulo.setFont(Fonts.titleMedium);

        for(Node n : vboxInfo.getChildren()) {
            if(n instanceof Label) {
                ((Label) n).setFont(Fonts.labelMedium);
            } else if (n instanceof Text) {
                ((Text) n).setFont(Fonts.titleSmall);
            }
        }

        for(Node n : vboxDir.getChildren()) {
            if(n instanceof Label) {
                ((Label) n).setFont(Fonts.labelMedium);
            } else if (n instanceof Text) {
                ((Text) n).setFont(Fonts.titleSmall);
            }
        }

        for(Node n : vboxFechas.getChildren()) {
            if(n instanceof Label) {
                ((Label) n).setFont(Fonts.labelMedium);
            } else if (n instanceof Text) {
                ((Text) n).setFont(Fonts.titleSmall);
            }
        }

        crearServicioButton.setFont(Fonts.titleSmall);
        choicePaises.getItems().addAll(Paises.paises);
        choiceTipos.getItems().addAll(getTiposServicio());

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

        precio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([\\\\.,]\\d{0,2})?")) {
                precio.setText(oldValue);
            }
        });

        codigoPostal.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,5}")) {
                codigoPostal.setText(oldValue);
            }
        });

    }

    private List<TipoServicio> getTiposServicio() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        GoTravel.getSesion().getSalida().writeUTF("findAll;tipoServicio");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Type type = new TypeToken<List<TipoServicio>>() {}.getType();

                        return gson.<List<TipoServicio>>fromJson(jsonFromServer, type);

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
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

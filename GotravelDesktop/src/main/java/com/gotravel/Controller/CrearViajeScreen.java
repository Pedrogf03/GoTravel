package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Servicio;
import com.gotravel.Model.Viaje;
import com.gotravel.Utils.Fonts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import static com.gotravel.Utils.Fechas.formatoFinal;
import static com.gotravel.Utils.Fechas.formatoFromDb;
import static com.gotravel.Utils.Regex.regexCamposAlfaNum;

public class CrearViajeScreen implements Initializable {

    @FXML
    private Button crearViajeButton;

    @FXML
    private Label descLabel;

    @FXML
    private TextField descripcion;

    @FXML
    private Text errorMsg;

    @FXML
    private DatePicker fechaFin;

    @FXML
    private Label fechaFinalLabel;

    @FXML
    private DatePicker fechaInicio;

    @FXML
    private Label fechaInicioLabel;

    @FXML
    private TextField nombre;

    @FXML
    private Label nombreLabel;

    @FXML
    private Text title;

    @FXML
    void crearViaje(ActionEvent event) {

        if(nombre.getText().isBlank() || fechaInicio.getValue() == null || fechaFin.getValue() == null) {
            errorMsg.setText("Por favor rellena todos los campos obligatorios");
        } else {

            LocalDate inicio = LocalDate.parse(fechaInicio.getValue().toString(), formatoFinal);
            LocalDate fin = LocalDate.parse(fechaFin.getValue().toString(), formatoFinal);

            if(!regexCamposAlfaNum.matcher(nombre.getText()).matches() || nombre.getText().length() > 45) {
                errorMsg.setText("El nombre no es válido");
            } else if(!regexCamposAlfaNum.matcher(descripcion.getText()).matches()) {
                errorMsg.setText("La descripción no es válida");
            } else if (fin.isBefore(inicio)) {
                errorMsg.setText("La fecha de final no puede ser antes que la fecha de inicio");
            } else {
                Viaje viaje = new Viaje(nombre.getText(), descripcion.getText().isBlank() ? null : descripcion.getText(), inicio.format(formatoFromDb), fin.format(formatoFromDb), 0.0);
                guardarViaje(viaje);
            }
        }

    }

    private void guardarViaje(Viaje viaje) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                new Thread(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("save:viaje");
                        GoTravel.getSesion().getSalida().flush();

                        String json = gson.toJson(viaje);
                        GoTravel.getSesion().getSalida().writeUTF(json);
                        GoTravel.getSesion().getSalida().flush();


                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Viaje viajeFromServer = gson.fromJson(jsonFromServer, Viaje.class);

                        if (viajeFromServer != null) {
                            // TODO: ViajeScreen.setViaje(viajeFromServer);
                            GoTravel.setRoot("viaje");
                        } else {
                            errorMsg.setText("No se ha podido guardar el viaje");
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    void navigateUp(ActionEvent event) throws IOException {
        GoTravel.setRoot("home");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        title.setFont(Fonts.titleMedium);

        nombreLabel.setFont(Fonts.labelMedium);
        descLabel.setFont(Fonts.labelMedium);
        fechaFinalLabel.setFont(Fonts.labelMedium);
        fechaInicioLabel.setFont(Fonts.labelMedium);

        errorMsg.setFont(Fonts.labelMedium);

        crearViajeButton.setFont(Fonts.titleMedium);

        fechaInicio.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        fechaFin.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

    }
}


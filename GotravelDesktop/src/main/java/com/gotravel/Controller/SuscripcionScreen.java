package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.GoTravel;
import com.gotravel.Model.Suscripcion;
import com.gotravel.Utils.Fonts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class SuscripcionScreen implements Initializable {

    private Suscripcion s;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        s = findSuscripcionByUsuarioId();

        title.setFont(Fonts.titleMedium);

        if(s.getRenovar().equalsIgnoreCase("1")){
            p.setText("Tu suscripción está activa y se renovará automáticamente el " + s.getFinal());
            button.setText("Cancelar suscripción");
        } else {
            p.setText("Tu suscripción está activa y pero se suspenderá automáticamente el " + s.getFinal() + "\n Si decidess volver a suscribirte no se aplicarán cargos hasta la próxima fecha de facturación");
            button.setText("Renovar suscripción");
        }
        p.setFont(Fonts.labelMedium);
        button.setFont(Fonts.labelMedium);

    }

    private Suscripcion findSuscripcionByUsuarioId()  {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;suscripcion");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        return gson.fromJson(jsonFromServer, Suscripcion.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        GoTravel.setRoot("landing");
                        return null;
                    }
                }).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;

    }

    private boolean activarSuscripcion(String subscriptionId)  {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("suscripcion;renovar;" + subscriptionId);
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Suscripcion s = gson.fromJson(jsonFromServer, Suscripcion.class);

                        if(s != null) {
                            return true;
                        } else {
                            return false;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        GoTravel.setRoot("landing");
                        return false;
                    }
                }).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;

    }

    private boolean cancelarSuscripcion(String subscriptionId)  {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("suscripcion;cancelar;" + subscriptionId);
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Suscripcion s = gson.fromJson(jsonFromServer, Suscripcion.class);

                        if(s != null) {
                            return true;
                        } else {
                            return false;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        GoTravel.setRoot("landing");
                        return false;
                    }
                }).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;

    }

    @FXML
    private Text p;

    @FXML
    private Text title;

    @FXML
    void navigateUp(ActionEvent event) throws IOException {
        GoTravel.setRoot("perfil");
    }

    @FXML
    private Button button;

    @FXML
    void suscribirse(ActionEvent event) throws IOException {
        boolean reload = false;

        if(s.getRenovar().equalsIgnoreCase("1")){
            reload = cancelarSuscripcion(s.getId());
        } else {
            reload = activarSuscripcion(s.getId());
        }

        if(reload) {
            GoTravel.setRoot("suscripcion");
        }
    }

}

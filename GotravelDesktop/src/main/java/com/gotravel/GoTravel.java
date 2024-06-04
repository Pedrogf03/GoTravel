package com.gotravel;

import com.gotravel.Utils.Sesion;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class GoTravel extends Application {

    @Getter
    private static HostServices host;

    private static Scene scene;

    @Getter
    private static Sesion sesion;


    @Override
    public void start(Stage stage) throws IOException {

        host = getHostServices();

        scene = new Scene(loadFXML("landing"));
        stage.setMaximized(true);
        stage.setTitle("GoTravel!");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("logo.jpg")));
        stage.getIcons().add(icon);
        stage.setScene(scene);

        stage.show();

        sesion = new Sesion();

    }

    @Override
    public void stop() {

        if(sesion != null && sesion.getSocket() != null && sesion.getSocket().isConnected()) {
            try {
                sesion.getSalida().writeUTF("cerrarSesion");
                sesion.getSalida().flush();

                sesion.getSalida().close();
                sesion.getEntrada().close();
                sesion.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        System.exit(0);

    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GoTravel.class.getResource("view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static String toSha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
package com.gotravel.cliente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.Socket;

public class App extends Application {

    private static Scene scene;
    @Getter
    @Setter
    private static Socket cliente;

    @Override
    public void start(Stage stage) throws IOException {

        scene = new Scene(loadFXML("login"));
        stage.setScene(scene);
        stage.show();

    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("../../../" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
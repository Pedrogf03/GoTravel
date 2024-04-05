package com.gotravel.cliente.controlador;

import com.gotravel.cliente.App;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class Registro {

    @FXML
    private TextField email;

    @FXML
    private Text mensajeError;

    @FXML
    private PasswordField password;

    @FXML
    private TextField usuario;

    @FXML
    void login() throws IOException {
        App.setRoot("login");
    }

    @FXML
    void registrarse() {

        if(!email.getText().isEmpty() && !password.getText().isEmpty() && !usuario.getText().isEmpty()) {

            String dirIp = "localhost";
            int puerto = 8484;

            try{

                Properties conf = new Properties();
                conf.load(new FileInputStream("client.properties"));
                puerto = Integer.parseInt(conf.getProperty("PUERTO"));
                dirIp = conf.getProperty("IP");

            } catch (IOException e) {
                System.out.println("No se ha podido leer el archivo de propiedades");
            }

            try {

                App.setCliente(new Socket(dirIp, puerto));
                DataInputStream entrada = new DataInputStream(App.getCliente().getInputStream());
                DataOutputStream salida = new DataOutputStream(App.getCliente().getOutputStream());

                salida.writeUTF("registro;" + email.getText() + ";" + password.getText() + ";" + usuario.getText());

                if(entrada.readBoolean()) {
                    mensajeError.setText("Usuario registrado correctamente");
                    mensajeError.setVisible(true);
                } else {
                    mensajeError.setText("Ese email ya está en uso");
                    mensajeError.setVisible(true);
                }

            } catch (IOException e) {
                System.out.println("No se puede conectar con el servidor");
            }

        } else {
            mensajeError.setText("Rellena los campos obligatorios");
            mensajeError.setVisible(true);
        }

    }

}

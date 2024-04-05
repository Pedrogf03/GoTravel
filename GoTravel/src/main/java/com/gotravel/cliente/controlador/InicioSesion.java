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

public class InicioSesion {

    @FXML
    private Text mensajeError;

    @FXML
    private PasswordField password;

    @FXML
    private TextField email;

    @FXML
    void iniciarSesion() {

        if(!email.getText().isEmpty() && !password.getText().isEmpty()) {

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

                salida.writeUTF("login;" + email.getText() + ";" + password.getText());

                if(entrada.readBoolean()) {
                    mensajeError.setText("Sesión iniciada correctamente");
                    mensajeError.setVisible(true);
                } else {
                    mensajeError.setText("Usuario o contraseña incorrectos");
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

    @FXML
    void registrarse() throws IOException {
        App.setRoot("registro");
    }

}

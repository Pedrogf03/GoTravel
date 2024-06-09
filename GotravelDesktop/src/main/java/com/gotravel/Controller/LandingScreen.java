package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.GoTravel;
import com.gotravel.ImageApi.ImageApi;
import com.gotravel.Model.Rol;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fonts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.lang.module.Configuration;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.gotravel.Utils.Regex.*;

public class LandingScreen implements Initializable {

    @FXML
    private PasswordField confirmarContrasena;

    @FXML
    private Label confirmarContrasenaLabel;

    @FXML
    private PasswordField contrasena;

    @FXML
    private Label contrasenaLabel;

    @FXML
    private TextField email;

    @FXML
    private Label emailLabel;

    @FXML
    private Label iniciaresionLabel;

    @FXML
    private TextField nombre;

    @FXML
    private Label nombreLabel;

    @FXML
    private ImageView wallpaper;

    @FXML
    private Button cambiarFormularioMsg;

    @FXML
    private Button contrasenaOlvidadaMsg;

    @FXML
    private Label errorMsg;

    @FXML
    void cambiarFormulario() {
        if(iniciarSesion) {
            iniciaresionLabel.setText("Registrarse");
            nombreLabel.setDisable(false);
            nombre.setDisable(false);
            nombreLabel.setVisible(true);
            nombre.setVisible(true);

            confirmarContrasenaLabel.setDisable(false);
            confirmarContrasena.setDisable(false);
            confirmarContrasenaLabel.setVisible(true);
            confirmarContrasena.setVisible(true);

            contrasenaOlvidadaMsg.setDisable(true);
            contrasenaOlvidadaMsg.setVisible(false);

            cambiarFormularioMsg.setText("¿Ya tienes una cuenta?");
        } else {
            iniciaresionLabel.setText("Iniciar Sesión");
            nombreLabel.setDisable(true);
            nombre.setDisable(true);
            nombreLabel.setVisible(false);
            nombre.setVisible(false);

            confirmarContrasenaLabel.setDisable(true);
            confirmarContrasena.setDisable(true);
            confirmarContrasenaLabel.setVisible(false);
            confirmarContrasena.setVisible(false);

            contrasenaOlvidadaMsg.setDisable(false);
            contrasenaOlvidadaMsg.setVisible(true);

            cambiarFormularioMsg.setText("¿Ya estás registrado?");

        }
        iniciarSesion = !iniciarSesion;
    }

    @FXML
    void conectarConElServidor() {

        errorMsg.setText("Conectando con el servidor...");

        String peticion;

        if(iniciarSesion) {
            peticion = iniciarSesion(email.getText(), contrasena.getText());
        } else {
            peticion = registrarse(email.getText(), contrasena.getText(), nombre.getText(), contrasena.getText());
        }

        if(!peticion.isEmpty()) {
            String dirIp = "localhost";
            int puerto = 8484;

            Properties conf = new Properties();
            try {
                FileInputStream inputStream = new FileInputStream("client.properties");
                conf.load(inputStream);
                dirIp = conf.getProperty("IP");
                puerto = Integer.parseInt(conf.getProperty("PUERTO"));
            } catch (IOException e) {
                System.err.println("No se ha podido leer el archivo de propiedades");
            }

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create();

            Usuario usuario;

            try {

                SocketAddress socketAddress = new InetSocketAddress(dirIp, puerto);
                Socket cliente = new Socket();
                int tiempoDeEspera = 1000;

                cliente.connect(socketAddress, tiempoDeEspera);
                GoTravel.getSesion().setSocket(cliente);
                GoTravel.getSesion().setSalida(new DataOutputStream(GoTravel.getSesion().getSocket().getOutputStream()));
                GoTravel.getSesion().setEntrada(new DataInputStream(GoTravel.getSesion().getSocket().getInputStream()));

                GoTravel.getSesion().getSalida().writeUTF(peticion); // El mensaje envíado dependerá de si se ha solicitado un inicio de sesión o un registro
                GoTravel.getSesion().getSalida().flush();

                String fromServer = GoTravel.getSesion().getEntrada().readUTF();
                if(fromServer.equals("reintentar")) {
                    if(peticion.contains("login")) {
                        errorMsg.setText("Email o contraseña incorrectos");
                    } else if (peticion.contains("registro")) {
                        errorMsg.setText("Ese email ya está en uso");
                    }
                } else if (fromServer.equals("oculto")) {
                    if(peticion.contains("login")) {
                        errorMsg.setText("Ese usuario está oculto y no puede iniciar sesión");
                    }
                } else {
                    usuario = gson.fromJson(fromServer, Usuario.class);

                    for(Rol r : usuario.getRoles()) {
                        if(r.getNombre().equalsIgnoreCase("profesional")) {
                            usuario.setProfesional(true);
                        } else if (r.getNombre().equalsIgnoreCase("administrador")) {
                            usuario.setAdministrador(true);
                        }
                    }

                    errorMsg.setText("Sesión iniciada correctamente");
                    GoTravel.getSesion().setUsuario(usuario);

                    // Si se recibe un true (es decir, el usuario tiene foto asociada en la bbdd)
                    if(GoTravel.getSesion().getEntrada().readBoolean()) {
                        int length = GoTravel.getSesion().getEntrada().readInt(); // Lee la longitud del ByteArray
                        byte[] byteArray = new byte[length];
                        GoTravel.getSesion().getEntrada().readFully(byteArray); // Lee el ByteArray
                        GoTravel.getSesion().getUsuario().setFoto(byteArray);
                    }

                    GoTravel.setRoot("home");
                }

            } catch (IOException e) {
                errorMsg.setText("No se ha podido conectar con el servidor");
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

    }

    private String registrarse(String email, String contrasena, String nombre, String confirmarContrasena) {

        if (!nombre.isBlank() && !nombre.isEmpty() && !email.isBlank() && !email.isEmpty() && !contrasena.isBlank() && !contrasena.isEmpty() && !confirmarContrasena.isBlank() && !confirmarContrasena.isEmpty()) {

            if (!regexCamposAlfaNum.matcher(nombre).matches() || nombre.length() > 45) {
                errorMsg.setText("El nombre no es válido");
                errorMsg.setText(nombre);
            } else if (!regexEmail.matcher(email).matches()) {
                errorMsg.setText("El email no es válido");
            } else if (!regexContrasena.matcher(contrasena).matches()) {
                errorMsg.setText("La contraseña no es válida");
            } else {

                String contrasenaHash = GoTravel.toSha256(contrasena.trim());
                String confirmarHash = GoTravel.toSha256(confirmarContrasena.trim());

                if(!contrasenaHash.equals(confirmarHash)) {
                    errorMsg.setText("Las contraseñas no coinciden");
                } else {

                    return "registro;" + email.trim() + ";" + contrasenaHash + ";" + nombre.trim();

                }

            }

        } else {
            errorMsg.setText("Por favor, rellena todos los campos");
        }

        return "";

    }

    private String iniciarSesion(String email, String contrasena) {

        if(!(email.isBlank() || email.isEmpty()) && !(contrasena.isBlank() || contrasena.isEmpty())) {

            return "login;" + email.trim() + ";" + GoTravel.toSha256(contrasena.trim());

        } else {
            errorMsg.setText("Por favor rellena todos los campos");
        }

        return "";

    }

    @FXML
    void contrasenaOlvidada() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recuperación de contraseña");
        dialog.setHeaderText("Recuperación de contraseña");
        dialog.setContentText("Introduce tu correo electrónico:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String correoElectronico = result.get();
            if(recuperarContrasena(correoElectronico)) {
                dialog.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Email Enviado");
                alert.setHeaderText(null);
                alert.setContentText("Se ha cambiado tu contraseña automáticamente y ha sido enviada junto con instrucciones al correo electrónico proporcionado. Por favor, inicia sesión y cambia tu contraseña cuanto antes.");

                alert.showAndWait();
            }

        }

    }


    private boolean recuperarContrasena(String email) {

        String dirIp = "localhost";
        int puerto = 8484;

        Properties conf = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream("client.properties");
            conf.load(inputStream);
            dirIp = conf.getProperty("IP");
            puerto = Integer.parseInt(conf.getProperty("PUERTO"));
        } catch (IOException e) {
            System.err.println("No se ha podido leer el archivo de propiedades");
        }

        try {
            SocketAddress socketAddress = new InetSocketAddress(dirIp, puerto);
            Socket cliente = new Socket();
            int tiempoDeEspera = 1000;

            cliente.connect(socketAddress, tiempoDeEspera);
            GoTravel.getSesion().setSocket(cliente);
            GoTravel.getSesion().setSalida(new DataOutputStream(GoTravel.getSesion().getSocket().getOutputStream()));
            GoTravel.getSesion().setEntrada(new DataInputStream(GoTravel.getSesion().getSocket().getInputStream()));

            GoTravel.getSesion().getSalida().writeUTF("recuperarContrasena;" + email);
            GoTravel.getSesion().getSalida().flush();

            return GoTravel.getSesion().getEntrada().readBoolean();

        } catch (IOException e) {
            errorMsg.setText("No se ha podido conectar con el servidor");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return false;

    }

    private boolean iniciarSesion = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            wallpaper.setImage(new Image(new ByteArrayInputStream(Objects.requireNonNull(ImageApi.getImage()))));
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }

        iniciaresionLabel.setFont(Fonts.titleMedium);
        nombreLabel.setFont(Fonts.labelMedium);
        emailLabel.setFont(Fonts.labelMedium);
        contrasenaLabel.setFont(Fonts.labelMedium);
        confirmarContrasenaLabel.setFont(Fonts.labelMedium);

        nombreLabel.setDisable(true);
        nombre.setDisable(true);
        nombreLabel.setVisible(false);
        nombre.setVisible(false);

        confirmarContrasenaLabel.setDisable(true);
        confirmarContrasena.setDisable(true);
        confirmarContrasenaLabel.setVisible(false);
        confirmarContrasena.setVisible(false);

    }

}
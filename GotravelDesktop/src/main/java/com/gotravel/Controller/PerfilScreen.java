package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.GoTravel;
import com.gotravel.Model.Rol;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fonts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

import static com.gotravel.Utils.Regex.*;

public class PerfilScreen implements Initializable {

    private Usuario u = GoTravel.getSesion().getUsuario();

    @FXML
    private Button actualizarContrasenaButton;

    @FXML
    private TextField apellidosField;

    @FXML
    private Label apellidosLabel;

    @FXML
    private VBox botones;

    @FXML
    private Text cambiarContrasenaTitle;

    @FXML
    private Button cerrarServidorButton;

    @FXML
    private PasswordField confirmarContrasena;

    @FXML
    private Label confirmarContrasenaLabel;

    @FXML
    private HBox containerTelefono;

    @FXML
    private PasswordField contrasenaActual;

    @FXML
    private Label contrasenaActualLabel;

    @FXML
    private PasswordField contrasenaNueva;

    @FXML
    private Label contrasenaNuevaLabel;

    @FXML
    private Button contratacionesButton;

    @FXML
    private Button editarPerfilButton;

    @FXML
    private AnchorPane editarPerfilPanel;

    @FXML
    private Label email;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailLabel;

    @FXML
    private Text errorMsg;

    @FXML
    private ImageView fotoEditar;

    @FXML
    private VBox infoUser;

    @FXML
    private Button logoutButton;

    @FXML
    private VBox navPanel;

    @FXML
    private Label nombre;

    @FXML
    private TextField nombreField;

    @FXML
    private Label nombreLabel;

    @FXML
    private AnchorPane cambiarContrasenaPanel;

    @FXML
    private Label rol;

    @FXML
    private Button serviciosButton;

    @FXML
    private Button suscripcionesButton;

    @FXML
    private TextField telefonoField;

    @FXML
    private Label telefonoLabel;

    @FXML
    private Label tfno;

    @FXML
    private Button updateFotoButton;

    @FXML
    private Button updateUsuarioButton;

    @FXML
    private ImageView userFoto;

    @FXML
    private Button cambiarContrasenaButton;

    @FXML
    void mostrarNuevaContrasena() {

        cambiarContrasenaPanel.setDisable(false);
        cambiarContrasenaPanel.setVisible(true);

        cerrarEditarPerfil();

    }

    @FXML
    void updateFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                byte[] fileContent = Files.readAllBytes(file.toPath());

                if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

                    new Thread(() -> {
                        try {
                            GoTravel.getSesion().getSalida().writeUTF("uploadFoto;usuario");
                            GoTravel.getSesion().getSalida().flush();

                            GoTravel.getSesion().getSalida().writeInt(fileContent.length);
                            GoTravel.getSesion().getSalida().write(fileContent);
                            GoTravel.getSesion().getSalida().flush();

                            Gson gson = new GsonBuilder()
                                    .serializeNulls()
                                    .setLenient()
                                    .create();

                            String jsonFromerver = GoTravel.getSesion().getEntrada().readUTF();
                            Usuario usuario = gson.fromJson(jsonFromerver, Usuario.class);

                            if(usuario != null) {
                                GoTravel.getSesion().getUsuario().setFoto(fileContent);
                                GoTravel.setRoot("perfil");
                            } else {
                                errorMsg.setText("Lo sentimos, la foto que has seleccionado es demasiado grande");
                            }

                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                            errorMsg.setText("No se puede conectar con el servidor");
                        }
                    }).start();

                }

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }


    @FXML
    void updateUsuario() {

        if(!(nombreField.getText().isEmpty() || nombreField.getText().isBlank()) && regexCamposAlfaNum.matcher(nombreField.getText()).matches() && nombreField.getText().length() <= 45) {
            if((regexCamposAlfaNum.matcher(apellidosField.getText()).matches() && apellidosField.getText().length() <= 200) || apellidosField.getText().isBlank()) {

                if(apellidosField.getText().isBlank()) {
                    apellidosField.setText(null);
                }

                if(!(emailField.getText().isEmpty() || emailField.getText().isBlank()) && regexEmail.matcher(emailField.getText()).matches()) {
                    if(regexTfno.matcher(telefonoField.getText()).matches() || telefonoField.getText().isBlank()) {

                        if(telefonoField.getText().isBlank()) {
                            telefonoField.setText(null);
                        }

                        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

                            new Thread(() -> {
                                try {

                                    Gson gson = new GsonBuilder()
                                            .serializeNulls()
                                            .setLenient()
                                            .create();

                                    GoTravel.getSesion().getSalida().writeUTF("update;usuario");
                                    GoTravel.getSesion().getSalida().flush();

                                    Usuario usuario = new Usuario(u.getId(), nombreField.getText(), apellidosField.getText(), emailField.getText(), u.getContrasena(), telefonoField.getText());

                                    String json = gson.toJson(usuario);
                                    GoTravel.getSesion().getSalida().writeUTF(json);
                                    GoTravel.getSesion().getSalida().flush();

                                    String jsonFromerver = GoTravel.getSesion().getEntrada().readUTF();
                                    usuario = gson.fromJson(jsonFromerver, Usuario.class);

                                    if(usuario != null) {
                                        byte[] foto = GoTravel.getSesion().getUsuario().getFoto();
                                        usuario.setFoto(foto);
                                        for(Rol r : usuario.getRoles()) {
                                            if(r.getNombre().equalsIgnoreCase("profesional")) {
                                                usuario.setProfesional(true);
                                            } else if (r.getNombre().equalsIgnoreCase("administrador")) {
                                                usuario.setAdministrador(true);
                                            }
                                        }
                                        GoTravel.getSesion().setUsuario(usuario);
                                        GoTravel.setRoot("perfil");
                                    }

                                } catch (IOException e) {
                                    System.err.println(e.getMessage());
                                    errorMsg.setText("No se puede conectar con el servidor");
                                }
                            }).start();

                        }

                    } else {
                        errorMsg.setText("El número de teléfono no es válido");
                    }
                } else {
                    errorMsg.setText("El email no es válido");
                }
            } else {
                errorMsg.setText("Los apellidos no son válidos");
            }
        } else {
            errorMsg.setText("El nombre no es válido");
        }

    }

    @FXML
    void cerrarServidor() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            new Thread(() -> {
                try {
                    GoTravel.getSesion().getSalida().writeUTF("cerrarServidor");
                    GoTravel.getSesion().getSalida().flush();

                    GoTravel.getSesion().getSalida().close();
                    GoTravel.getSesion().getEntrada().close();
                    GoTravel.getSesion().getSocket().close();

                    GoTravel.setRoot("landing");

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }).start();

        }

    }

    @FXML
    void cerrarSesion() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            new Thread(() -> {
                try {
                    GoTravel.getSesion().getSalida().writeUTF("cerrarSesion");
                    GoTravel.getSesion().getSalida().flush();

                    GoTravel.getSesion().getSalida().close();
                    GoTravel.getSesion().getEntrada().close();
                    GoTravel.getSesion().getSocket().close();

                    GoTravel.setRoot("landing");

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }).start();

        }

    }

    @FXML
    void editarPerfil() {
        editarPerfilPanel.setVisible(true);
        editarPerfilPanel.setDisable(false);
    }

    @FXML
    void verContrataciones() throws IOException {
        GoTravel.setRoot("contrataciones");
    }

    @FXML
    void verSuscripcion() throws IOException {
        if(u.isProfesional()) {
            GoTravel.setRoot("suscripcion");
        } else {
            GoTravel.setRoot("suscribirse");
        }
    }

    @FXML
    void navigateToChats() throws IOException {
        GoTravel.setRoot("chats");
    }

    @FXML
    void navigateToHome() throws IOException {
        GoTravel.setRoot("home");
    }

    @FXML
    void navigateToPerfil() throws IOException {
        GoTravel.setRoot("perfil");
    }

    @FXML
    void navigateToServicios() throws IOException {
        GoTravel.setRoot("servicios");
    }

    @FXML
    void navigateToViajes() throws IOException {
        GoTravel.setRoot("viajes");
    }

    @FXML
    void cerrarEditarPerfil() {
        editarPerfilPanel.setVisible(false);
        editarPerfilPanel.setDisable(true);
    }

    @FXML
    void cerrarCambiarContrasena() {
        cambiarContrasenaPanel.setVisible(false);
        cambiarContrasenaPanel.setDisable(true);
    }

    @FXML
    void updateContrasena() {


        if((!contrasenaActual.getText().isBlank() || !contrasenaActual.getText().isEmpty()) && (!contrasenaNueva.getText().isBlank() || !contrasenaNueva.getText().isEmpty()) && (!confirmarContrasena.getText().isBlank() || !confirmarContrasena.getText().isEmpty()) ) {

            String contrasenaNuevaHash = GoTravel.toSha256(contrasenaNueva.getText());
            String confirmarHash = GoTravel.toSha256(confirmarContrasena.getText());
            String contrasenaActualHash = GoTravel.toSha256(contrasenaActual.getText());

            if(contrasenaNuevaHash.equals(contrasenaActualHash)) {
                errorMsg.setText("La nueva contraseña no puede ser igual a la anterior");
            } else if (!contrasenaNuevaHash.equals(confirmarHash)) {
                errorMsg.setText("Las contraseñas no coinciden");
            } else {

                if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    new Thread(() -> {
                        try {

                            Usuario usuarioFromServer;

                            GoTravel.getSesion().getSalida().writeUTF("update;contrasena;" + contrasenaActualHash + ";" + contrasenaNuevaHash);
                            GoTravel.getSesion().getSalida().flush();

                            String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                            usuarioFromServer = gson.fromJson(jsonFromServer, Usuario.class);

                            if(usuarioFromServer != null) {
                                byte[] foto = GoTravel.getSesion().getUsuario().getFoto();
                                usuarioFromServer.setFoto(foto);
                                for(Rol r : usuarioFromServer.getRoles()) {
                                    if(r.getNombre().equalsIgnoreCase("profesional")) {
                                        usuarioFromServer.setProfesional(true);
                                    } else if (r.getNombre().equalsIgnoreCase("administrador")) {
                                        usuarioFromServer.setAdministrador(true);
                                    }
                                }
                                GoTravel.getSesion().setUsuario(usuarioFromServer);
                                GoTravel.setRoot("perfil");
                            }

                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    }).start();

                }

            }

        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(!u.isProfesional()) {
            navPanel.getChildren().remove(serviciosButton);
            suscripcionesButton.setText("PROGRAMA DE PROFESIONALES");
        }

        if(!u.isAdministrador()) {
            botones.getChildren().remove(cerrarServidorButton);
        }

        if(u.getFoto() != null){
            userFoto.setImage(new Image(new ByteArrayInputStream(u.getFoto())));
        } else {
            userFoto.setImage(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("userNoFoto.png"))));
        }

        userFoto.setFitHeight(200);
        userFoto.setFitWidth(200);
        userFoto.setPreserveRatio(false);
        if (u.getApellidos() != null) {
            nombre.setText(u.getNombre() + "\n" + u.getApellidos());
        } else {
            nombre.setText(u.getNombre());
        }
        nombre.setFont(Fonts.labelMedium);

        email.setText(u.getEmail());
        email.setFont(Fonts.labelMedium);

        if(u.getTfno() != null) {
            tfno.setText(u.getTfno());
            tfno.setFont(Fonts.labelMedium);
        } else {
            infoUser.getChildren().remove(containerTelefono);
        }

        rol.setText(u.getRoles().get(0).getNombre());
        rol.setFont(Fonts.labelMedium);

        editarPerfilButton.setFont(Fonts.titleMedium);
        contratacionesButton.setFont(Fonts.titleMedium);
        serviciosButton.setFont(Fonts.titleMedium);
        suscripcionesButton.setFont(Fonts.titleMedium);
        logoutButton.setFont(Fonts.titleMedium);
        cerrarServidorButton.setFont(Fonts.titleMedium);

        if(u.getFoto() != null){
            fotoEditar.setImage(new Image(new ByteArrayInputStream(u.getFoto())));
        } else {
            fotoEditar.setImage(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("userNoFoto.png"))));
        }
        nombreField.setText(u.getNombre());
        nombreLabel.setFont(Fonts.labelMedium);
        apellidosField.setText(u.getApellidos());
        apellidosLabel.setFont(Fonts.labelMedium);
        emailField.setText(u.getEmail());
        emailLabel.setFont(Fonts.labelMedium);
        telefonoField.setText(u.getTfno());
        telefonoLabel.setFont(Fonts.labelMedium);

        updateFotoButton.setFont(Fonts.labelMedium);
        updateUsuarioButton.setFont(Fonts.labelMedium);

        actualizarContrasenaButton.setFont(Fonts.labelMedium);

        cambiarContrasenaTitle.setFont(Fonts.titleSmall);
        contrasenaActualLabel.setFont(Fonts.labelMedium);
        contrasenaNuevaLabel.setFont(Fonts.labelMedium);
        confirmarContrasenaLabel.setFont(Fonts.labelMedium);

        cambiarContrasenaButton.setFont(Fonts.labelSmall);

    }

}

package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.GoTravel;
import com.gotravel.Model.Rol;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fonts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import static com.gotravel.Utils.Regex.*;

public class PerfilScreen implements Initializable {

    private Usuario u = GoTravel.getSesion().getUsuario();

    @FXML
    private TextField apellidosField;

    @FXML
    private Label apellidosLabel;

    @FXML
    private VBox botones;

    @FXML
    private Button cerrarServidorButton;

    @FXML
    private HBox containerTelefono;

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
    private Text errorMsg;

    @FXML
    void updateFoto(ActionEvent event) {
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
                            e.printStackTrace();
                            errorMsg.setText("No se puede conectar con el servidor");
                        }
                    }).start();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void updateUsuario(ActionEvent event) throws ExecutionException, InterruptedException {

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
                                    e.printStackTrace();
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
    void cerrarServidor(ActionEvent event) throws ExecutionException, InterruptedException {

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
                    e.printStackTrace();
                }
            }).start();

        }

    }

    @FXML
    void cerrarSesion(ActionEvent event) throws ExecutionException, InterruptedException {

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
                    e.printStackTrace();
                }
            }).start();

        }

    }

    @FXML
    void editarPerfil(ActionEvent event) {
        editarPerfilPanel.setVisible(true);
        editarPerfilPanel.setDisable(false);
    }

    @FXML
    void verContrataciones(ActionEvent event) throws IOException {
        GoTravel.setRoot("contrataciones");
    }

    @FXML
    void verSuscripcion(ActionEvent event) throws IOException {
        if(u.isProfesional()) {
            GoTravel.setRoot("suscripcion");
        } else {
            GoTravel.setRoot("suscribirse");
        }
    }

    @FXML
    void navigateToChats(ActionEvent event) throws IOException {
        GoTravel.setRoot("chats");
    }

    @FXML
    void navigateToHome(ActionEvent event) throws IOException {
        GoTravel.setRoot("home");
    }

    @FXML
    void navigateToPerfil(ActionEvent event) throws IOException {
        GoTravel.setRoot("perfil");
    }

    @FXML
    void navigateToServicios(ActionEvent event) throws IOException {
        GoTravel.setRoot("servicios");
    }

    @FXML
    void navigateToViajes(ActionEvent event) throws IOException {
        GoTravel.setRoot("viajes");
    }

    @FXML
    void cerrarEditarPerfil(ActionEvent event) {
        editarPerfilPanel.setVisible(false);
        editarPerfilPanel.setDisable(true);
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

    }

}

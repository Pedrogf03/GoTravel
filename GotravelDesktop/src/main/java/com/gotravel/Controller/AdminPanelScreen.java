package com.gotravel.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gotravel.GoTravel;
import com.gotravel.Model.Rol;
import com.gotravel.Model.TipoServicio;
import com.gotravel.Model.Usuario;
import com.gotravel.Utils.Fonts;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;

public class AdminPanelScreen implements Initializable {

    private static String busqueda = "";
    private List<Usuario> usuarios;

    @FXML
    private TextField buscador;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ChoiceBox<String> allTipos;

    @FXML
    private Text tipoServicioText;

    @FXML
    void buscarUsuarios() throws IOException {
        busqueda = buscador.getText();
        GoTravel.setRoot("adminPanel");
    }

    @FXML
    void navigateUp() throws IOException {
        GoTravel.setRoot("home");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(busqueda.isBlank()) {
            usuarios = getAllUsuarios();
        } else {
            usuarios = new ArrayList<>();
            for(Usuario u : getAllUsuarios()) {
               if(u.getNombre().toLowerCase().contains(busqueda.toLowerCase()) || (u.getApellidos() != null && u.getApellidos().toLowerCase().contains(busqueda.toLowerCase()))) {
                   usuarios.add(u);
               }
            }
        }

        if(usuarios != null) {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);

            for (Usuario u : usuarios) {
                VBox vboxUsuario = new VBox();
                vboxUsuario.setPadding(new Insets(10));
                vboxUsuario.setSpacing(5);
                vboxUsuario.setStyle("-fx-background-color: white; -fx-alignment: center;");
                vboxUsuario.setPrefSize(400, 200);

                HBox hboxImagenInfo = new HBox(10);
                hboxImagenInfo.setAlignment(Pos.CENTER_LEFT);

                ImageView imagenUsuario;
                if(u.getFoto() != null) {
                    imagenUsuario = new ImageView(new Image(new ByteArrayInputStream(u.getFoto())));
                } else {
                    imagenUsuario = new ImageView(new Image(Objects.requireNonNull(GoTravel.class.getResourceAsStream("userNoFoto.png"))));
                }
                imagenUsuario.setFitHeight(100);
                imagenUsuario.setFitWidth(100);
                hboxImagenInfo.getChildren().add(imagenUsuario);

                VBox vboxInfo = new VBox();

                Label nombreUsuario = new Label(u.getNombre());
                nombreUsuario.setFont(Fonts.titleSmall);

                Label apellidosUsuario = new Label(u.getApellidos());
                apellidosUsuario.setWrapText(true);
                apellidosUsuario.setFont(Fonts.labelSmall);

                Label emailUsuario = new Label(u.getEmail());
                emailUsuario.setWrapText(true);
                emailUsuario.setFont(Fonts.labelSmall);

                Label telefonoUsuario = new Label(u.getTfno());
                telefonoUsuario.setWrapText(true);
                telefonoUsuario.setFont(Fonts.labelSmall);

                StringBuilder roles = new StringBuilder();
                for(Rol rol : u.getRoles()) {
                    roles.append(rol.getNombre()).append(", ");
                }
                if (roles.length() > 0) roles.setLength(roles.length() - 2);

                Label rolesUsuario = new Label(roles.toString());
                rolesUsuario.setFont(Fonts.labelSmall);
                rolesUsuario.setWrapText(true);

                Label ocultoUsuario = new Label("Oculto: " + (u.getOculto().equals("0") ? "No" : "Sí"));
                ocultoUsuario.setWrapText(true);
                ocultoUsuario.setFont(Fonts.labelSmall);

                vboxInfo.getChildren().addAll(nombreUsuario, apellidosUsuario, emailUsuario, telefonoUsuario, rolesUsuario, ocultoUsuario);

                hboxImagenInfo.getChildren().add(vboxInfo);

                HBox hboxBotonesServiciosResenas = new HBox(10);
                hboxBotonesServiciosResenas.setAlignment(Pos.CENTER);

                Button btnServicios = getButton("Servicios", e -> {
                    try {
                        AdminPanelServicios.setUsuario(u);
                        AdminPanelServicios.setBusqueda("");
                        GoTravel.setRoot("adminPanelServicios");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                Button btnResenas = getButton("Reseñas", e -> {
                    try {
                        AdminPanelResenas.setUsuario(u);
                        GoTravel.setRoot("adminPanelResenas");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                hboxBotonesServiciosResenas.getChildren().addAll(btnServicios, btnResenas);

                Button btnOcultar = getBotonOcultar(u);

                vboxUsuario.getChildren().addAll(hboxImagenInfo, hboxBotonesServiciosResenas, btnOcultar);

                flowPane.getChildren().add(vboxUsuario);
            }

            flowPane.setVgap(10);
            flowPane.setHgap(10);
        }

        tipoServicioText.setFont(Fonts.labelSmall);
        allTipos.getItems().addAll(getTiposServicio());
        allTipos.getItems().add("Añadir nuevo...");

        allTipos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Añadir nuevo...".equals(newValue)) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Añadir Nuevo Tipo de Servicio");
                dialog.setHeaderText("Crear un nuevo tipo de servicio");
                dialog.setContentText("Nombre del nuevo tipo de servicio:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(nombreTipoServicio -> {
                    TipoServicio ts = new TipoServicio(nombreTipoServicio);
                    allTipos.setValue(null);
                    if(saveTipoServicio(ts)){
                        allTipos.getItems().add(allTipos.getItems().size() - 1, ts.getNombre());
                    }
                });
            }
        });

    }

    private static Button getButton(String  nombreBoton, EventHandler<ActionEvent> onAction) {
        Button btn = new Button(nombreBoton);
        btn.setFont(Fonts.titleSmall);
        btn.setStyle("-fx-background-color: #3D5F90; -fx-text-fill: white;");
        btn.setCursor(Cursor.HAND);

        btn.setOnAction(onAction);

        return btn;
    }


    private boolean saveTipoServicio(TipoServicio ts) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        GoTravel.getSesion().getSalida().writeUTF("save;tipoServicio");
                        GoTravel.getSesion().getSalida().flush();

                        GoTravel.getSesion().getSalida().writeUTF(gson.toJson(ts));
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        TipoServicio tsFromServer = gson.fromJson(jsonFromServer, TipoServicio.class);

                        return tsFromServer != null;


                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            System.err.println(e.getMessage());
                        }
                        return false;
                    }
                }).get();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

        return false;

    }

    private List<String> getTiposServicio() {

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

                        List<TipoServicio> tipos = gson.fromJson(jsonFromServer, type);
                        List<String> nombresTipo = new ArrayList<>();

                        for(TipoServicio ts : tipos){
                            nombresTipo.add(ts.getNombre());
                        }

                        return nombresTipo;

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

    private Button getBotonOcultar(Usuario u) {
        Button btnOcultar;
        if(u.getOculto().equals("0")) {
            btnOcultar = new Button("Ocultar");
        } else {
            btnOcultar = new Button("Devolver acceso");
        }
        btnOcultar.setFont(Fonts.titleSmall);
        btnOcultar.setStyle("-fx-background-color: #3D5F90; -fx-text-fill: white;");
        btnOcultar.setCursor(Cursor.HAND);

        btnOcultar.setOnAction(e -> {

            ocultarUsuario(u);

        });
        return btnOcultar;
    }

    private void ocultarUsuario(Usuario u) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                new Thread(() -> {

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {

                        if(u.getOculto().equals("0")){
                            u.setOculto("1");
                        } else {
                            u.setOculto("0");
                        }
                        u.setFoto(null);

                        GoTravel.getSesion().getSalida().writeUTF("update;usuario");
                        GoTravel.getSesion().getSalida().flush();

                        GoTravel.getSesion().getSalida().writeUTF(gson.toJson(u));
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Usuario user = gson.fromJson(jsonFromServer, Usuario.class);

                        if(user != null) {
                            GoTravel.setRoot("adminPanel");
                        }

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        try {
                            GoTravel.setRoot("landing");
                        } catch (IOException ex) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }


    }

    private List<Usuario> getAllUsuarios() {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findAll;usuarios");
                        GoTravel.getSesion().getSalida().flush();

                        String jsonFromServer = GoTravel.getSesion().getEntrada().readUTF();
                        Type type = new TypeToken<List<Usuario>>() {}.getType();
                        List<Usuario> usuarios = gson.fromJson(jsonFromServer, type);

                        for (Usuario u : usuarios) {
                            u.setFoto(getImagenFromUserId(u.getId()));
                        }

                        return usuarios;

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        GoTravel.setRoot("landing");
                        return null;
                    }
                }).get();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }

        return null;

    }

    private byte[] getImagenFromUserId(int id) {

        if(GoTravel.getSesion().getSocket() != null && !GoTravel.getSesion().getSocket().isClosed()) {

            try {
                return Executors.newSingleThreadExecutor().submit(() -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create();

                    try {
                        GoTravel.getSesion().getSalida().writeUTF("findByUserId;imagen;" + id);
                        GoTravel.getSesion().getSalida().flush();

                        if(GoTravel.getSesion().getEntrada().readBoolean()) {
                            int length = GoTravel.getSesion().getEntrada().readInt();
                            byte[] byteArray = new byte[length];
                            GoTravel.getSesion().getEntrada().readFully(byteArray);
                            return byteArray;
                        }

                        return null;

                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        GoTravel.setRoot("landing");
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

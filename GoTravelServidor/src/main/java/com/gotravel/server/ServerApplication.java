package com.gotravel.server;

import com.gotravel.server.service.AppService;
import com.gotravel.server.servidor.HiloCliente;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

@SpringBootApplication
public class ServerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);
    private final AppService service;

    private int puerto;
    private volatile boolean pararServidor;
    private ServerSocket socketServidor;
    private Map<Integer, HiloCliente> clientesConectados;

    public ServerApplication(AppService service) throws IOException {

        this.service = service;

        try {
            this.pararServidor = false;
            Properties conf = new Properties();
            conf.load(new FileInputStream("server.properties"));
            this.puerto = Integer.parseInt(conf.getProperty("PUERTO"));
            this.clientesConectados = new HashMap<>();
        } catch (IOException e) {
            LOG.error("No se han podido leer las propiedades del servidor: {}", e.getMessage());
            throw e;
        }

    }

    public static void main(String[] args) {

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("server.properties"));
        } catch (IOException e) {
            LOG.error("No se pudo cargar el archivo de configuración: {}", e.getMessage());
            return;
        }

        // Comprobación de si existe la base de datos
        String url = props.getProperty("DATABASE_URL");
        String usuario = props.getProperty("MYSQL_USER");
        String contrasena = props.getProperty("MYSQL_PASSWORD");

        ConfigurableApplicationContext context;

        try (Connection connection = DriverManager.getConnection(url, usuario, contrasena)) {
            DatabaseMetaData dbms = connection.getMetaData();
            ResultSet bbdd = dbms.getCatalogs();
            boolean existeBd = false;

            while (bbdd.next()) {
                String nombreBd = bbdd.getString(1);
                if (nombreBd.equals("gotravel")) {
                    LOG.info("La base de datos ya existe");
                    existeBd = true;
                    break;
                }
            }

            // Si no existe ejecuta el script de creación
            if(!existeBd) {
                LOG.warn("Creando base de datos...");
                ejecutarScriptSQL(connection);
                LOG.info("Base de datos creada");
            }

            context = SpringApplication.run(ServerApplication.class, args);
            ServerApplication server = new ServerApplication(context.getBean(AppService.class));
            server.iniciarServidor();

        } catch (SQLException e) {
            LOG.error("Error al conectar con la base de datos: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("No se ha podido iniciar el servidor: {}", e.getMessage());
        }


    }

    private static void ejecutarScriptSQL(Connection connection) throws IOException, SQLException {
        ScriptRunner sr = new ScriptRunner(connection);
        String delimiter = ";";

        try (BufferedReader reader = new BufferedReader(new FileReader("script.sql"))) {
            String line;
            StringBuilder command = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("--")) {
                    if (line.startsWith("DELIMITER")) {
                        delimiter = line.split(" ")[1];
                    } else {
                        command.append(" ").append(line);
                        if (line.endsWith(delimiter)) {
                            sr.runScript(new StringReader(command.toString()));
                            command = new StringBuilder();
                        }
                    }
                }
            }
        }
    }


    public void iniciarServidor() {
        try {
            socketServidor = new ServerSocket(puerto);
            while (!pararServidor) {
                Socket cliente = socketServidor.accept();
                LOG.info("Se ha conectado un usuario");
                new HiloCliente(cliente, service, clientesConectados, this).start();
            }
        } catch (IOException e) {
            if (pararServidor) {
                LOG.info("Cerrando servidor");
            } else {
                LOG.error("Se ha interrumpido la conexión: {}", e.getMessage());
            }
        } finally {
            if (socketServidor != null && !socketServidor.isClosed()) {
                try {
                    socketServidor.close();
                } catch (IOException e) {
                    LOG.error("Error al cerrar el socket del servidor: {}", e.getMessage());
                }
            }
        }
    }

    public void pararServidor() {
        pararServidor = true;
        if (socketServidor != null && !socketServidor.isClosed()) {
            try {
                socketServidor.close();
            } catch (IOException e) {
                LOG.error("Error al cerrar el socket del servidor: {}", e.getMessage());
            }
        }
    }

}

package com.gotravel.server;

import com.gotravel.server.service.AppService;
import com.gotravel.server.servidor.HiloCliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
			this.puerto = Integer.parseInt(conf.getProperty("puerto"));
			this.clientesConectados = new HashMap<>();
		} catch (IOException e) {
            LOG.error("No se han podido leer las propiedades del servidor: {}", e.getMessage());
			throw e;
		}

	}

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);

		try {
			ServerApplication server = new ServerApplication(context.getBean(AppService.class));
			server.iniciarServidor();
		} catch (IOException e) {
			LOG.error("No se ha podido iniciar el servidor {}", e.getMessage());
		}

		context.close();
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

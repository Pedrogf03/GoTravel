package com.gotravel.server;

import com.gotravel.server.service.AppService;
import com.gotravel.server.servidor.HiloCliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

@SpringBootApplication
public class ServerApplication implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);
	private final AppService service;

	private int puerto;
	private boolean pararServidor;
	private ServerSocket socketServidor;
	private List<Socket> clientesConectados;

	public ServerApplication(AppService service) {

		this.service = service;

        try {
			this.pararServidor = false;
			Properties conf = new Properties();
			conf.load(new ClassPathResource("application.properties").getInputStream());
			this.puerto = Integer.parseInt(conf.getProperty("puerto"));
			this.clientesConectados = new ArrayList<>();
		} catch (IOException e) {
            LOG.error("No se han podido leer las propiedades del servidor: {}", e.getMessage());
		}

	}

	private void pararServidor() {
		pararServidor = true;
		try {
			socketServidor.close();
		} catch (IOException e) {
            LOG.error("Se ha perdido la conexión con un cliente: {}", e.getMessage());
		}
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);

		ServerApplication server = new ServerApplication(context.getBean(AppService.class));
		Thread hiloServidor = new Thread(server);
		hiloServidor.start();

		Scanner sc = new Scanner(System.in);
		LOG.warn("Pulsa (s) para parar el servidor");
		String res = sc.nextLine();
		while (!res.equalsIgnoreCase("s")) {
			res = sc.nextLine();
		}

		server.pararServidor();
		sc.close();

		context.close();
	}

	public void iniciarServidor() {

		try(ServerSocket server = new ServerSocket(puerto)) {
			socketServidor = server;
			while(!pararServidor) {
				Socket cliente = socketServidor.accept();
				clientesConectados.add(cliente);
				LOG.info("Se ha conectado un usuario");
				HiloCliente hilo = new HiloCliente(cliente, service);
				hilo.start();
			}
		} catch (IOException e) {
            LOG.error("Se ha interrumpido la conexión: {}", e.getMessage());
		}

	}

	@Override
	public void run() {
		iniciarServidor();
	}

}

package com.gotravel;

import com.gotravel.server.HiloInicioSesion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

@SpringBootApplication
public class ServidorApplication implements Runnable {

	private static Logger LOG = LoggerFactory.getLogger(ServidorApplication.class);
	private int puerto;
	private boolean pararServidor;
	private ServerSocket socketServidor;

	public ServidorApplication() {

		try {
			pararServidor = false;
			Properties conf = new Properties();
			conf.load(new FileInputStream("server.properties"));
			puerto = Integer.parseInt(conf.getProperty("PUERTO"));
		} catch (IOException e) {
			LOG.error("No se han podido leer las propiedades del servidor: " + e.getMessage());
		}

	}

	private void pararServidor() {
		pararServidor = true;
		try {
			socketServidor.close();
		} catch (IOException e) {
			LOG.error("Se ha perdido la conexión con un cliente: " + e.getMessage());
		}
	}

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(ServidorApplication.class, args);
		ServidorApplication server = new ServidorApplication();
		Thread hiloServidor = new Thread(server);
		hiloServidor.start();

		Scanner sc = new Scanner(System.in);
		System.out.println("Pulsa (s) para parar el servidor");
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
				new Thread(() -> {
					HiloInicioSesion login = new HiloInicioSesion(cliente);
					login.start();
					try {
						login.join();
					} catch (InterruptedException e) {
						LOG.warn("Un cliente no ha iniciado sesión correctamente: " + e.getMessage());
					}
					if(login.correcto()) {
						LOG.info("Un cliente ha iniciado sesión correctamente");
					}
				}).start();
			}
		} catch (IOException e) {
			LOG.error("Se ha interrumpido la conexión: " + e.getMessage());
		}

	}

	@Override // Run de Runnable
	public void run() {
		iniciarServidor();
	}
}

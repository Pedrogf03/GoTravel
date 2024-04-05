package com.gotravel.servidor;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Servidor implements Runnable{

    private int puerto;
    private boolean pararServidor;
    private ServerSocket socketServidor;

    public Servidor() {

        try {
            pararServidor = false;
            Properties conf = new Properties();
            conf.load(new FileInputStream("server.properties"));
            puerto = Integer.parseInt(conf.getProperty("PUERTO"));
        } catch (IOException e) {
            System.out.println("No se han podido leer las propiedades del servidor");
        }

    }

    public void pararServidor() {
        pararServidor = true;
        try {
            socketServidor.close();
        } catch (IOException e) {
            //ignore
        }
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
                        System.out.println("El cliente no ha iniciado sesión correctamente");
                    }
                    if(login.correcto()) {
                        System.out.println("El cliente ha iniciado sesión correctamente");
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Se ha interrumpido la conexion");
        }

    }

    public static void main(String[] args) {

        Servidor server = new Servidor();
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

    }

    @Override
    public void run() {
        iniciarServidor();
    }

}
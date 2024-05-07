package com.gotravel.server.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.server.ServerApplication;
import com.gotravel.server.model.Usuario;
import com.gotravel.server.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class HiloCliente extends Thread {

    private final Socket cliente;
    private final Protocolo protocolo;
    private final AppService service;
    private final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    private boolean sesionIniciada;

    public HiloCliente(Socket cliente, AppService service) {
        this.cliente = cliente;
        this.service = service;
        this.protocolo = new Protocolo();
        this.sesionIniciada = false;
    }

    @Override
    public void run() {

        DataOutputStream salida = null;
        DataInputStream entrada = null;

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        try {

            salida = new DataOutputStream(cliente.getOutputStream());
            entrada = new DataInputStream(cliente.getInputStream());

            while (!sesionIniciada) {

                // CREDENCIALES -> COMPROBACION
                protocolo.procesarMensaje(null);

                String output;
                String[] fromCliente = entrada.readUTF().split(";");

                String opcion = fromCliente[0];
                String email = fromCliente[1];
                String contrasena = fromCliente[2];

                if(opcion.equalsIgnoreCase("login")) {

                    Usuario u = service.findUsuarioByEmail(email);

                    // COMPROBACION -> SESION_INICIADA || CREDENCIALES
                    output = protocolo.procesarMensaje("" + (u != null && u.getContrasena().equals(contrasena)));
                    if(output.equalsIgnoreCase("correcto")){
                        sesionIniciada = true;
                        String json = gson.toJson(u);
                        System.out.println(json);
                        salida.writeUTF(json);
                    } else {
                        salida.writeUTF("");
                    }

                } else if (opcion.equalsIgnoreCase("registro")) {

                    String nombre = fromCliente[3];

                    Usuario u = service.saveUsuario(new Usuario(nombre, email, contrasena));

                    // COMPROBACION -> CREDENCIALES
                    output = protocolo.procesarMensaje("" + (u != null));
                    System.out.println(output);
                    if(output.equalsIgnoreCase("correcto")){
                        sesionIniciada = true;
                        String json = gson.toJson(u);
                        salida.writeUTF(json);
                    } else {
                        salida.writeUTF("");
                    }

                }

            }

        } catch (IOException e) {
            LOG.warn("Se ha desconectado un cliente");
        }

    }

}

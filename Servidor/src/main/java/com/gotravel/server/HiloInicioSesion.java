package com.gotravel.server;

import com.gotravel.model.Cliente;
import com.gotravel.repository.ClienteRepository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloInicioSesion extends Thread {

    private final Socket cliente;
    private boolean sesionIniciada;
    private final ClienteRepository repo;
    private final Protocolo protocolo;

    public HiloInicioSesion(Socket cliente) {
        this.cliente = cliente;
        this.repo = new ClienteRepository();
        protocolo = new Protocolo();
    }

    public boolean correcto() {
        return sesionIniciada;
    }

    @Override
    public void run() {

        DataInputStream entrada = null;
        DataOutputStream salida = null;

        try {

            entrada = new DataInputStream(cliente.getInputStream());
            salida = new DataOutputStream(cliente.getOutputStream());

            while (!sesionIniciada) {

                // CREDENCIALES -> COMPROBACION
                protocolo.procesarMensaje(null);

                String output;
                String[] fromCliente = entrada.readUTF().split(";");

                String opcion = fromCliente[0];
                String email = fromCliente[1];
                String passwd = fromCliente[2];

                if(opcion.equalsIgnoreCase("login")) {

                    Cliente c = repo.getClienteByEmail(email);

                    // COMPROBACION -> SESION_INICIADA || CREDENCIALES
                    output = protocolo.procesarMensaje("" + (c != null && c.getPassword().equals(passwd)));
                    salida.writeUTF(output);
                    if(output.equalsIgnoreCase("correcto")){
                        sesionIniciada = true;
                    }

                } else if (opcion.equalsIgnoreCase("registro")) {

                    String nombre = fromCliente[3];

                    Integer idUsuario = repo.addCliente(new Cliente(nombre, email, passwd));
                    // COMPROBACION -> CREDENCIALES
                    output = protocolo.procesarMensaje("" + (idUsuario != null));
                    salida.writeUTF(output);
                    if(output.equalsIgnoreCase("correcto")){
                        sesionIniciada = true;
                    }

                }

            }

        } catch (IOException e) {
            System.out.println("No se puede conectar con el cliente");
        }

    }

}

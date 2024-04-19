package com.gotravel.server;

import com.gotravel.model.Cliente;
import com.gotravel.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloInicioSesion extends Thread {

    private Socket cliente;
    private boolean sesionIniciada;
    private ClienteRepository repo;

    public HiloInicioSesion(Socket cliente) {
        this.cliente = cliente;
        this.repo = new ClienteRepository();
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

                String[] fromCliente = entrada.readUTF().split(";");

                if(fromCliente[0].equalsIgnoreCase("login")) {

                    String email = fromCliente[1];
                    String passwd = fromCliente[2];

                    Cliente c = repo.getClienteByEmail(email);

                    if(c != null && c.getPassword().equals(passwd)) {
                        salida.writeBoolean(true);
                        sesionIniciada = true;
                    } else {
                        salida.writeBoolean(false);
                    }

                } else if (fromCliente[0].equalsIgnoreCase("registro")) {

                    String email = fromCliente[1];
                    String passwd = fromCliente[2];
                    String nombre = fromCliente[3];

                    Integer idUsuario = repo.addCliente(new Cliente(nombre, email, passwd));

                    salida.writeBoolean(idUsuario != null);
                    if(idUsuario != null) {
                        sesionIniciada = true;
                    }

                }

            }

        } catch (IOException e) {
            System.out.println("No se puede conectar con el cliente");
        }

    }

}

package com.gotravel.servidor;

import com.gotravel.dao.DAOUsuario;
import com.gotravel.entity.Usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloInicioSesion extends Thread {

    private Socket cliente;
    private boolean sesionIniciada;
    private DAOUsuario daoUsuario;

    public HiloInicioSesion(Socket cliente) {
        this.cliente = cliente;
        daoUsuario = new DAOUsuario();
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

                    Usuario u = daoUsuario.getUsuario(email);

                    if(u != null && u.getPassword().equals(passwd)) {
                        salida.writeBoolean(true);
                        sesionIniciada = true;
                    } else {
                        salida.writeBoolean(false);
                    }

                } else if (fromCliente[0].equalsIgnoreCase("registro")) {

                    String email = fromCliente[1];
                    String passwd = fromCliente[2];
                    String usuario = fromCliente[3];

                    Integer idUsuario = daoUsuario.addUsuario(usuario, email, passwd);

                    salida.writeBoolean(idUsuario != null);

                }

            }

        } catch (IOException e) {
            System.out.println("No se puede conectar con el cliente");
        }

    }

}

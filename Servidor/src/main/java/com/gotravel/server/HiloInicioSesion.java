package com.gotravel.server;

import com.gotravel.model.Usuario;
import com.gotravel.repository.UsuarioRepository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloInicioSesion extends Thread {

    private final Socket cliente;
    private boolean sesionIniciada;
    private final Protocolo protocolo;
    private final UsuarioRepository repo;

    public HiloInicioSesion(Socket cliente) {
        this.cliente = cliente;
        this.repo = new UsuarioRepository();
        this.protocolo = new Protocolo();
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
                String contrasena = fromCliente[2];

                if(opcion.equalsIgnoreCase("login")) {

                    Usuario u = repo.findUsuarioByEmail(email);

                    // COMPROBACION -> SESION_INICIADA || CREDENCIALES
                    output = protocolo.procesarMensaje("" + (u != null && u.getContrasena().equals(contrasena)));
                    if(output.equalsIgnoreCase("correcto")){
                        sesionIniciada = true;
                        salida.writeUTF(output + ";" + u.getId());
                    } else {
                        salida.writeUTF(output);
                    }

                } else if (opcion.equalsIgnoreCase("registro")) {

                    String nombre = fromCliente[3];

                    Integer idUsuario = repo.save(new Usuario(nombre, email, contrasena));

                    // COMPROBACION -> CREDENCIALES
                    output = protocolo.procesarMensaje("" + (idUsuario != null));
                    if(output.equalsIgnoreCase("correcto")){
                        sesionIniciada = true;
                        salida.writeUTF(output + ";" + idUsuario);
                    } else {
                        salida.writeUTF(output);
                    }

                }

            }

        } catch (IOException e) {
            System.out.println("No se puede conectar con el cliente");
        }

    }

}

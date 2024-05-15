package com.gotravel.server.servidor;

import com.gotravel.server.model.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

@Getter
@Setter
public class Sesion {

    private Usuario usuario;
    private Socket cliente;
    private DataInputStream entrada;
    private DataOutputStream salida;

    public Sesion(Socket cliente) {
        this.cliente = cliente;
    }

}

package com.gotravel.gotraveldesktop.Utils;

import com.gotravel.gotraveldesktop.Model.Usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Sesion {

    private Socket socket;
    private Usuario usuario;
    private DataInputStream entrada;
    private DataOutputStream salida;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataOutputStream getSalida() {
        return salida;
    }

    public void setSalida(DataOutputStream salida) {
        this.salida = salida;
    }

    public DataInputStream getEntrada() {
        return entrada;
    }

    public void setEntrada(DataInputStream entrada) {
        this.entrada = entrada;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

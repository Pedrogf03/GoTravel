package com.gotravel;

import com.gotravel.dao.DAOUsuario;
import com.gotravel.entity.Usuario;
import com.gotravel.protocolo.ProtocoloInicioSesion;

import java.net.Socket;

public class HiloInicioSesion extends Thread {

    private Socket cliente;
    private ProtocoloInicioSesion protocolo;
    private boolean sesionIniciada;
    private DAOUsuario dao;
    private Usuario usuario;

    public HiloInicioSesion(Socket cliente, ProtocoloInicioSesion protocoloInicioSesion) {
        this.cliente = cliente;
        this.protocolo = protocoloInicioSesion;
    }

    public boolean correcto() {
        return sesionIniciada;
    }

    @Override
    public void run() {

    }
}

package com.gotravel.protocolo;

import lombok.Getter;

abstract public class Protocolo {

    @Getter
    protected Estado estado;
    protected String mensaje;

    public Protocolo(Estado estado) {
        this.estado = estado;
    }

    abstract public String procesarMensaje(String entrada);

}

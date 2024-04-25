package com.gotravel.server;

public class Protocolo {

    String mensaje;
    Estado estado;

    public Protocolo() {
        estado = Estado.CREDENCIALES;
    }

    public String procesarMensaje(String entrada) {

        if(estado == Estado.CREDENCIALES) {
            mensaje = "credenciales";
            estado = Estado.COMPROBACION;
        } else if(estado == Estado.COMPROBACION) {
            if(entrada != null){
                if(entrada.equalsIgnoreCase("true")) {
                    estado = Estado.SESION_INICIADA;
                    mensaje = "correcto";
                } else {
                    estado = Estado.CREDENCIALES;
                    mensaje = "incorrecto";
                }
            }
        } else if(estado == Estado.SESION_INICIADA) {
            mensaje = "sesion_iniciada";
        }


        return mensaje;
    }

}

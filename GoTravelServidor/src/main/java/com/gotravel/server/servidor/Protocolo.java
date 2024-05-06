package com.gotravel.server.servidor;

public class Protocolo {

    String mensaje;
    Estado estado;

    public Protocolo() {
        estado = Estado.CREDENCIALES;
    }

    public String procesarMensaje(String entrada) {

        switch (estado) {
            case CREDENCIALES:
                mensaje = "credenciales";
                estado = Estado.COMPROBACION;
                break;
            case COMPROBACION:
                if(entrada.equalsIgnoreCase("true")) {
                    estado = Estado.SESION_INICIADA;
                    mensaje = "correcto";
                } else {
                    estado = Estado.CREDENCIALES;
                    mensaje = "incorrecto";
                }
                break;
        }


        return mensaje;
    }

}

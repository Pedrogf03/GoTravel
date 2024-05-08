package com.gotravel.server.servidor;

public class Protocolo {

    String mensaje;
    Estado estado;

    public Protocolo() {
        estado = Estado.ESPERANDO_CREDENCIALES;
    }

    public String procesarMensaje(String entrada) {

        switch (estado) {
            case ESPERANDO_CREDENCIALES:
                mensaje = "credenciales";
                estado = Estado.COMPROBANDO_CREDENCIALES;
                break;
            case COMPROBANDO_CREDENCIALES:
                if(entrada.equalsIgnoreCase("true")) {
                    estado = Estado.ATENDIENDO_PETICIONES;
                    mensaje = "login";
                } else {
                    estado = Estado.ESPERANDO_CREDENCIALES;
                    mensaje = "retry";
                }
                break;
            case ATENDIENDO_PETICIONES:
                if(entrada.equalsIgnoreCase("chatear")) {
                    mensaje = "chatear";
                } else {
                    mensaje = "peticion";
                }
                break;
        }


        return mensaje;
    }

}

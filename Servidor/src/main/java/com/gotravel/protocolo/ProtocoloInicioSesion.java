package com.gotravel.protocolo;

public class ProtocoloInicioSesion extends Protocolo {

    public ProtocoloInicioSesion() {
        super(Estado.INICIAR_SESION);
    }

    @Override
    public String procesarMensaje(String entrada) {

        switch (estado) {
            case INICIAR_SESION:
                if(entrada != null) {
                    mensaje = "iniciar_sesion";
                }
                break;

            case REGISTRARSE:
                mensaje = "registrarse";
                break;

            case COMPROBAR:
                mensaje = "comprobar";
                break;
        }

        return mensaje;

    }

}

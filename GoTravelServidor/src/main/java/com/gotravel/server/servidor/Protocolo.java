package com.gotravel.server.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.server.model.Usuario;

public class Protocolo {

    String mensaje;
    Estado estado;

    public Protocolo() {
        this.estado = Estado.INICIANDO_SESION;
    }

    public String procesarMensaje(String entrada) {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .setLenient()
                .create();

        String[] fromCliente;

        switch (estado) {
            case INICIANDO_SESION:

                fromCliente = entrada.split(";");

                Usuario u = gson.fromJson(fromCliente[0], Usuario.class);

                if(u != null && u.getContrasena().equals(fromCliente[1])) {

                    if (u.getOculto().equals("1")){
                        mensaje = "oculto";
                    } else {
                        mensaje = gson.toJson(u);
                        estado = Estado.ATENDIENDO_PETICIONES;
                    }

                } else {
                    mensaje = "reintentar";
                }

                break;
            case ATENDIENDO_PETICIONES:

                if(entrada.equals("chat")) {
                    estado = Estado.CHATEANDO;
                    mensaje = "chateando";
                } else if(entrada.equals("cerrarSesion")) {
                    estado = Estado.FINALIZADO;
                    mensaje = "finHilo";
                } else if(entrada.equals("cerrarServidor")) {
                    estado = Estado.FINALIZADO;
                    mensaje = "finServer";
                } else {
                    mensaje = "peticion";
                }

                break;
            case CHATEANDO:

                if(entrada.equals("dejarChat")) {
                    estado = Estado.ATENDIENDO_PETICIONES;
                    mensaje = "peticion";
                } else if(entrada.equals("cerrarSesion")) {
                    estado = Estado.FINALIZADO;
                    mensaje = "finServer";
                } else {
                    mensaje = "chat";
                }

                break;
        }


        return mensaje;
    }

}

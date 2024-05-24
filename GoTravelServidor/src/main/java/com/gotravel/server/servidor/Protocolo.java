package com.gotravel.server.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gotravel.server.model.Usuario;
import com.gotravel.server.service.AppService;

public class Protocolo {

    String mensaje;
    Estado estado;

    public Protocolo(AppService service) {
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
                    mensaje = gson.toJson(u);
                    estado = Estado.ATENDIENDO_PETICIONES;
                } else {
                    mensaje = "reintentar";
                }

                break;
            case ATENDIENDO_PETICIONES:

                if(entrada.equals("chat")) {
                    //TODO
                } else if(entrada.equals("cerrarSesion")) {
                    estado = Estado.FINALIZADO;
                    mensaje = "finalizado";
                } else {
                    mensaje = "peticion";
                }

                break;
        }


        return mensaje;
    }

}

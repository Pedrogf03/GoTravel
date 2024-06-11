package com.gotravel.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    private int id;
    private Usuario emisor;
    private Usuario receptor;
    private String texto;
    private String fecha;
    private String hora;

    public Mensaje(Usuario emisor, Usuario receptor, String texto, String fecha, String hora) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.texto = texto;
        this.fecha = fecha;
        this.hora = hora;
    }

    public Mensaje(String texto, String fecha, String hora) {
        this.texto = texto;
        this.fecha = fecha;
        this.hora = hora;
    }
}

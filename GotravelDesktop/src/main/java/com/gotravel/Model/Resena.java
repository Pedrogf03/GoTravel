package com.gotravel.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    private ResenaId id;
    private Usuario usuario;
    private int puntuacion;
    private String contenido;
    private String oculto;

    public Resena(ResenaId id, int puntuacion, String contenido, String oculto) {
        this.id = id;
        this.puntuacion = puntuacion;
        this.contenido = contenido;
        this.oculto = oculto;
    }
}

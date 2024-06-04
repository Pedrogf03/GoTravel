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

}

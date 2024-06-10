package com.gotravel.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {

    private int id;
    private String linea1;
    private String linea2;
    private String ciudad;
    private String estado;
    private String pais;
    private String cp;

    public Direccion(String linea1, String linea2, String ciudad, String estado, String pais, String cp) {
        this.linea1 = linea1;
        this.linea2 = linea2;
        this.ciudad = ciudad;
        this.estado = estado;
        this.pais = pais;
        this.cp = cp;
    }
}

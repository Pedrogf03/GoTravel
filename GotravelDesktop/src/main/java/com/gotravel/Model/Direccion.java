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

}

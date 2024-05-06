package com.gotravel.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "direccion")
public class Direccion extends Localizacion {

    @Column(name = "calle", nullable = false, length = 100)
    private String calle;

    @Column(name = "numero", nullable = false, length = 5)
    private String numero;

    @Column(name = "ciudad", nullable = false, length = 50)
    private String ciudad;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "cp", nullable = false, length = 5, columnDefinition = "CHAR")
    private String cp;

}
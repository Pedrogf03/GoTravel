package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @Column(name = "id_direccion", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "linea1", nullable = false, length = 200)
    @Expose
    private String linea1;

    @Column(name = "linea2", length = 200)
    @Expose
    private String linea2;

    @Column(name = "ciudad", nullable = false, length = 100)
    @Expose
    private String ciudad;

    @Column(name = "estado", nullable = false, length = 100)
    @Expose
    private String estado;

    @Column(name = "pais", nullable = false, length = 100)
    @Expose
    private String pais;

    @Column(name = "cp", nullable = false, length = 5, columnDefinition = "CHAR")
    @Expose
    private String cp;

}
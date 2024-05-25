package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dir_facturacion")
public class DirFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facturacion", nullable = false)
    @Expose
    private Integer id;

    @Column(name = "line1", nullable = false, length = 45)
    @Expose
    private String line1;

    @Column(name = "ciudad", nullable = false, length = 45)
    @Expose
    private String ciudad;

    @Column(name = "estado", nullable = false, length = 45)
    @Expose
    private String estado;

    @Column(name = "cp", nullable = false, length = 5, columnDefinition = "CHAR")
    @Expose
    private String cp;

    @Column(name = "codigo_pais", nullable = false, length = 45)
    @Expose
    private String codigoPais;

}
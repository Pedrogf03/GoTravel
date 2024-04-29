package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @Column(name = "idLocalizacion", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idLocalizacion", nullable = false)
    private Localizacion localizacion;

    @Column(name = "Calle", nullable = false, length = 100)
    private String calle;

    @Column(name = "Numero", nullable = false, length = 5)
    private String numero;

    @Column(name = "Ciudad", nullable = false, length = 50)
    private String ciudad;

    @Column(name = "Estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "CP", nullable = false, length = 5)
    private String cp;

}
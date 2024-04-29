package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "ubicacion")
public class Ubicacion {

    @Id
    @Column(name = "idLocalizacion", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idLocalizacion", nullable = false)
    private Localizacion localizacion;

    @Column(name = "CoordenadaX", nullable = false, precision = 9, scale = 6)
    private BigDecimal coordenadaX;

    @Column(name = "CoordenadaY", nullable = false, precision = 9, scale = 6)
    private BigDecimal coordenadaY;

}
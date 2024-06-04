package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "ubicacion")
public class Ubicacion extends Localizacion {

    @Column(name = "coordenada_x", nullable = false, precision = 9, scale = 6)
    private BigDecimal coordenadaX;

    @Column(name = "coordenada_y", nullable = false, precision = 9, scale = 6)
    private BigDecimal coordenadaY;

}
package com.gotravel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ubicacion")
public class Ubicacion extends Localizacion {

    @NonNull
    @Column(name = "CoordenadaX", nullable = false, precision = 9, scale = 6)
    private BigDecimal coordenadaX;

    @NonNull
    @Column(name = "CoordenadaY", nullable = false, precision = 9, scale = 6)
    private BigDecimal coordenadaY;

}
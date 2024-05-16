package com.gotravel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tarjetacredito")
public class Tarjetacredito extends Metodopago {

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "ultimos_digitos", nullable = false, length = 4, columnDefinition = "CHAR")
    private String ultimosDigitos;

}
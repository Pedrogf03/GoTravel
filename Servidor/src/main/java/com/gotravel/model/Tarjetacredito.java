package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tarjetacredito")
public class Tarjetacredito extends Metodopago{

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idMetodoPago", nullable = false)
    private Metodopago metodopago;

    @Column(name = "FechaVencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "Nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "Ultimos4Digitos", nullable = false, length = 4)
    private String ultimos4Digitos;

}
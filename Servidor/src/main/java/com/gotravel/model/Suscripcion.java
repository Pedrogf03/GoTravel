package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "suscripcion")
public class Suscripcion {

    @Id
    @Column(name = "idSuscripcion", nullable = false)
    private Integer id;

    @Column(name = "FechaIni", nullable = false)
    private LocalDate fechaIni;

    @Column(name = "FechaFin", nullable = false)
    private LocalDate fechaFin;

    @Lob
    @Column(name = "Estado", nullable = false)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idPago", nullable = false)
    private Pago pago;

}
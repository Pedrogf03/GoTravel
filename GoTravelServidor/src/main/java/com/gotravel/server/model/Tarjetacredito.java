package com.gotravel.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tarjetacredito")
public class Tarjetacredito extends Metodopago {
    @Id
    @Column(name = "id_metodopago", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_metodopago", nullable = false)
    private Metodopago metodopago;

    @Column(name = "fecha_vencimiento", nullable = false, columnDefinition = "DATE")
    private String fechaVencimiento;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "ultimos_digitos", nullable = false, length = 4, columnDefinition = "CHAR")
    private String ultimosDigitos;

}
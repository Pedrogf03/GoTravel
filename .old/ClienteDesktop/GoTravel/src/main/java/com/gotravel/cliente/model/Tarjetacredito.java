package com.gotravel.cliente.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tarjetacredito")
public class Tarjetacredito extends Metodopago {

    @NonNull
    @Column(name = "FechaVencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @NonNull
    @Column(name = "Nombre", nullable = false, length = 200)
    private String nombre;

    @NonNull
    @Column(name = "Ultimos4Digitos", nullable = false, length = 4)
    private String ultimos4Digitos;

}
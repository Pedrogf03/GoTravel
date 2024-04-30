package com.gotravel.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor // Constructor con aquellos atributos que son NonNull
@Entity
@Table(name = "pago")
public class Pago implements Serializable {

    @Id
    @Column(name = "idPago", nullable = false)
    private Integer id;

    @NonNull
    @Column(name = "Coste", nullable = false)
    private Double coste;

    @NonNull
    @Column(name = "Fecha", nullable = false)
    private LocalDate fecha;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idMetodoPago", nullable = false)
    private Metodopago metodoPago;

}
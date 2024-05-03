package com.gotravel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suscripcion")
public class Suscripcion {

    @Id
    @Column(name = "id_suscripcion", nullable = false)
    private Integer id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_final", nullable = false)
    private LocalDate fechaFinal;

    @Lob
    @Column(name = "estado", nullable = false, columnDefinition = "ENUM('activa', 'inactiva')")
    private String estado;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

}
package com.gotravel.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "suscripcion")
public class Suscripcion {

    @Id
    @Column(name = "id_suscripcion", nullable = false)
    private Integer id;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    private String fechaInicio;

    @Column(name = "fecha_final", nullable = false, columnDefinition = "DATE")
    private String fechaFinal;

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
package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
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
    @Expose
    private String id;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaInicio;

    @Column(name = "fecha_final", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaFinal;

    @Column(name = "estado", nullable = false)
    @Expose
    private String estado;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pago", nullable = false)
    @Expose
    private Pago pago;

}
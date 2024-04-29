package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "resena")
public class Resena {

    @EmbeddedId
    private ResenaId id;

    @MapsId("idUsuario")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @MapsId("idContratacion")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idContratacion", nullable = false)
    private Contratacion contratacion;

    @Column(name = "Puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "Contenido", nullable = false, length = 200)
    private String contenido;

    @Lob
    @Column(name = "oculto", nullable = false)
    private String oculto;

}
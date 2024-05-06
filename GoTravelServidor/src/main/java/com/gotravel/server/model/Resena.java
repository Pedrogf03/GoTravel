package com.gotravel.server.model;

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
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @MapsId("idContratacion")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_contratacion", nullable = false)
    private Contratacion contratacion;

    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "contenido", nullable = false, length = 200)
    private String contenido;

    @Lob
    @Column(name = "oculto", nullable = false, columnDefinition = "ENUM('0', '1')")
    private String oculto;

}
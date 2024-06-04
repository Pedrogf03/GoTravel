package com.gotravel.cliente.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor // Constructor con aquellos atributos que son NonNull
@Entity
@Table(name = "resena")
public class Resena implements Serializable {

    @EmbeddedId
    private ResenaId id;

    @NonNull
    @MapsId("idUsuario")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @NonNull
    @MapsId("idContratacion")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idContratacion", nullable = false)
    private Contratacion contratacion;

    @NonNull
    @Column(name = "Puntuacion", nullable = false)
    private Integer puntuacion;

    @NonNull
    @Column(name = "Contenido", nullable = false, length = 200)
    private String contenido;

    @NonNull
    @Lob
    @Column(name = "oculto", nullable = false)
    private String oculto;

}
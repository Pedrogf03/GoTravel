package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "resena")
public class Resena {

    @EmbeddedId
    @Expose
    private ResenaId id;

    @MapsId("idUsuario")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @Expose
    private Usuario usuario;

    @MapsId("idServicio")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @Column(name = "puntuacion", nullable = false)
    @Expose
    private Integer puntuacion;

    @Column(name = "contenido", nullable = false, length = 500)
    @Expose
    private String contenido;

    @Lob
    @Column(name = "oculto", nullable = false, columnDefinition = "ENUM('0', '1')")
    private String oculto;

}
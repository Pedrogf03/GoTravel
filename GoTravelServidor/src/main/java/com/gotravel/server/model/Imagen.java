package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "imagen")
public class Imagen {

    @Id
    @Column(name = "id_imagen", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @Column(name = "imagen", nullable = false)
    private byte[] imagen;

    public Imagen(byte[] imagen, Servicio s) {
        this.imagen = imagen;
        this.servicio = s;
    }
}
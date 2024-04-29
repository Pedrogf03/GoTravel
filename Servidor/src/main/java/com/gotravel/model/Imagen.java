package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "imagen")
public class Imagen {

    @Id
    @Column(name = "idServicio", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idServicio", nullable = false)
    private Servicio servicio;

    @Column(name = "Imagen", nullable = false)
    private byte[] imagen;

}
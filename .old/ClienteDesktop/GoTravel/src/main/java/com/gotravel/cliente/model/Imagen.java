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
@Table(name = "imagen")
public class Imagen implements Serializable {

    @Id
    @Column(name = "idServicio", nullable = false)
    private Integer id;

    @NonNull
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idServicio", nullable = false)
    private Servicio servicio;

    @Column(name = "Imagen", nullable = false)
    private byte @NonNull [] imagen;

}
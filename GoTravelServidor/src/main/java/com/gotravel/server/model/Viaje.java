package com.gotravel.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "viaje")
public class Viaje {

    @Id
    @Column(name = "id_viaje", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    private String fechaInicio;

    @Column(name = "fecha_fin", columnDefinition = "DATE")
    private String fechaFin;

    @Column(name = "coste_total", nullable = false)
    private Double costeTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private com.gotravel.server.model.Usuario usuario;

    @OneToMany(mappedBy = "viaje")
    private List<Etapa> etapas;

}
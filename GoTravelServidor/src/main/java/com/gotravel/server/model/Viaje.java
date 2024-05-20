package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    @Expose
    private String nombre;

    @Column(name = "descripcion", length = 150)
    @Expose
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaInicio;

    @Column(name = "fecha_fin", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaFin;

    @Column(name = "coste_total", nullable = false)
    @Expose
    private Double costeTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "viaje", fetch = FetchType.EAGER)
    @OrderBy("fechaInicio ASC")
    @Expose
    private List<Etapa> etapas;

}
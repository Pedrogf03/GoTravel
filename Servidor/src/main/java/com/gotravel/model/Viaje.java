package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "viaje")
public class Viaje {

    @Id
    @Column(name = "idViaje", nullable = false)
    private Integer id;

    @Column(name = "Nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "Descripcion", nullable = false, length = 150)
    private String descripcion;

    @Column(name = "FechaIni", nullable = false)
    private LocalDate fechaIni;

    @Column(name = "FechaFin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "CosteTotal", nullable = false)
    private Double costeTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idViaje")
    private List<Etapa> etapas;

}
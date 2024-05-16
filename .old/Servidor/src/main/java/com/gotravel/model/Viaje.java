package com.gotravel.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "viaje")
public class Viaje {

    @Id
    @Column(name = "id_viaje", nullable = false)
    @JsonView(Views.Viaje.class)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    @JsonView(Views.Viaje.class)
    private String nombre;

    @Column(name = "descripcion", length = 150)
    @JsonView(Views.Viaje.class)
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    @JsonView(Views.Viaje.class)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    @JsonView(Views.Viaje.class)
    private LocalDate fechaFin;

    @Column(name = "coste_total", nullable = false)
    @JsonView(Views.Viaje.class)
    private Double costeTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "viaje")
    private List<Etapa> etapas;

    public Viaje(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, Double costeTotal) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costeTotal = costeTotal;
    }
}
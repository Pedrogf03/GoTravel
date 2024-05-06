package com.gotravel.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "etapa")
public class Etapa {

    @Id
    @Column(name = "id_etapa", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    private String fechaInicio;

    @Column(name = "fecha_final", nullable = false, columnDefinition = "DATE")
    private String fechaFinal;

    @Column(name = "coste_total", nullable = false)
    private Double costeTotal;

    @Lob
    @Column(name = "tipo", nullable = false, columnDefinition = "ENUM('transporte', 'estancia')")
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_viaje", nullable = false)
    private Viaje viaje;

    @OneToMany(mappedBy = "etapa")
    private List<Contratacion> contrataciones;

}
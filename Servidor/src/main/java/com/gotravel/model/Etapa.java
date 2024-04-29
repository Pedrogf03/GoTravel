package com.gotravel.model;

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
    @Column(name = "idEtapa", nullable = false)
    private Integer id;

    @Column(name = "Nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "FechaIni", nullable = false)
    private LocalDate fechaIni;

    @Column(name = "FechaFin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "CosteTotal", nullable = false)
    private Double costeTotal;

    @Lob
    @Column(name = "Tipo", nullable = false)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idViaje", nullable = false)
    private Viaje viaje;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idEtapa")
    private List<Contratacion> contrataciones;

}
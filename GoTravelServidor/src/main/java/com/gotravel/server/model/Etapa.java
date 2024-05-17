package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "etapa")
public class Etapa {

    @Id
    @Column(name = "id_etapa", nullable = false)
    @Expose
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    @Expose
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaInicio;

    @Column(name = "fecha_final", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaFinal;

    @Column(name = "coste_total", nullable = false)
    @Expose
    private Double costeTotal;

    @Lob
    @Column(name = "tipo", nullable = false, columnDefinition = "ENUM('transporte', 'estancia')")
    @Expose
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_viaje", nullable = false)
    private Viaje viaje;

    @OneToMany(mappedBy = "etapa")
    @Expose
    private List<Contratacion> contrataciones;

}
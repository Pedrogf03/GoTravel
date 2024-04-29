package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "servicio")
public class Servicio {

    @Id
    @Column(name = "idServicio", nullable = false)
    private Integer id;

    @Column(name = "Nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "Descripcion", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "Precio", nullable = false)
    private Double precio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NombreTipoServicio", nullable = false)
    private Tiposervicio tipoServicio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idLocalizacion", nullable = false)
    private Localizacion localizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario profesional;

    @Lob
    @Column(name = "oculto", nullable = false)
    private String oculto;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idServicio")
    private List<Imagen> imagenes;

}
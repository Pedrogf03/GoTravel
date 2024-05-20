package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "servicio")
public class Servicio {

    @Id
    @Column(name = "id_servicio", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaInicio;

    @Column(name = "fecha_final", columnDefinition = "DATE")
    @Expose
    private String fechaFinal;

    @Column(name = "hora", columnDefinition = "TIME")
    private String hora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_servicio", nullable = false)
    private Tiposervicio tipoServicio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_localizacion", nullable = false)
    private Localizacion localizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Lob
    @Column(name = "oculto", nullable = false, columnDefinition = "ENUM('0', '1')")
    private String oculto;

}
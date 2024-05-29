package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "servicio")
@ToString
public class Servicio {

    @Id
    @Column(name = "id_servicio", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    @Expose
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 500)
    @Expose
    private String descripcion;

    @Column(name = "precio", nullable = false)
    @Expose
    private Double precio;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaInicio;

    @Column(name = "fecha_final", columnDefinition = "DATE")
    @Expose
    private String fechaFinal;

    @Column(name = "hora", columnDefinition = "TIME")
    @Expose
    private String hora;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tipo_servicio", nullable = false)
    @Expose
    private Tiposervicio tipoServicio;

    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_direccion", nullable = false)
    @Expose
    private Direccion direccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "servicio", fetch = FetchType.EAGER)
    private List<Imagen> imagenes;

    @Lob
    @Column(name = "oculto", nullable = false, columnDefinition = "ENUM('0', '1')")
    @Expose
    private String oculto = "0";

    @Lob
    @Column(name = "publicado", nullable = false, columnDefinition = "ENUM('0', '1')")
    @Expose
    private String publicado = "0";

}
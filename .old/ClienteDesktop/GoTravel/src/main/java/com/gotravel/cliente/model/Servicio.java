package com.gotravel.cliente.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor // Constructor con aquellos atributos que son NonNull
@Entity
@Table(name = "servicio")
public class Servicio implements Serializable {

    @Id
    @Column(name = "idServicio", nullable = false)
    private Integer id;

    @NonNull
    @Column(name = "Nombre", nullable = false, length = 45)
    private String nombre;

    @NonNull
    @Column(name = "Descripcion", nullable = false, length = 100)
    private String descripcion;

    @NonNull
    @Column(name = "Precio", nullable = false)
    private Double precio;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "NombreTipoServicio", nullable = false)
    private Tiposervicio tipoServicio;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idLocalizacion", nullable = false)
    private Localizacion localizacion;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario profesional;

    @NonNull
    @Lob
    @Column(name = "oculto", nullable = false)
    private String oculto;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idServicio")
    private List<Imagen> imagenes;

}
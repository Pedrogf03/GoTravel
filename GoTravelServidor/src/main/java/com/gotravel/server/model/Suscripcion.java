package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "suscripcion")
public class Suscripcion {

    @Id
    @Column(name = "id_suscripcion", nullable = false, length = 50)
    @Expose
    private String id;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaInicio;

    @Column(name = "fecha_final", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fechaFinal;

    @Column(name = "estado", nullable = false, length = 50)
    @Expose
    private String estado;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @Expose
    private Usuario usuario;

    @Lob
    @Column(name = "renovar", nullable = false, columnDefinition = "ENUM('0','1')")
    @Expose
    private String renovar;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "pagosporsuscripcion",
            joinColumns = @JoinColumn(name = "id_suscripcion"),
            inverseJoinColumns = @JoinColumn(name = "id_pago"))
    @OrderBy("fecha DESC")
    @Expose
    private List<Pago> pagos;

}
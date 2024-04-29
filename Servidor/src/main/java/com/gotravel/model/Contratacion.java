package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "contratacion")
public class Contratacion {

    @Id
    @Column(name = "idContratacion", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idServicio", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idPago")
    private Pago pago;

    @OneToMany(mappedBy = "contratacion", cascade = CascadeType.ALL)
    private List<Resena> resenas;

}
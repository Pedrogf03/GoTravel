package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "mensaje")
public class Mensaje {

    @Id
    @Column(name = "idMensaje", nullable = false)
    private Integer id;

    @Column(name = "Contenido", nullable = false, length = 500)
    private String contenido;

    @Column(name = "Fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "Hora", nullable = false)
    private LocalTime hora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idEmisor", nullable = false)
    private Usuario emisor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idReceptor", nullable = false)
    private Usuario receptor;

}
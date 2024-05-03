package com.gotravel.cliente.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor // Constructor con aquellos atributos que son NonNull
@Entity
@Table(name = "mensaje")
public class Mensaje implements Serializable {

    @Id
    @Column(name = "idMensaje", nullable = false)
    private Integer id;

    @NonNull
    @Column(name = "Contenido", nullable = false, length = 500)
    private String contenido;

    @NonNull
    @Column(name = "Fecha", nullable = false)
    private LocalDate fecha;

    @NonNull
    @Column(name = "Hora", nullable = false)
    private LocalTime hora;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idEmisor", nullable = false)
    private Usuario emisor;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idReceptor", nullable = false)
    private Usuario receptor;

}
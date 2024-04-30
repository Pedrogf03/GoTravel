package com.gotravel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor // Constructor con aquellos atributos que son NonNull
@Entity
@Table(name = "suscripcion")
public class Suscripcion implements Serializable {

    @Id
    @Column(name = "idSuscripcion", nullable = false)
    private Integer id;

    @NonNull
    @Column(name = "FechaIni", nullable = false)
    private LocalDate fechaIni;

    @NonNull
    @Column(name = "FechaFin", nullable = false)
    private LocalDate fechaFin;

    @NonNull
    @Lob
    @Column(name = "Estado", nullable = false)
    private String estado;

    @NonNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idPago", nullable = false)
    private Pago pago;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name="idUsuario")
    private Usuario usuario;

}
package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "pago")
@NoArgsConstructor
public class Pago {

    public Pago(Usuario usuario, Double coste, String fecha) {
        this.usuario = usuario;
        this.coste = coste;
        this.fecha = fecha;
    }

    public Pago(Double coste, String fecha) {
        this.coste = coste;
        this.fecha = fecha;
    }

    @Id
    @Column(name = "id_pago", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "coste", nullable = false)
    @Expose
    private Double coste;

    @Column(name = "fecha", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fecha;

}
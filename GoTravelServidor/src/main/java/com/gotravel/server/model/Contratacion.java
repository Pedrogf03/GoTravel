package com.gotravel.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contratacion")
@NoArgsConstructor
@AllArgsConstructor
public class Contratacion {

    public Contratacion(String id, Pago pago, String fecha) {
        this.id = id;
        this.pago = pago;
        this.fecha = fecha;
    }

    @Id
    @Column(name = "id_contratacion", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha", nullable = false, columnDefinition = "DATE")
    private String fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etapa")
    private Etapa etapa;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

}
package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "metodopago")
public abstract class Metodopago {

    @Id
    @Column(name = "idMetodoPago", nullable = false)
    protected Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUsuario", nullable = false)
    protected Usuario usuario;

}
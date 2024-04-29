package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "paypal")
public class Paypal extends Metodopago {

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idMetodoPago", nullable = false)
    private Metodopago metodoPago;

    @Column(name = "Email", nullable = false, length = 45)
    private String email;

}
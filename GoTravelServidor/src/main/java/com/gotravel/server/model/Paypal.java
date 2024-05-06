package com.gotravel.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "paypal")
public class Paypal extends Metodopago {

    @Column(name = "email", nullable = false, length = 45)
    private String email;

}
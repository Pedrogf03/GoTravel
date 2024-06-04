package com.gotravel.cliente.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paypal")
public class Paypal extends Metodopago {

    @NonNull
    @Column(name = "Email", nullable = false, length = 45)
    private String email;

}
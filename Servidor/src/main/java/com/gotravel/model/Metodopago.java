package com.gotravel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "metodopago")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Metodopago implements Serializable {

    @Id
    @Column(name = "idMetodoPago", nullable = false)
    protected Integer id;

}
package com.gotravel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @Column(name = "NombreRol", nullable = false, length = 50)
    private String nombreRol;

}
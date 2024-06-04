package com.gotravel.cliente.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "tiposervicio")
public class Tiposervicio implements Serializable {

    @Id
    @Column(name = "NombreTipoServicio", nullable = false, length = 100)
    private String nombreTipoServicio;

}
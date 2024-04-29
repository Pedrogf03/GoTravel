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
@Table(name = "tiposervicio")
public class Tiposervicio {

    @Id
    @Column(name = "NombreTipoServicio", nullable = false, length = 100)
    private String nombreTipoServicio;

}
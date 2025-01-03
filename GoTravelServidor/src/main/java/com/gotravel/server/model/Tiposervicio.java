package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
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
    @Column(name = "nombre", nullable = false, length = 100)
    @Expose
    private String nombre;

}
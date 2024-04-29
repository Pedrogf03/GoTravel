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
@Table(name = "localizacion")
public class Localizacion {

    @Id
    @Column(name = "idLocalizacion", nullable = false)
    private Integer id;

}
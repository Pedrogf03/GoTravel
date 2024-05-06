package com.gotravel.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "localizacion")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Localizacion {
    @Id
    @Column(name = "id_localizacion", nullable = false)
    private Integer id;

}
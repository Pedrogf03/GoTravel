package com.gotravel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "localizacion")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Localizacion {

    @Id
    @Column(name = "id_localizacion", nullable = false)
    private Integer id;

}
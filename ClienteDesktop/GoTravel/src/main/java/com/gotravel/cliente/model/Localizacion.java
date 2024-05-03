package com.gotravel.cliente.model;

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
@Table(name = "localizacion")
@Inheritance(strategy= InheritanceType.JOINED)
public abstract class Localizacion implements Serializable {

    @Id
    @Column(name = "idLocalizacion", nullable = false)
    private Integer id;

}
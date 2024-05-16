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
@Table(name = "direccion")
public class Direccion extends Localizacion {

    @NonNull
    @Column(name = "Calle", nullable = false, length = 100)
    private String calle;

    @NonNull
    @Column(name = "Numero", nullable = false, length = 5)
    private String numero;

    @NonNull
    @Column(name = "Ciudad", nullable = false, length = 50)
    private String ciudad;

    @NonNull
    @Column(name = "Estado", nullable = false, length = 50)
    private String estado;

    @NonNull
    @Column(name = "CP", nullable = false, length = 5)
    private String cp;

}
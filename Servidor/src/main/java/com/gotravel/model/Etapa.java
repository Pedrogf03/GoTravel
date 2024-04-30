package com.gotravel.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor // Constructor con aquellos atributos que son NonNull
@Entity
@Table(name = "etapa")
public class Etapa implements Serializable {

    @Id
    @Column(name = "idEtapa", nullable = false)
    private Integer id;

    @NonNull
    @Column(name = "Nombre", nullable = false, length = 45)
    private String nombre;

    @NonNull
    @Column(name = "FechaIni", nullable = false)
    private LocalDate fechaIni;

    @NonNull
    @Column(name = "FechaFin", nullable = false)
    private LocalDate fechaFin;

    @NonNull
    @Column(name = "CosteTotal", nullable = false)
    private Double costeTotal;

    @NonNull
    @Lob
    @Column(name = "Tipo", nullable = false)
    private String tipo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idEtapa")
    private List<Contratacion> contrataciones;

}
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
@Table(name = "contratacion")
public class Contratacion implements Serializable {

    @Id
    @Column(name = "idContratacion", nullable = false)
    private Integer id;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idServicio", nullable = false)
    private Servicio servicio;

    @NonNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idPago", referencedColumnName = "idPago")
    private Pago pago;

    @OneToMany(mappedBy = "contratacion", cascade = CascadeType.ALL)
    private List<Resena> resenas;

}
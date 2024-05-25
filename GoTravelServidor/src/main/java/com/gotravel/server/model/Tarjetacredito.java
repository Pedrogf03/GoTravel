package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tarjetacredito")
public class Tarjetacredito extends Metodopago{

    @Column(name = "numero", nullable = false, length = 16, columnDefinition = "CHAR")
    @Expose
    private String numero;

    @Column(name = "tipo", nullable = false, length = 45)
    @Expose
    private String tipo;

    @Column(name = "titular", nullable = false, length = 100)
    @Expose
    private String titular;

    @Column(name = "mes_vencimiento", nullable = false)
    @Expose
    private Integer mesVencimiento;

    @Column(name = "anyo_vencimiento", nullable = false)
    @Expose
    private Integer anyoVencimiento;

    @Column(name = "cvv", nullable = false, length = 3, columnDefinition = "CHAR")
    @Expose
    private String cvv;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "dir_facturacion", nullable = false)
    @Expose
    private DirFacturacion dirFacturacion;

}
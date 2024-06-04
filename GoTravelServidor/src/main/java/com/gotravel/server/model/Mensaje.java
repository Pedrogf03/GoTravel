package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "mensaje")
@ToString
public class Mensaje {

    @Id
    @Column(name = "id_mensaje", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_emisor", nullable = false)
    @Expose
    private Usuario emisor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_receptor", nullable = false)
    @Expose
    private Usuario receptor;

    @Column(name = "texto", nullable = false, length = 200)
    @Expose
    private String texto;

    @Column(name = "fecha", nullable = false, columnDefinition = "DATE")
    @Expose
    private String fecha;

    @Column(name = "hora", nullable = false, columnDefinition = "TIME")
    @Expose
    private String hora;
}
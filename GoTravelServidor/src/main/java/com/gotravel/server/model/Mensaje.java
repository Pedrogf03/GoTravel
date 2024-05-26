package com.gotravel.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mensaje")
public class Mensaje {

    @Id
    @Column(name = "id_mensaje", nullable = false)
    private Integer id;

    @Column(name = "contenido", nullable = false, length = 200)
    private String contenido;

    @Column(name = "fecha", nullable = false, columnDefinition = "DATE")
    private String fecha;

    @Column(name = "hora", nullable = false, columnDefinition = "TIME")
    private String hora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario emisor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chat", nullable = false)
    private Chat chat;

}
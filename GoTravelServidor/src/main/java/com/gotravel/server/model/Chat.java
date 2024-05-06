package com.gotravel.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @Column(name = "id_chat", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario1", nullable = false)
    private Usuario usuario1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario2", nullable = false)
    private Usuario usuario2;

    @OneToMany(mappedBy = "chat")
    private List<Mensaje> mensajes;

}
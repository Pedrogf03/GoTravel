package com.gotravel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name = "idUsuario", nullable = false)
    private Integer id;

    @Column(name = "Nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "Apellidos", nullable = false, length = 200)
    private String apellidos;

    @Column(name = "Email", nullable = false, length = 150)
    private String email;

    @Column(name = "Tfno", length = 9)
    private String tfno;

    @Column(name = "Foto")
    private byte[] foto;

    @Lob
    @Column(name = "oculto", nullable = false)
    private String oculto;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idUsuario")
    private List<Viaje> viajes;

    @ManyToMany
    @JoinTable(
            name = "usuariorol",
            joinColumns = @JoinColumn(name = "idUsuario"),
            inverseJoinColumns = @JoinColumn(name = "NombreRol")
    )
    private List<Rol> roles;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idUsuario")
    private Suscripcion suscripcion;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idUsuario")
    private List<Pago> pagos;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idEmisor")
    private List<Mensaje> mensajesEnviados;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idReceptor")
    private List<Mensaje> mensajesRecibidos;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idUsuario")
    private List<Servicio> servicios;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idUsuario")
    private List<Contratacion> contrataciones;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idUsuario")
    private List<Metodopago> metodosPago;

}
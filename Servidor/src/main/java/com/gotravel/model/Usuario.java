package com.gotravel.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor // Constructor con aquellos atributos que son NonNull
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @Column(name = "idUsuario", nullable = false)
    @JsonView(Views.UsuarioView.class)
    private Integer id;

    @NonNull
    @Column(name = "Nombre", nullable = false, length = 45)
    @JsonView(Views.UsuarioView.class)
    private String nombre;

    @NonNull
    @Column(name = "Apellidos", nullable = false, length = 200)
    @JsonView(Views.UsuarioView.class)
    private String apellidos;

    @NonNull
    @Column(name = "Email", nullable = false, length = 150)
    @JsonView(Views.UsuarioView.class)
    private String email;

    @NonNull
    @Column(name = "Contrasena", nullable = false, length = 300)
    @JsonView(Views.UsuarioView.class)
    private String contrasena;

    @Column(name = "Tfno", length = 9)
    @JsonView(Views.UsuarioView.class)
    private String tfno;

    @Column(name = "Foto")
    @JsonView(Views.UsuarioView.class)
    private byte[] foto;

    @NonNull
    @Lob
    @Column(name = "oculto", nullable = false)
    @JsonView(Views.UsuarioView.class)
    private String oculto;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idUsuario")
    private List<Viaje> viajes;

    @NonNull
    @ManyToMany
    @JoinTable(
            name = "usuariorol",
            joinColumns = @JoinColumn(name = "idUsuario"),
            inverseJoinColumns = @JoinColumn(name = "NombreRol")
    )
    @JsonView(Views.UsuarioView.class)
    private List<Rol> roles;

    @OneToOne(mappedBy = "usuario")
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
package com.gotravel.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    public Usuario(String nombre, String email, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
    }

    @Id
    @Column(name = "id_usuario", nullable = false)
    @JsonView(Views.Usuario.class)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    @JsonView(Views.Usuario.class)
    private String nombre;

    @Column(name = "apellidos", length = 200)
    @JsonView(Views.Usuario.class)
    private String apellidos;

    @Column(name = "email", nullable = false, length = 150)
    @JsonView(Views.Usuario.class)
    private String email;

    @Column(name = "contrasena", nullable = false, length = 300)
    private String contrasena;

    @Column(name = "tfno", length = 9, columnDefinition = "CHAR")
    @JsonView(Views.Usuario.class)
    private String tfno;

    @Column(name = "foto")
    @JsonView(Views.Usuario.class)
    private byte[] foto;

    @Lob
    @Column(name = "oculto", nullable = false, columnDefinition = "ENUM('0', '1')")
    @JsonView(Views.Usuario.class)
    private String oculto;

    @OneToMany(mappedBy = "usuario")
    private List<Viaje> viajes;

    @ManyToMany
    @JoinTable(
            name = "usuariorol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "rol")
    )
    private List<Rol> roles;

    @OneToMany(mappedBy = "usuario")
    private List<Servicio> servicios;

    @OneToMany(mappedBy = "usuario")
    private List<Pago> pagos;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private List<Contratacion> contrataciones;

    @OneToOne(mappedBy = "usuario")
    private Suscripcion suscripcion;

    @OneToMany(mappedBy = "emisor")
    private List<Mensaje> mensajesEnviados;

    @OneToMany(mappedBy = "receptor")
    private List<Mensaje> mensajesRecibidos;

    @OneToMany(mappedBy = "usuario")
    private List<Metodopago> metodosPago;

}
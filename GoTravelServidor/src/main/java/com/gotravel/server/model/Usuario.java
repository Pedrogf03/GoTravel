package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "usuario")
public class Usuario implements Serializable {

    public Usuario(String nombre, String email, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.oculto = "0";
    }

    public Usuario(int id, String nombre, String email, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.oculto = "0";
    }

    public Usuario(Integer id, String nombre, String apellidos, String email, String contrasena, String tfno, byte[] foto, List<Rol> roles) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasena = contrasena;
        this.tfno = tfno;
        this.foto = foto;
        this.roles = roles;
    }

    public Usuario(Integer id, String nombre, String apellidos, String contrasena, String email, String tfno) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tfno = tfno;
        this.contrasena = contrasena;
    }

    @Id
    @Column(name = "id_usuario", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 45)
    @Expose
    private String nombre;

    @Column(name = "apellidos", length = 200)
    @Expose
    private String apellidos;

    @Column(name = "email", nullable = false, length = 150)
    @Expose
    private String email;

    @Column(name = "contrasena", nullable = false, length = 300)
    @Expose
    private String contrasena;

    @Column(name = "tfno", length = 9, columnDefinition = "CHAR")
    @Expose
    private String tfno;

    @Column(name = "foto")
    private byte[] foto;

    @Column(name = "oculto", nullable = false, columnDefinition = "ENUM('0', '1')")
    @Expose
    private String oculto = "0";

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private List<Viaje> viajes;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "usuariorol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "rol")
    )
    @Expose
    private List<Rol> roles;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private List<Servicio> servicios;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private List<Pago> pagos;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private List<Contratacion> contrataciones;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Suscripcion suscripcion;

    @OneToMany(mappedBy = "emisor")
    List<Mensaje> mensajesEnviados;

    @OneToMany(mappedBy = "receptor")
    List<Mensaje> mensajesRecibidos;

}
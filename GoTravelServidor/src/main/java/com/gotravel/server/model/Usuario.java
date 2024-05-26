package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    public Usuario(String nombre, String email, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
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

    @Lob
    @Column(name = "oculto", nullable = false, columnDefinition = "ENUM('0', '1')")
    private String oculto = "0";

    @OneToMany(mappedBy = "usuario")
    private List<Viaje> viajes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuariorol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "rol")
    )
    @Expose
    private List<Rol> roles;

    @OneToMany(mappedBy = "usuario")
    private List<Servicio> servicios;

    @OneToMany(mappedBy = "usuario")
    private List<Pago> pagos;

    @OneToMany(mappedBy = "usuario")
    private List<Contratacion> contrataciones;

    @OneToOne(mappedBy = "usuario")
    private Suscripcion suscripcion;

    @OneToMany(mappedBy = "usuario1")
    private List<Chat> chatsComoUsuario1;

    @OneToMany(mappedBy = "usuario2")
    private List<Chat> chatsComoUsuario2;

    public List<Chat> getChats() {
        List<Chat> todosLosChats = new ArrayList<>();
        todosLosChats.addAll(chatsComoUsuario1);
        todosLosChats.addAll(chatsComoUsuario2);
        return todosLosChats;
    }

    @OneToMany(mappedBy = "emisor")
    private List<Mensaje> mensajes;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private List<DirFacturacion> direcciones;

}
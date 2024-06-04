package com.gotravel.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.processing.Generated;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private int id;
    private String nombre;
    private String apellidos;
    private String email;
    private String contrasena;
    private List<Rol> roles;
    private String tfno;
    private byte[] foto;
    private boolean profesional;
    private boolean administrador;

    public Usuario(int id, String nombre, String apellidos, String email, String contrasena, String tfno) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tfno = tfno;
        this.contrasena = contrasena;
    }

    public String getApellidos() {
        return apellidos != null ? apellidos : "";
    }

    public String getTfno() {
        return tfno != null ? tfno : "";
    }
}

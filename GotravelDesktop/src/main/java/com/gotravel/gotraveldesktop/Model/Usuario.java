package com.gotravel.gotraveldesktop.Model;

import java.util.List;

public class Usuario {
    public int id;
    public String nombre;
    public String apellidos;
    public String email;
    public String contrasena;
    public List<Rol> roles;
    public String tfno;
    public byte[] foto;
    public boolean esProfesional;
    public boolean esAdministrador;

    public String getApellidos() {
        return apellidos != null ? apellidos : "";
    }

    public String getTfno() {
        return tfno != null ? tfno : "";
    }
}

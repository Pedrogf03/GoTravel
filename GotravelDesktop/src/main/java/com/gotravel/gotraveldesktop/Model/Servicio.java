package com.gotravel.gotraveldesktop.Model;

import java.util.List;

public class Servicio {

    public int id;
    public String nombre;
    public String descripcion;
    public Double precio;
    public String fechaInicio;
    public String fechaFinal;
    public String hora;
    public TipoServicio tipoServicio;
    public Direccion direccion;
    public Usuario usuario;
    public List<Imagen> imagenes;
    public List<Resena> resenas;
    public String publicado = "0";
    public boolean contratado = false;

}

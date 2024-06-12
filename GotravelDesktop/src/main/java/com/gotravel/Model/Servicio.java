package com.gotravel.Model;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.gotravel.Utils.Fechas.formatoFinal;
import static com.gotravel.Utils.Fechas.formatoFromDb;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    private int id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String fechaInicio;
    private String fechaFinal;
    private String hora;
    private TipoServicio tipoServicio;
    private Direccion direccion;
    private Usuario usuario;
    private List<Imagen> imagenes = new ArrayList<>();
    private List<Resena> resenas = new ArrayList<>();
    private String publicado = "0";
    private String oculto = "0";
    private boolean contratado = false;

    public Servicio(String nombre, String descripcion, Double precio, String fechaInicio, String fechaFinal, String hora, TipoServicio tipoServicio, Direccion direccion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.hora = hora;
        this.tipoServicio = tipoServicio;
        this.direccion = direccion;
    }

    public String getInicio() {
        return LocalDate.parse(fechaInicio, formatoFromDb).format(formatoFinal);
    }

    public String getFinal() {
        if (fechaFinal == null) {
            return "";
        } else {
            return LocalDate.parse(fechaFinal, formatoFromDb).format(formatoFinal);
        }
    }

}

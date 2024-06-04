package com.gotravel.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import static com.gotravel.Utils.Fechas.formatoFinal;
import static com.gotravel.Utils.Fechas.formatoFromDb;


@Getter
@Setter
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
    private List<Imagen> imagenes;
    private List<Resena> resenas;
    private String publicado = "0";
    private boolean contratado = false;

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

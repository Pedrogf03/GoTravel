package com.gotravel.Model;

import com.gotravel.Utils.Fechas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Viaje {

    private int id;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    private Double costeTotal;
    private List<Etapa> etapas;

    public Viaje(String nombre, String descripcion, String fechaInicio, String fechaFin, Double costeTotal) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costeTotal = costeTotal;
    }

    public Viaje(int id, String nombre, String descripcion, String fechaInicio, String fechaFin, Double costeTotal) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costeTotal = costeTotal;
    }

    public String inicio() {
        return LocalDate.parse(this.fechaInicio, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

    public String fin() {
        return LocalDate.parse(this.fechaFin, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

}

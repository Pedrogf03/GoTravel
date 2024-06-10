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
public class Etapa {

    private int id;
    private String nombre;
    private String fechaInicio;
    private String fechaFinal;
    private Double costeTotal;
    private String tipo;
    private List<Servicio> contrataciones;

    public Etapa(String nombre, String fechaInicio, String fechaFinal, String tipo, Double costeTotal) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.tipo = tipo;
        this.costeTotal = costeTotal;
    }

    public String inicio() {
        return LocalDate.parse(this.fechaInicio, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

    public String fin() {
        return LocalDate.parse(this.fechaFinal, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

}

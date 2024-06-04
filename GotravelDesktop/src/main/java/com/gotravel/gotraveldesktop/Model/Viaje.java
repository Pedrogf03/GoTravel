package com.gotravel.gotraveldesktop.Model;

import com.gotravel.gotraveldesktop.Utils.Fechas;

import java.time.LocalDate;
import java.util.List;

public class Viaje {

    public int id;
    public String nombre;
    public String descripcion;
    public String fechaInicio;
    public String fechaFin;
    public Double costeTotal;
    public List<Etapa> etapas;

    public String inicio() {
        return LocalDate.parse(this.fechaInicio, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

    public String fin() {
        return LocalDate.parse(this.fechaFin, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

}

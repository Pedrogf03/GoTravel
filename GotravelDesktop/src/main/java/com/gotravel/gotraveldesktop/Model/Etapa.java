package com.gotravel.gotraveldesktop.Model;

import com.gotravel.gotraveldesktop.Utils.Fechas;

import java.time.LocalDate;
import java.util.List;

public class Etapa {

    public int id;
    public String nombre;
    public String fechaInicio;
    public String fechaFinal;
    public Double costeTotal;
    public String tipo;
    public List<Servicio> contrataciones;

    public String inicio() {
        return LocalDate.parse(this.fechaInicio, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

    public String fin() {
        return LocalDate.parse(this.fechaFinal, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

}

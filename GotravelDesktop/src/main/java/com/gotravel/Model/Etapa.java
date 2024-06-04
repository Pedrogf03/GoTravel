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

    public String inicio() {
        return LocalDate.parse(this.fechaInicio, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

    public String fin() {
        return LocalDate.parse(this.fechaFinal, Fechas.formatoFromDb).format(Fechas.formatoFinal);
    }

}

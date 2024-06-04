package com.gotravel.Model;

import com.gotravel.Utils.Fechas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Suscripcion {

    private String id;
    private String fechaInicio;
    private String fechaFinal;
    private String estado;
    private String renovar;
    private List<Pago> pagos;

    public String getFinal() {
        LocalDate fecha = LocalDate.parse(fechaFinal, Fechas.formatoFromDb);
        return fecha.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy"));
    }

}

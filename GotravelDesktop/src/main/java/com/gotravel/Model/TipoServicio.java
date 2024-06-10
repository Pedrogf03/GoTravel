package com.gotravel.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoServicio {

    private String nombre;

    @Override
    public String toString() {
        return nombre;
    }
}

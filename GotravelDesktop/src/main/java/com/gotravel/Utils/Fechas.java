package com.gotravel.Utils;

import java.time.format.DateTimeFormatter;

public class Fechas {

    public static final DateTimeFormatter formatoFromDb = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter formatoFinal = DateTimeFormatter.ofPattern("dd/MM/yyyy");

}

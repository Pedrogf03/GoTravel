package com.gotravel.gotraveldesktop.Utils;

import java.util.regex.Pattern;

public class Regex {

    public static Pattern regexEmail = Pattern.compile("^(?=.{1,150}$)[A-Za-z0-9+_.-]+@(.+)$");
    public static Pattern regexContrasena = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).{8,}$");
    public static Pattern regexCamposAlfaNum = Pattern.compile("^(?=.{1,500}$)[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ,]*$");
    public static Pattern regexTfno = Pattern.compile("^\\d{9}$");
    public static Pattern regexCp = Pattern.compile("^\\d{5}$");

}

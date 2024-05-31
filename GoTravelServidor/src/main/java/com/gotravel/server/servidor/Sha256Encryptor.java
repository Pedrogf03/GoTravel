package com.gotravel.server.servidor;

import java.security.MessageDigest;

public class Sha256Encryptor {

    public static String cifrarTexto(String texto) {
        try {
            // Crear instancia de MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Aplicar SHA-256 al texto
            byte[] hash = digest.digest(texto.getBytes("UTF-8"));

            // Convertir el hash a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Devolver el texto cifrado en hexadecimal
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error al cifrar el texto", ex);
        }
    }

}


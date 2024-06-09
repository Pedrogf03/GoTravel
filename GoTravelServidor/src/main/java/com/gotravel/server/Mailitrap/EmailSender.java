package com.gotravel.server.Mailitrap;


import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailSender {

    public static Boolean sendRecoveryEmail(String email, String newPassword) {

        // Configuración del servidor SMTP de Mailtrap
        final String username = "3187270cfea8bb";
        final String password = "d48d12076bbd53";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");

        // Crear una sesión con autenticación
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Crear un mensaje de correo electrónico
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-reply@gotravel.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Recuperación de contraseña de GoTravel!");

            // Formatear el cuerpo del correo electrónico
            String cuerpoCorreo = "Hola,\n\n" +
                    "Hemos recibido una solicitud para restablecer la contraseña asociada a esta dirección de correo electrónico.\n\n" +
                    "Tu nueva contraseña temporal es: " + newPassword + "\n\n" +
                    "Por favor, inicia sesión con esta contraseña temporal y cámbiala por una nueva lo antes posible para garantizar la seguridad de tu cuenta.\n\n" +
                    "Atentamente,\n" +
                    "El equipo de GoTravel!";

            message.setText(cuerpoCorreo);

            // Enviar el mensaje
            Transport.send(message);

            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }


}

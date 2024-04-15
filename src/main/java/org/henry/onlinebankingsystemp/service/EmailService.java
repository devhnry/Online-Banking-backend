package org.henry.onlinebankingsystemp.service;

import org.springframework.stereotype.Service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

@Service
public class EmailService {
    public void sendEmail(String recipientEmail, String subject, String body) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.google.com"); // Replace with your SMTP server host
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("taiwoh782@gmail.com", "june60025$");
            }
        });

        // Create a Message object
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("taiwoh782@example.com")); // Replace with your email address
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail)); // Set recipient email address
        message.setSubject(subject);
        message.setText(body);

        // Send the message
        Transport.send(message);
    }
}

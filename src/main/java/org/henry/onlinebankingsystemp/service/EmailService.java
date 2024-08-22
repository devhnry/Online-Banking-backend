package org.henry.onlinebankingsystemp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;

import javax.mail.*;

public interface EmailService {
    void sendEmail(String toEmail, String subject, Context context);
}

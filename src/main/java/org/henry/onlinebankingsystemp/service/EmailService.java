package org.henry.onlinebankingsystemp.service;

import org.thymeleaf.context.Context;

import javax.mail.*;

public interface EmailService {
    void sendEmail(String toEmail, String subject, Context context, String template);
}

package org.henry.onlinebankingsystemp.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service @Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;


    @Async("customEmailExecutor")/* Send Email Asynchronously */
    @Override
    public void sendEmail(String toEmail, String subject, Context context, String template) {
        int maxRetries = 3;
        int retryCount = 0;
        long retryDelay = 2000; // 2 second

        String htmlContent = templateEngine.process(template, context);
        //noinspection ConstantValue
        while (retryCount < maxRetries) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(SENDER_EMAIL);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                mailSender.send(message);

                /* If successful, break out of the retry loop */
                System.out.println("Email sent successfully");
                return;
            } catch (Exception e) {
                retryCount++;
                System.out.println("Attempt " + retryCount + " failed. Error: " + e.getMessage());

                if (retryCount >= maxRetries) {
                    System.out.println("All retry attempts failed. Giving up.");
                    throw new RuntimeException("Failed to send email after " + maxRetries + " attempts", e);
                }

                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ex);
                }
            }
        }
    }
}

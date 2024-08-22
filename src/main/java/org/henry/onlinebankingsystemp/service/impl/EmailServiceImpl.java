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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service @Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    @Async("customEmailExecutor")/* Send Email Asynchronously */
    @Override
    public void sendEmail(String toEmail, String subject, Context context) {
        int maxRetries = 3;
        int retryCount = 0;
        long retryDelay = 2000; // 2 second

        final String htmlContent = springTemplateEngine.process("onboardTemplate", context);

        do {
            try{
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(SENDER_EMAIL);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                mailSender.send(message);

                /* If successful, break out of the retry loop */
                log.info("Email sent successfully");
                return;
            } catch (Exception e) {
                retryCount++;
                log.debug("Attempt {} failed. Error: {}", retryCount, e.getMessage());

                if (retryCount >= maxRetries) {
                    log.debug("All retry attempts failed. Giving up.");
                    throw new RuntimeException("Failed to send email after " + maxRetries + " attempts", e);
                }

                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ex);
                }
            }
        } while (retryCount < maxRetries);
    }
}

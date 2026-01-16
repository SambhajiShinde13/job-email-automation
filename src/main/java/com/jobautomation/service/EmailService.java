package com.jobautomation.service;

import com.jobautomation.model.HRContact;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.resume.path}")
    private String resumePath;

    @Value("${app.sender.name}")
    private String senderName;

    @Value("${app.sender.phone}")
    private String senderPhone;

    @Value("${app.sender.linkedin}")
    private String senderLinkedIn;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public boolean sendResumeEmail(HRContact contact) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(contact.getEmail());
            helper.setSubject(buildSubject(contact));
            helper.setText(buildEmailBody(contact), true);

            FileSystemResource file = new FileSystemResource(new File(resumePath));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);

            log.info("✅ Email sent to {} ({})", contact.getCompany(), contact.getEmail());
            return true;

        } catch (MessagingException e) {
            log.error("❌ Mail error for {} : {}", contact.getEmail(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ Unexpected error for {} : {}", contact.getEmail(), e.getMessage());
            return false;
        }
    }

    private String buildSubject(HRContact contact) {
        return "Application for Software Engineer at " + contact.getCompany();
    }

    private String buildEmailBody(HRContact contact) {

        return """
                 <html>
                 <body style="font-family: Arial, sans-serif; line-height:1.6; font-size:14px; color:#333;">
                    \s
                     <p>Dear %s,</p>
                 
                     <p>I hope you are doing well.</p>
                 
                     <p>I am writing to express my interest in the <b>Software Engineer</b> position at <b>%s</b>.</p>
                 
                     <p>
                         I have hands-on experience in Java, Spring Boot, REST APIs, and AWS, and recently built an
                         event-driven microservices system using Apache Kafka.
                     </p>
                 
                     <p>Please find my resume attached for your review.</p>
                 
                     <p>
                         Thank you for your time and consideration. I would welcome the opportunity to discuss how I can contribute to your team.
                     </p>
                 
                     <p>
                         Warm regards,<br/>
                         <b>Sambhaji Shinde</b><br/>
                         Phone: +91-9716321313<br/>
                         Email: sambhajishinde4454@gmail.com<br/>
                         LinkedIn:\s
                         <a href="https://linkedin.com/in/sambhajishinde13">
                             https://linkedin.com/in/sambhajishinde13
                         </a>
                     </p>
                 
                 </body>
                 </html>
                 
                """.formatted(
                contact.getHrName(),
                contact.getCompany(),
                senderName,
                senderPhone,
                senderEmail,
                senderLinkedIn,
                senderLinkedIn
        );
    }
}

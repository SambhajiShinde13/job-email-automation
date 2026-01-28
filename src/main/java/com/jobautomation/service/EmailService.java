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
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;



    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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
        return "Application – Software Engineer | 2+ Years Java & Spring Boot | Immediate Joiner" + contact.getCompany();
    }

    private String buildEmailBody(HRContact contact) {

        return """
                  
                  <html>
                  <body style="font-family: Arial, sans-serif; line-height:1.6; font-size:14px; color:#333;">
                  
                  <p>Dear %s,</p>
                  
                  <p>I hope you are doing well.</p>
                  
                  <p>
                  I am writing to express my interest in the <b>Software Engineer</b> position at <b>%s</b>.
                  </p>
                  
                  <p>
                  I have 2+ years of experience in backend development using <b>Java, Spring Boot, REST APIs, MySQL, and AWS</b>.\s
                  I have worked on developing scalable RESTful services and recently built a microservices-based system using Apache Kafka.
                  </p>
                  
                  <p>
                  I am currently available to join immediately and would welcome the opportunity to contribute to your team.
                  </p>
                  
                  <p>Please find my resume attached for your review.</p>
                  
                  <p>
                  Thank you for your time and consideration. I look forward to hearing from you.
                  </p>
                  
                  <p>
                  Best regards,<br/>
                  <b>Sambhaji Shinde</b><br/>
                  Phone: +91-9716321313<br/>
                  Email: sambhashinde4454@gmail.com<br/>
                  LinkedIn:\s
                  <a href="https://linkedin.com/in/sambhashinde">
                  https://linkedin.com/in/sambhashinde
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

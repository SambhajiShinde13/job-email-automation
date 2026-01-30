package com.jobautomation.service;

import com.jobautomation.model.HRContact;
import com.jobautomation.repository.HRContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler service that automatically sends emails every weekday at 9:30 AM
 */
@Service
@Slf4j
public class EmailSchedulerService {

    private final HRContactRepository hrContactRepository;

    public EmailSchedulerService(HRContactRepository hrContactRepository,EmailService emailService) {
        this.hrContactRepository = hrContactRepository;
        this.emailService= emailService;
    }
    private final EmailService emailService;

    private static final String TEST_EMAIL = "sambhashinde01@gmail.com";

    @Value("${app.email.daily.limit:10}")
    private int dailyEmailLimit;

    /**
     * Scheduled task that runs every weekday at 9:30 AM
     * Cron: 0 30 9 * * MON-FRI (second minute hour day month weekday)
     */
    // @Scheduled(fixedDelay = 60000)


    @Scheduled(cron = "0 30 9 * * MON-FRI", zone = "Asia/Kolkata")
   // @Transactional
    public void sendDailyEmails() {

        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        log.info("=======================================================");
        log.info("📧 EMAIL AUTOMATION STARTED - {} {}", dayOfWeek, now);
        log.info("=======================================================");

//        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
//            log.info("⏸️ Weekend detected. Skipping email sending.");
//            return;
//        }

        List<HRContact> contacts = hrContactRepository.findActiveContacts(
                PageRequest.of(0, dailyEmailLimit)
        );

        if (contacts.isEmpty()) {
            log.warn("⚠️ No active HR contacts found.");
            return;
        }

        log.info("📋 Found {} active contacts", contacts.size());

        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < contacts.size(); i++) {

            HRContact contact = contacts.get(i);

            // 🔥 TEST MODE — ONLY YOUR EMAIL
//            if (!contact.getEmail().equalsIgnoreCase("sambhashinde01@gmail.com")) {
//                continue;
//            }

            log.info("📨 [{}/{}] Sending to {} ({})",
                    i + 1, contacts.size(), contact.getHrName(), contact.getEmail());

            try {

                emailService.sendResumeEmail(contact);

                contact.setActive(false);
                contact.setStatus("PROCESSED");
                contact.setEmailSentAt(LocalDateTime.now());
                hrContactRepository.save(contact);



                successCount++;

                Thread.sleep(7000);

            } catch (Exception e) {
                failureCount++;

                contact.setActive(false);
                contact.setStatus("FAILED");
                contact.setNotes(e.getMessage());
                hrContactRepository.save(contact);

                log.error("❌ Failed for {} : {}", contact.getEmail(), e.getMessage());
            }
        }

        // ✅ SUMMARY MUST BE OUTSIDE LOOP
        log.info("=======================================================");
        log.info("✅ EMAIL AUTOMATION COMPLETED");
        log.info("📊 Summary:");
        log.info("   - Emails sent successfully: {}", successCount);
        log.info("   - Emails failed: {}", failureCount);
        log.info("   - Remaining active contacts: {}", hrContactRepository.countByActiveTrue());
        log.info("   - Total processed contacts: {}", hrContactRepository.countByActiveFalse());
        log.info("=======================================================");
    }


    /**
     * Manual trigger for testing (can be called via REST API)
     */
    @Transactional
    public String sendEmailsManually() {
        log.info("🔧 Manual email trigger activated");
       // sendDailyEmails();
        sendSingleTestMail();
        return "Email sending process completed. Check logs for details.";
    }


   // @Scheduled(fixedDelay = 600000)
    @Transactional
    public void sendSingleTestMail() {

        log.info("🧪 TEST MODE STARTED for {}", TEST_EMAIL);

        HRContact contact = hrContactRepository
                .findByEmailIgnoreCase(TEST_EMAIL)
                .orElseThrow(() -> new RuntimeException("Test email not found in DB: " + TEST_EMAIL));

        try {
            emailService.sendResumeEmail(contact);

            contact.setActive(false);
            contact.setEmailSentAt(LocalDateTime.now());
            hrContactRepository.save(contact);

            log.info("✅ TEST EMAIL SENT SUCCESSFULLY to {}", contact.getEmail());

        } catch (Exception e) {
            log.error("❌ TEST EMAIL FAILED", e);
        }
    }

}

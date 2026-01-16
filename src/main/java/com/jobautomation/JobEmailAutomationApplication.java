package com.jobautomation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application for Daily Job Email Automation System
 * 
 * This application automatically sends personalized resume emails to HR contacts
 * every weekday at 9:30 AM, helping automate the job application process.
 */
@SpringBootApplication
@EnableScheduling
public class JobEmailAutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobEmailAutomationApplication.class, args);
        System.out.println("========================================");
        System.out.println("Job Email Automation System Started!");
        System.out.println("Scheduler will run daily at 9:30 AM");
        System.out.println("========================================");
    }
}

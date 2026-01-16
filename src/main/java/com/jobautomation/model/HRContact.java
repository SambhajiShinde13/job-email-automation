package com.jobautomation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hr_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HRContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hr_name", nullable = false)
    private String hrName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(name = "email_sent_at", columnDefinition = "DATETIME")
    private LocalDateTime emailSentAt;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public HRContact(String hrName, String email, String company, String position) {
        this.hrName = hrName;
        this.email = email;
        this.company = company;
        this.position = position;
        this.active = true;
    }
}

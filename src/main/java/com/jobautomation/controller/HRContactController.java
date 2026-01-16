package com.jobautomation.controller;

import com.jobautomation.model.HRContact;
import com.jobautomation.service.EmailSchedulerService;
import com.jobautomation.service.HRContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for managing HR contacts and email automation
 */
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HRContactController {

    private final HRContactService hrContactService;
    private final EmailSchedulerService emailSchedulerService;

    /**
     * Add a new HR contact
     * POST /api/contacts
     */
    @PostMapping
    public ResponseEntity<?> addContact(@RequestBody HRContact contact) {
        try {
            HRContact saved = hrContactService.addContact(contact);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Add multiple HR contacts
     * POST /api/contacts/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> addContacts(@RequestBody List<HRContact> contacts) {
        try {
            List<HRContact> saved = hrContactService.addContacts(contacts);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Bulk contacts added successfully",
                "totalContacts", saved.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all contacts
     * GET /api/contacts
     */
    @GetMapping
    public ResponseEntity<List<HRContact>> getAllContacts() {
        return ResponseEntity.ok(hrContactService.getAllContacts());
    }

    /**
     * Get active contacts only
     * GET /api/contacts/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<HRContact>> getActiveContacts() {
        return ResponseEntity.ok(hrContactService.getActiveContacts());
    }

    /**
     * Get processed contacts
     * GET /api/contacts/processed
     */
    @GetMapping("/processed")
    public ResponseEntity<List<HRContact>> getProcessedContacts() {
        return ResponseEntity.ok(hrContactService.getProcessedContacts());
    }

    /**
     * Get contact by ID
     * GET /api/contacts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getContactById(@PathVariable Long id) {
        return hrContactService.getContactById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update contact
     * PUT /api/contacts/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable Long id, @RequestBody HRContact contact) {
        try {
            HRContact updated = hrContactService.updateContact(id, contact);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reactivate a contact
     * POST /api/contacts/{id}/reactivate
     */
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateContact(@PathVariable Long id) {
        try {
            HRContact reactivated = hrContactService.reactivateContact(id);
            return ResponseEntity.ok(reactivated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete contact
     * DELETE /api/contacts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        try {
            hrContactService.deleteContact(id);
            return ResponseEntity.ok(Map.of("message", "Contact deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get statistics
     * GET /api/contacts/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.ok(hrContactService.getStatistics());
    }

    /**
     * Manually trigger email sending (for testing)
     * POST /api/contacts/send-emails
     */
    @PostMapping("/send-emails")
    public ResponseEntity<?> sendEmailsManually() {
        String result = emailSchedulerService.sendEmailsManually();
        return ResponseEntity.ok(Map.of("message", result));
    }

    /**
     * Health check endpoint
     * GET /api/contacts/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "Job Email Automation System",
            "timestamp", System.currentTimeMillis()
        ));
    }
}

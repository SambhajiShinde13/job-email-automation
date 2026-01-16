package com.jobautomation.service;

import com.jobautomation.model.HRContact;
import com.jobautomation.repository.HRContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing HR contacts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HRContactService {

    private final HRContactRepository hrContactRepository;

    /**
     * Add a new HR contact
     */
    @Transactional
    public HRContact addContact(HRContact contact) {
        // Check if email already exists
        if (hrContactRepository.existsByEmail(contact.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + contact.getEmail());
        }

        HRContact saved = hrContactRepository.save(contact);
        log.info("✅ New HR contact added: {} at {}", saved.getHrName(), saved.getCompany());
        return saved;
    }

    /**
     * Add multiple contacts at once
     */
    @Transactional
    public List<HRContact> addContacts(List<HRContact> contacts) {
        int added = 0;
        int skipped = 0;

        for (HRContact contact : contacts) {
            try {
                if (!hrContactRepository.existsByEmail(contact.getEmail())) {
                    hrContactRepository.save(contact);
                    added++;
                } else {
                    log.warn("⚠️  Skipping duplicate email: {}", contact.getEmail());
                    skipped++;
                }
            } catch (Exception e) {
                log.error("❌ Error adding contact {}: {}", contact.getEmail(), e.getMessage());
                skipped++;
            }
        }

        log.info("📊 Bulk add completed - Added: {}, Skipped: {}", added, skipped);
        return hrContactRepository.findAll();
    }

    /**
     * Get all contacts
     */
    public List<HRContact> getAllContacts() {
        return hrContactRepository.findAll();
    }

    /**
     * Get active contacts only
     */
    public List<HRContact> getActiveContacts() {
        return hrContactRepository.findAll().stream()
            .filter(HRContact::getActive)
            .toList();
    }

    /**
     * Get processed (inactive) contacts
     */
    public List<HRContact> getProcessedContacts() {
        return hrContactRepository.findAll().stream()
            .filter(contact -> !contact.getActive())
            .toList();
    }

    /**
     * Get contact by ID
     */
    public Optional<HRContact> getContactById(Long id) {
        return hrContactRepository.findById(id);
    }

    /**
     * Update contact
     */
    @Transactional
    public HRContact updateContact(Long id, HRContact updatedContact) {
        HRContact existing = hrContactRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found with ID: " + id));

        existing.setHrName(updatedContact.getHrName());
        existing.setCompany(updatedContact.getCompany());
        existing.setPosition(updatedContact.getPosition());
        existing.setNotes(updatedContact.getNotes());
        
        // Don't allow changing email if it exists for another contact
        if (!existing.getEmail().equals(updatedContact.getEmail())) {
            if (hrContactRepository.existsByEmail(updatedContact.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + updatedContact.getEmail());
            }
            existing.setEmail(updatedContact.getEmail());
        }

        return hrContactRepository.save(existing);
    }

    /**
     * Reactivate a contact (make it active again for re-sending)
     */
    @Transactional
    public HRContact reactivateContact(Long id) {
        HRContact contact = hrContactRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found with ID: " + id));

        contact.setActive(true);
        contact.setEmailSentAt(null);
        
        log.info("🔄 Contact reactivated: {} at {}", contact.getHrName(), contact.getCompany());
        return hrContactRepository.save(contact);
    }

    /**
     * Delete a contact
     */
    @Transactional
    public void deleteContact(Long id) {
        if (!hrContactRepository.existsById(id)) {
            throw new IllegalArgumentException("Contact not found with ID: " + id);
        }
        hrContactRepository.deleteById(id);
        log.info("🗑️  Contact deleted: ID {}", id);
    }

    /**
     * Get statistics
     */
    public StatisticsDTO getStatistics() {
        long totalContacts = hrContactRepository.count();
        long activeContacts = hrContactRepository.countByActiveTrue();
        long processedContacts = hrContactRepository.countByActiveFalse();

        return new StatisticsDTO(totalContacts, activeContacts, processedContacts);
    }

    /**
     * DTO for statistics
     */
    public record StatisticsDTO(
        long totalContacts,
        long activeContacts,
        long processedContacts
    ) {}
}

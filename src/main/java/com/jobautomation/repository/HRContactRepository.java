package com.jobautomation.repository;

import com.jobautomation.model.HRContact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for HR Contact database operations
 */
@Repository
public interface HRContactRepository extends JpaRepository<HRContact, Long> {

    /**
     * Find active HR contacts, ordered by creation date (oldest first)
     * This ensures we process contacts in FIFO order
     */
    @Query("SELECT h FROM HRContact h WHERE h.active = true ORDER BY h.createdAt ASC")
    List<HRContact> findActiveContacts(Pageable pageable);

    Optional<HRContact> findByEmailIgnoreCase(String email);


    /**
     * Count total active contacts
     */
    long countByActiveTrue();

    /**
     * Count total inactive (processed) contacts
     */
    long countByActiveFalse();

    /**
     * Check if email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Find contact by email
     */
    HRContact findByEmail(String email);
}

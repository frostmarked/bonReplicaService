package com.bonlimousin.replica.service;

import com.bonlimousin.replica.domain.JournalEntryEntity;
import com.bonlimousin.replica.repository.JournalEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link JournalEntryEntity}.
 */
@Service
@Transactional
public class JournalEntryService {

    private final Logger log = LoggerFactory.getLogger(JournalEntryService.class);

    private final JournalEntryRepository journalEntryRepository;

    public JournalEntryService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    /**
     * Save a journalEntry.
     *
     * @param journalEntryEntity the entity to save.
     * @return the persisted entity.
     */
    public JournalEntryEntity save(JournalEntryEntity journalEntryEntity) {
        log.debug("Request to save JournalEntry : {}", journalEntryEntity);
        return journalEntryRepository.save(journalEntryEntity);
    }

    /**
     * Get all the journalEntries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<JournalEntryEntity> findAll(Pageable pageable) {
        log.debug("Request to get all JournalEntries");
        return journalEntryRepository.findAll(pageable);
    }


    /**
     * Get one journalEntry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<JournalEntryEntity> findOne(Long id) {
        log.debug("Request to get JournalEntry : {}", id);
        return journalEntryRepository.findById(id);
    }

    /**
     * Delete the journalEntry by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete JournalEntry : {}", id);
        journalEntryRepository.deleteById(id);
    }
}

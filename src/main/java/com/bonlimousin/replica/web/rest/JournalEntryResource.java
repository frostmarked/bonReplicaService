package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.domain.JournalEntryEntity;
import com.bonlimousin.replica.service.JournalEntryService;
import com.bonlimousin.replica.web.rest.errors.BadRequestAlertException;
import com.bonlimousin.replica.service.dto.JournalEntryCriteria;
import com.bonlimousin.replica.service.JournalEntryQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.bonlimousin.replica.domain.JournalEntryEntity}.
 */
@RestController
@RequestMapping("/api")
public class JournalEntryResource {

    private final Logger log = LoggerFactory.getLogger(JournalEntryResource.class);

    private final JournalEntryService journalEntryService;

    private final JournalEntryQueryService journalEntryQueryService;

    public JournalEntryResource(JournalEntryService journalEntryService, JournalEntryQueryService journalEntryQueryService) {
        this.journalEntryService = journalEntryService;
        this.journalEntryQueryService = journalEntryQueryService;
    }

    /**
     * {@code GET  /journal-entries} : get all the journalEntries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of journalEntries in body.
     */
    @GetMapping("/journal-entries")
    public ResponseEntity<List<JournalEntryEntity>> getAllJournalEntries(JournalEntryCriteria criteria, Pageable pageable) {
        log.debug("REST request to get JournalEntries by criteria: {}", criteria);
        Page<JournalEntryEntity> page = journalEntryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /journal-entries/count} : count all the journalEntries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/journal-entries/count")
    public ResponseEntity<Long> countJournalEntries(JournalEntryCriteria criteria) {
        log.debug("REST request to count JournalEntries by criteria: {}", criteria);
        return ResponseEntity.ok().body(journalEntryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /journal-entries/:id} : get the "id" journalEntry.
     *
     * @param id the id of the journalEntryEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the journalEntryEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/journal-entries/{id}")
    public ResponseEntity<JournalEntryEntity> getJournalEntry(@PathVariable Long id) {
        log.debug("REST request to get JournalEntry : {}", id);
        Optional<JournalEntryEntity> journalEntryEntity = journalEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(journalEntryEntity);
    }
}

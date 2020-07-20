package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.domain.BlupEntity;
import com.bonlimousin.replica.service.BlupService;
import com.bonlimousin.replica.web.rest.errors.BadRequestAlertException;
import com.bonlimousin.replica.service.dto.BlupCriteria;
import com.bonlimousin.replica.service.BlupQueryService;

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
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.bonlimousin.replica.domain.BlupEntity}.
 */
@RestController
@RequestMapping("/api")
public class BlupResource {

    private final Logger log = LoggerFactory.getLogger(BlupResource.class);

    private final BlupService blupService;

    private final BlupQueryService blupQueryService;

    public BlupResource(BlupService blupService, BlupQueryService blupQueryService) {
        this.blupService = blupService;
        this.blupQueryService = blupQueryService;
    }

    /**
     * {@code GET  /blups} : get all the blups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of blups in body.
     */
    @GetMapping("/blups")
    public ResponseEntity<List<BlupEntity>> getAllBlups(BlupCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Blups by criteria: {}", criteria);
        Page<BlupEntity> page = blupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /blups/count} : count all the blups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/blups/count")
    public ResponseEntity<Long> countBlups(BlupCriteria criteria) {
        log.debug("REST request to count Blups by criteria: {}", criteria);
        return ResponseEntity.ok().body(blupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /blups/:id} : get the "id" blup.
     *
     * @param id the id of the blupEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the blupEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/blups/{id}")
    public ResponseEntity<BlupEntity> getBlup(@PathVariable Long id) {
        log.debug("REST request to get Blup : {}", id);
        Optional<BlupEntity> blupEntity = blupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(blupEntity);
    }
}

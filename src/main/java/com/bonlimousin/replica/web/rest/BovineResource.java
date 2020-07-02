package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.service.BovineService;
import com.bonlimousin.replica.web.rest.errors.BadRequestAlertException;
import com.bonlimousin.replica.service.dto.BovineCriteria;
import com.bonlimousin.replica.service.BovineQueryService;

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
import java.util.stream.StreamSupport;

/**
 * REST controller for managing {@link com.bonlimousin.replica.domain.BovineEntity}.
 */
@RestController
@RequestMapping("/api")
public class BovineResource {

    private final Logger log = LoggerFactory.getLogger(BovineResource.class);

    private final BovineService bovineService;

    private final BovineQueryService bovineQueryService;

    public BovineResource(BovineService bovineService, BovineQueryService bovineQueryService) {
        this.bovineService = bovineService;
        this.bovineQueryService = bovineQueryService;
    }

    /**
     * {@code GET  /bovines} : get all the bovines.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bovines in body.
     */
    @GetMapping("/bovines")
    public ResponseEntity<List<BovineEntity>> getAllBovines(BovineCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Bovines by criteria: {}", criteria);
        Page<BovineEntity> page = bovineQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bovines/count} : count all the bovines.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/bovines/count")
    public ResponseEntity<Long> countBovines(BovineCriteria criteria) {
        log.debug("REST request to count Bovines by criteria: {}", criteria);
        return ResponseEntity.ok().body(bovineQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /bovines/:id} : get the "id" bovine.
     *
     * @param id the id of the bovineEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bovineEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bovines/{id}")
    public ResponseEntity<BovineEntity> getBovine(@PathVariable Long id) {
        log.debug("REST request to get Bovine : {}", id);
        Optional<BovineEntity> bovineEntity = bovineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bovineEntity);
    }
}

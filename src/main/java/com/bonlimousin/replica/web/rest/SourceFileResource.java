package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.service.SourceFileService;
import com.bonlimousin.replica.web.rest.errors.BadRequestAlertException;
import com.bonlimousin.replica.service.dto.SourceFileCriteria;
import com.bonlimousin.replica.service.SourceFileQueryService;

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
 * REST controller for managing {@link com.bonlimousin.replica.domain.SourceFileEntity}.
 */
@RestController
@RequestMapping("/api")
public class SourceFileResource {

    private final Logger log = LoggerFactory.getLogger(SourceFileResource.class);

    private static final String ENTITY_NAME = "bonReplicaServiceSourceFile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SourceFileService sourceFileService;

    private final SourceFileQueryService sourceFileQueryService;

    public SourceFileResource(SourceFileService sourceFileService, SourceFileQueryService sourceFileQueryService) {
        this.sourceFileService = sourceFileService;
        this.sourceFileQueryService = sourceFileQueryService;
    }

    /**
     * {@code POST  /source-files} : Create a new sourceFile.
     *
     * @param sourceFileEntity the sourceFileEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sourceFileEntity, or with status {@code 400 (Bad Request)} if the sourceFile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/source-files")
    public ResponseEntity<SourceFileEntity> createSourceFile(@Valid @RequestBody SourceFileEntity sourceFileEntity) throws URISyntaxException {
        log.debug("REST request to save SourceFile : {}", sourceFileEntity);
        if (sourceFileEntity.getId() != null) {
            throw new BadRequestAlertException("A new sourceFile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SourceFileEntity result = sourceFileService.save(sourceFileEntity);
        return ResponseEntity.created(new URI("/api/source-files/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /source-files} : Updates an existing sourceFile.
     *
     * @param sourceFileEntity the sourceFileEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sourceFileEntity,
     * or with status {@code 400 (Bad Request)} if the sourceFileEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sourceFileEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/source-files")
    public ResponseEntity<SourceFileEntity> updateSourceFile(@Valid @RequestBody SourceFileEntity sourceFileEntity) throws URISyntaxException {
        log.debug("REST request to update SourceFile : {}", sourceFileEntity);
        if (sourceFileEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SourceFileEntity result = sourceFileService.save(sourceFileEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sourceFileEntity.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /source-files} : get all the sourceFiles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sourceFiles in body.
     */
    @GetMapping("/source-files")
    public ResponseEntity<List<SourceFileEntity>> getAllSourceFiles(SourceFileCriteria criteria, Pageable pageable) {
        log.debug("REST request to get SourceFiles by criteria: {}", criteria);
        Page<SourceFileEntity> page = sourceFileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /source-files/count} : count all the sourceFiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/source-files/count")
    public ResponseEntity<Long> countSourceFiles(SourceFileCriteria criteria) {
        log.debug("REST request to count SourceFiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(sourceFileQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /source-files/:id} : get the "id" sourceFile.
     *
     * @param id the id of the sourceFileEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sourceFileEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/source-files/{id}")
    public ResponseEntity<SourceFileEntity> getSourceFile(@PathVariable Long id) {
        log.debug("REST request to get SourceFile : {}", id);
        Optional<SourceFileEntity> sourceFileEntity = sourceFileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sourceFileEntity);
    }

    /**
     * {@code DELETE  /source-files/:id} : delete the "id" sourceFile.
     *
     * @param id the id of the sourceFileEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/source-files/{id}")
    public ResponseEntity<Void> deleteSourceFile(@PathVariable Long id) {
        log.debug("REST request to delete SourceFile : {}", id);
        sourceFileService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}

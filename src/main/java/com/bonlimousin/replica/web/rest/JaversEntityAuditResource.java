package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.domain.EntityAuditEvent;
import com.bonlimousin.replica.security.AuthoritiesConstants;

import io.github.jhipster.web.util.PaginationUtil;

import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * REST controller for getting the audit events for entity
 */
@RestController
@RequestMapping("/api")
public class JaversEntityAuditResource {

    private final Logger log = LoggerFactory.getLogger(JaversEntityAuditResource.class);

    private final Javers javers;

    public JaversEntityAuditResource(Javers javers) {
        this.javers = javers;
    }

    /**
     * fetches all the audited entity types
     *
     * @return
     */
    @RequestMapping(value = "/audits/entity/all",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<String> getAuditedEntities() {

      return Arrays.asList("Blup", "Bovine", "JournalEntry", "SourceFile");
    }

    /**
     * fetches the last 100 change list for an entity class, if limit is passed fetches that many changes
     *
     * @return
     */
    @RequestMapping(value = "/audits/entity/changes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<EntityAuditEvent>> getChanges(@RequestParam(value = "entityType") String entityType,
                                                             @RequestParam(value = "limit") int limit,
                                                             @RequestParam MultiValueMap<String, String> queryParams,
                                                             UriComponentsBuilder uriBuilder)
        throws ClassNotFoundException {
        log.debug("REST request to get a page of EntityAuditEvents");

        Class entityTypeToFetch = Class.forName("com.bonlimousin.replica.domain." + entityType);
        QueryBuilder jqlQuery = QueryBuilder.byClass(entityTypeToFetch)
                                            .limit(limit)
                                            .withNewObjectChanges(true);

        List<CdoSnapshot> snapshots =  javers.findSnapshots(jqlQuery.build());

        List<EntityAuditEvent> auditEvents = new ArrayList<>();

        snapshots.forEach(snapshot -> {
           EntityAuditEvent event = EntityAuditEvent.fromJaversSnapshot(snapshot);
           event.setEntityType(entityType);
           auditEvents.add(event);
        });

        Page<EntityAuditEvent> page = new PageImpl<>(auditEvents);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);

        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

    }

    /**
     * fetches a previous version for for an entity class and id
     *
     * @return
     */
    @RequestMapping(value = "/audits/entity/changes/version/previous",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<EntityAuditEvent> getPrevVersion(@RequestParam(value = "qualifiedName") String qualifiedName,
                                                           @RequestParam(value = "entityId") String entityId,
                                                           @RequestParam(value = "commitVersion") Long commitVersion)
        throws ClassNotFoundException {

        Class entityTypeToFetch = Class.forName("com.bonlimousin.replica.domain." + qualifiedName);

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(entityId, entityTypeToFetch)
                                           .limit(1)
                                           .withVersion(commitVersion - 1)
                                           .withNewObjectChanges(true);

        EntityAuditEvent prev = EntityAuditEvent.fromJaversSnapshot(javers.findSnapshots(jqlQuery.build()).get(0));

        return new ResponseEntity<>(prev, HttpStatus.OK);

    }

}

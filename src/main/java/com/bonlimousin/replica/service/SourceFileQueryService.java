package com.bonlimousin.replica.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.domain.*; // for static metamodels
import com.bonlimousin.replica.repository.SourceFileRepository;
import com.bonlimousin.replica.service.dto.SourceFileCriteria;

/**
 * Service for executing complex queries for {@link SourceFileEntity} entities in the database.
 * The main input is a {@link SourceFileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SourceFileEntity} or a {@link Page} of {@link SourceFileEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SourceFileQueryService extends QueryService<SourceFileEntity> {

    private final Logger log = LoggerFactory.getLogger(SourceFileQueryService.class);

    private final SourceFileRepository sourceFileRepository;

    public SourceFileQueryService(SourceFileRepository sourceFileRepository) {
        this.sourceFileRepository = sourceFileRepository;
    }

    /**
     * Return a {@link List} of {@link SourceFileEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SourceFileEntity> findByCriteria(SourceFileCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SourceFileEntity> specification = createSpecification(criteria);
        return sourceFileRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link SourceFileEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SourceFileEntity> findByCriteria(SourceFileCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SourceFileEntity> specification = createSpecification(criteria);
        return sourceFileRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SourceFileCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SourceFileEntity> specification = createSpecification(criteria);
        return sourceFileRepository.count(specification);
    }

    /**
     * Function to convert {@link SourceFileCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SourceFileEntity> createSpecification(SourceFileCriteria criteria) {
        Specification<SourceFileEntity> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SourceFileEntity_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), SourceFileEntity_.name));
            }
            if (criteria.getProcessed() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getProcessed(), SourceFileEntity_.processed));
            }
            if (criteria.getOutcome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOutcome(), SourceFileEntity_.outcome));
            }
        }
        return specification;
    }
}

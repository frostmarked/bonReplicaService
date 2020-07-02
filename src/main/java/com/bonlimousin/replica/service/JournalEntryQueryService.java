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

import com.bonlimousin.replica.domain.JournalEntryEntity;
import com.bonlimousin.replica.domain.*; // for static metamodels
import com.bonlimousin.replica.repository.JournalEntryRepository;
import com.bonlimousin.replica.service.dto.JournalEntryCriteria;

/**
 * Service for executing complex queries for {@link JournalEntryEntity} entities in the database.
 * The main input is a {@link JournalEntryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link JournalEntryEntity} or a {@link Page} of {@link JournalEntryEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JournalEntryQueryService extends QueryService<JournalEntryEntity> {

    private final Logger log = LoggerFactory.getLogger(JournalEntryQueryService.class);

    private final JournalEntryRepository journalEntryRepository;

    public JournalEntryQueryService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    /**
     * Return a {@link List} of {@link JournalEntryEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<JournalEntryEntity> findByCriteria(JournalEntryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<JournalEntryEntity> specification = createSpecification(criteria);
        return journalEntryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link JournalEntryEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JournalEntryEntity> findByCriteria(JournalEntryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<JournalEntryEntity> specification = createSpecification(criteria);
        return journalEntryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JournalEntryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<JournalEntryEntity> specification = createSpecification(criteria);
        return journalEntryRepository.count(specification);
    }

    /**
     * Function to convert {@link JournalEntryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<JournalEntryEntity> createSpecification(JournalEntryCriteria criteria) {
        Specification<JournalEntryEntity> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), JournalEntryEntity_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), JournalEntryEntity_.status));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), JournalEntryEntity_.date));
            }
            if (criteria.getEdited() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEdited(), JournalEntryEntity_.edited));
            }
            if (criteria.getHerdId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHerdId(), JournalEntryEntity_.herdId));
            }
            if (criteria.getNewHerdId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNewHerdId(), JournalEntryEntity_.newHerdId));
            }
            if (criteria.getSubState1() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSubState1(), JournalEntryEntity_.subState1));
            }
            if (criteria.getSubState2() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSubState2(), JournalEntryEntity_.subState2));
            }
            if (criteria.getBovineId() != null) {
                specification = specification.and(buildSpecification(criteria.getBovineId(),
                    root -> root.join(JournalEntryEntity_.bovine, JoinType.LEFT).get(BovineEntity_.id)));
            }
        }
        return specification;
    }
}

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

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.*; // for static metamodels
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.dto.BovineCriteria;

/**
 * Service for executing complex queries for {@link BovineEntity} entities in the database.
 * The main input is a {@link BovineCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BovineEntity} or a {@link Page} of {@link BovineEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BovineQueryService extends QueryService<BovineEntity> {

    private final Logger log = LoggerFactory.getLogger(BovineQueryService.class);

    private final BovineRepository bovineRepository;

    public BovineQueryService(BovineRepository bovineRepository) {
        this.bovineRepository = bovineRepository;
    }

    /**
     * Return a {@link List} of {@link BovineEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BovineEntity> findByCriteria(BovineCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BovineEntity> specification = createSpecification(criteria);
        return bovineRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link BovineEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BovineEntity> findByCriteria(BovineCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BovineEntity> specification = createSpecification(criteria);
        return bovineRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BovineCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BovineEntity> specification = createSpecification(criteria);
        return bovineRepository.count(specification);
    }

    /**
     * Function to convert {@link BovineCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BovineEntity> createSpecification(BovineCriteria criteria) {
        Specification<BovineEntity> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BovineEntity_.id));
            }
            if (criteria.getEarTagId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEarTagId(), BovineEntity_.earTagId));
            }
            if (criteria.getMasterIdentifier() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMasterIdentifier(), BovineEntity_.masterIdentifier));
            }
            if (criteria.getCountry() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountry(), BovineEntity_.country));
            }
            if (criteria.getHerdId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHerdId(), BovineEntity_.herdId));
            }
            if (criteria.getBirthDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBirthDate(), BovineEntity_.birthDate));
            }
            if (criteria.getGender() != null) {
                specification = specification.and(buildSpecification(criteria.getGender(), BovineEntity_.gender));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), BovineEntity_.name));
            }
            if (criteria.getBovineStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getBovineStatus(), BovineEntity_.bovineStatus));
            }
            if (criteria.getHornStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getHornStatus(), BovineEntity_.hornStatus));
            }
            if (criteria.getMatriId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMatriId(), BovineEntity_.matriId));
            }
            if (criteria.getPatriId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPatriId(), BovineEntity_.patriId));
            }
            if (criteria.getWeight0() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getWeight0(), BovineEntity_.weight0));
            }
            if (criteria.getWeight200() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getWeight200(), BovineEntity_.weight200));
            }
            if (criteria.getWeight365() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getWeight365(), BovineEntity_.weight365));
            }
            if (criteria.getJournalEntriesId() != null) {
                specification = specification.and(buildSpecification(criteria.getJournalEntriesId(),
                    root -> root.join(BovineEntity_.journalEntries, JoinType.LEFT).get(JournalEntryEntity_.id)));
            }
            if (criteria.getSourceFileId() != null) {
                specification = specification.and(buildSpecification(criteria.getSourceFileId(),
                    root -> root.join(BovineEntity_.sourceFile, JoinType.LEFT).get(SourceFileEntity_.id)));
            }
            if (criteria.getBlupId() != null) {
                specification = specification.and(buildSpecification(criteria.getBlupId(),
                    root -> root.join(BovineEntity_.blup, JoinType.LEFT).get(BlupEntity_.id)));
            }
        }
        return specification;
    }
}

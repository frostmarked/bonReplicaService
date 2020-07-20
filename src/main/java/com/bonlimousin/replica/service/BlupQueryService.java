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

import com.bonlimousin.replica.domain.BlupEntity;
import com.bonlimousin.replica.domain.*; // for static metamodels
import com.bonlimousin.replica.repository.BlupRepository;
import com.bonlimousin.replica.service.dto.BlupCriteria;

/**
 * Service for executing complex queries for {@link BlupEntity} entities in the database.
 * The main input is a {@link BlupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BlupEntity} or a {@link Page} of {@link BlupEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BlupQueryService extends QueryService<BlupEntity> {

    private final Logger log = LoggerFactory.getLogger(BlupQueryService.class);

    private final BlupRepository blupRepository;

    public BlupQueryService(BlupRepository blupRepository) {
        this.blupRepository = blupRepository;
    }

    /**
     * Return a {@link List} of {@link BlupEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BlupEntity> findByCriteria(BlupCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BlupEntity> specification = createSpecification(criteria);
        return blupRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link BlupEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BlupEntity> findByCriteria(BlupCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BlupEntity> specification = createSpecification(criteria);
        return blupRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BlupCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BlupEntity> specification = createSpecification(criteria);
        return blupRepository.count(specification);
    }

    /**
     * Function to convert {@link BlupCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BlupEntity> createSpecification(BlupCriteria criteria) {
        Specification<BlupEntity> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BlupEntity_.id));
            }
            if (criteria.gett0() != null) {
                specification = specification.and(buildRangeSpecification(criteria.gett0(), BlupEntity_.t0));
            }
            if (criteria.getd0() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getd0(), BlupEntity_.d0));
            }
            if (criteria.getm0() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getm0(), BlupEntity_.m0));
            }
            if (criteria.gett200() != null) {
                specification = specification.and(buildRangeSpecification(criteria.gett200(), BlupEntity_.t200));
            }
            if (criteria.getd200() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getd200(), BlupEntity_.d200));
            }
            if (criteria.getm200() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getm200(), BlupEntity_.m200));
            }
            if (criteria.gett365() != null) {
                specification = specification.and(buildRangeSpecification(criteria.gett365(), BlupEntity_.t365));
            }
            if (criteria.getd365() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getd365(), BlupEntity_.d365));
            }
            if (criteria.getTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotal(), BlupEntity_.total));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStatus(), BlupEntity_.status));
            }
            if (criteria.getBovineId() != null) {
                specification = specification.and(buildSpecification(criteria.getBovineId(),
                    root -> root.join(BlupEntity_.bovine, JoinType.LEFT).get(BovineEntity_.id)));
            }
        }
        return specification;
    }
}

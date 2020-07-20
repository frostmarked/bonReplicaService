package com.bonlimousin.replica.service;

import com.bonlimousin.replica.domain.BlupEntity;
import com.bonlimousin.replica.repository.BlupRepository;
import com.bonlimousin.replica.repository.BovineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link BlupEntity}.
 */
@Service
@Transactional
public class BlupService {

    private final Logger log = LoggerFactory.getLogger(BlupService.class);

    private final BlupRepository blupRepository;

    private final BovineRepository bovineRepository;

    public BlupService(BlupRepository blupRepository, BovineRepository bovineRepository) {
        this.blupRepository = blupRepository;
        this.bovineRepository = bovineRepository;
    }

    /**
     * Save a blup.
     *
     * @param blupEntity the entity to save.
     * @return the persisted entity.
     */
    public BlupEntity save(BlupEntity blupEntity) {
        log.debug("Request to save Blup : {}", blupEntity);
        Long bovineId = blupEntity.getBovine().getId();
        bovineRepository.findById(bovineId).ifPresent(blupEntity::bovine);
        return blupRepository.save(blupEntity);
    }

    /**
     * Get all the blups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BlupEntity> findAll(Pageable pageable) {
        log.debug("Request to get all Blups");
        return blupRepository.findAll(pageable);
    }


    /**
     * Get one blup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BlupEntity> findOne(Long id) {
        log.debug("Request to get Blup : {}", id);
        return blupRepository.findById(id);
    }

    /**
     * Delete the blup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Blup : {}", id);
        blupRepository.deleteById(id);
    }
}

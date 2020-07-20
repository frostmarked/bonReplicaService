package com.bonlimousin.replica.service;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.repository.BovineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Implementation for managing {@link BovineEntity}.
 */
@Service
@Transactional
public class BovineService {

    private final Logger log = LoggerFactory.getLogger(BovineService.class);

    private final BovineRepository bovineRepository;

    public BovineService(BovineRepository bovineRepository) {
        this.bovineRepository = bovineRepository;
    }

    /**
     * Save a bovine.
     *
     * @param bovineEntity the entity to save.
     * @return the persisted entity.
     */
    public BovineEntity save(BovineEntity bovineEntity) {
        log.debug("Request to save Bovine : {}", bovineEntity);
        return bovineRepository.save(bovineEntity);
    }

    /**
     * Get all the bovines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BovineEntity> findAll(Pageable pageable) {
        log.debug("Request to get all Bovines");
        return bovineRepository.findAll(pageable);
    }



    /**
     *  Get all the bovines where Blup is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true) 
    public List<BovineEntity> findAllWhereBlupIsNull() {
        log.debug("Request to get all bovines where Blup is null");
        return StreamSupport
            .stream(bovineRepository.findAll().spliterator(), false)
            .filter(bovine -> bovine.getBlup() == null)
            .collect(Collectors.toList());
    }

    /**
     * Get one bovine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BovineEntity> findOne(Long id) {
        log.debug("Request to get Bovine : {}", id);
        return bovineRepository.findById(id);
    }

    /**
     * Delete the bovine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Bovine : {}", id);
        bovineRepository.deleteById(id);
    }
}

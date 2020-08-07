package com.bonlimousin.replica.service;

import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.repository.SourceFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link SourceFileEntity}.
 */
@Service
@Transactional
public class SourceFileService {

    private final Logger log = LoggerFactory.getLogger(SourceFileService.class);

    private final SourceFileRepository sourceFileRepository;

    public SourceFileService(SourceFileRepository sourceFileRepository) {
        this.sourceFileRepository = sourceFileRepository;
    }

    /**
     * Save a sourceFile.
     *
     * @param sourceFileEntity the entity to save.
     * @return the persisted entity.
     */
    public SourceFileEntity save(SourceFileEntity sourceFileEntity) {
        log.debug("Request to save SourceFile : {}", sourceFileEntity);
        return sourceFileRepository.save(sourceFileEntity);
    }

    /**
     * Get all the sourceFiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SourceFileEntity> findAll(Pageable pageable) {
        log.debug("Request to get all SourceFiles");
        return sourceFileRepository.findAll(pageable);
    }


    /**
     * Get one sourceFile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SourceFileEntity> findOne(Long id) {
        log.debug("Request to get SourceFile : {}", id);
        return sourceFileRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean exists(Long id) {
		return sourceFileRepository.existsById(id);
	}

    /**
     * Delete the sourceFile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SourceFile : {}", id);
        sourceFileRepository.deleteById(id);
    }
}

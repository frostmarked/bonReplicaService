package com.bonlimousin.replica.repository;

import com.bonlimousin.replica.domain.SourceFileEntity;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the SourceFileEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SourceFileRepository extends JpaRepository<SourceFileEntity, Long>, JpaSpecificationExecutor<SourceFileEntity> {
}

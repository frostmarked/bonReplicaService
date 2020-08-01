package com.bonlimousin.replica.repository;

import com.bonlimousin.replica.domain.SourceFileEntity;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the SourceFileEntity entity.
 */
@Repository
@JaversSpringDataAuditable
public interface SourceFileRepository extends JpaRepository<SourceFileEntity, Long>, JpaSpecificationExecutor<SourceFileEntity> {
}

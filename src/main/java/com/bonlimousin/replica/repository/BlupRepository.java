package com.bonlimousin.replica.repository;

import com.bonlimousin.replica.domain.BlupEntity;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the BlupEntity entity.
 */
@Repository
@JaversSpringDataAuditable
public interface BlupRepository extends JpaRepository<BlupEntity, Long>, JpaSpecificationExecutor<BlupEntity> {
}

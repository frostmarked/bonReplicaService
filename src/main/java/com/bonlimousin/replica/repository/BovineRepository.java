package com.bonlimousin.replica.repository;

import com.bonlimousin.replica.domain.BovineEntity;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the BovineEntity entity.
 */
@Repository
@JaversSpringDataAuditable
public interface BovineRepository extends JpaRepository<BovineEntity, Long>, JpaSpecificationExecutor<BovineEntity> {
}

package com.bonlimousin.replica.repository;

import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bonlimousin.replica.domain.BovineEntity;

/**
 * Spring Data  repository for the BovineEntity entity.
 */
@Repository
@JaversSpringDataAuditable
public interface BovineRepository extends JpaRepository<BovineEntity, Long>, JpaSpecificationExecutor<BovineEntity> {

	Optional<BovineEntity> findOneByEarTagId(Integer earTagId);
	
	Optional<BovineEntity> findOneByEarTagIdAndHerdId(Integer earTagId, Integer herdId);
	
}

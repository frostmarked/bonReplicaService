package com.bonlimousin.replica.repository;

import com.bonlimousin.replica.domain.JournalEntryEntity;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the JournalEntryEntity entity.
 */
@Repository
@JaversSpringDataAuditable
public interface JournalEntryRepository extends JpaRepository<JournalEntryEntity, Long>, JpaSpecificationExecutor<JournalEntryEntity> {
}

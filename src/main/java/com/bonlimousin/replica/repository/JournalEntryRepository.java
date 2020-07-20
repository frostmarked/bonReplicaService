package com.bonlimousin.replica.repository;

import com.bonlimousin.replica.domain.JournalEntryEntity;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the JournalEntryEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntryEntity, Long>, JpaSpecificationExecutor<JournalEntryEntity> {
}

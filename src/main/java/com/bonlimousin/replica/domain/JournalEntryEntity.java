package com.bonlimousin.replica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

import com.bonlimousin.replica.domain.enumeration.EntryStatus;

/**
 * A JournalEntryEntity.
 */
@Entity
@Table(name = "bon_replica_journal_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JournalEntryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EntryStatus status;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "edited")
    private Instant edited;

    @NotNull
    @Min(value = 0)
    @Column(name = "herd_id", nullable = false)
    private Integer herdId;

    @Min(value = 0)
    @Column(name = "new_herd_id")
    private Integer newHerdId;

    @Column(name = "sub_state_1")
    private Integer subState1;

    @Column(name = "sub_state_2")
    private Integer subState2;

    @ManyToOne
    @JsonIgnoreProperties(value = "journalEntries", allowSetters = true)
    private BovineEntity bovine;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntryStatus getStatus() {
        return status;
    }

    public JournalEntryEntity status(EntryStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(EntryStatus status) {
        this.status = status;
    }

    public Instant getDate() {
        return date;
    }

    public JournalEntryEntity date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getEdited() {
        return edited;
    }

    public JournalEntryEntity edited(Instant edited) {
        this.edited = edited;
        return this;
    }

    public void setEdited(Instant edited) {
        this.edited = edited;
    }

    public Integer getHerdId() {
        return herdId;
    }

    public JournalEntryEntity herdId(Integer herdId) {
        this.herdId = herdId;
        return this;
    }

    public void setHerdId(Integer herdId) {
        this.herdId = herdId;
    }

    public Integer getNewHerdId() {
        return newHerdId;
    }

    public JournalEntryEntity newHerdId(Integer newHerdId) {
        this.newHerdId = newHerdId;
        return this;
    }

    public void setNewHerdId(Integer newHerdId) {
        this.newHerdId = newHerdId;
    }

    public Integer getSubState1() {
        return subState1;
    }

    public JournalEntryEntity subState1(Integer subState1) {
        this.subState1 = subState1;
        return this;
    }

    public void setSubState1(Integer subState1) {
        this.subState1 = subState1;
    }

    public Integer getSubState2() {
        return subState2;
    }

    public JournalEntryEntity subState2(Integer subState2) {
        this.subState2 = subState2;
        return this;
    }

    public void setSubState2(Integer subState2) {
        this.subState2 = subState2;
    }

    public BovineEntity getBovine() {
        return bovine;
    }

    public JournalEntryEntity bovine(BovineEntity bovine) {
        this.bovine = bovine;
        return this;
    }

    public void setBovine(BovineEntity bovine) {
        this.bovine = bovine;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JournalEntryEntity)) {
            return false;
        }
        return id != null && id.equals(((JournalEntryEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalEntryEntity{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", date='" + getDate() + "'" +
            ", edited='" + getEdited() + "'" +
            ", herdId=" + getHerdId() +
            ", newHerdId=" + getNewHerdId() +
            ", subState1=" + getSubState1() +
            ", subState2=" + getSubState2() +
            "}";
    }
}

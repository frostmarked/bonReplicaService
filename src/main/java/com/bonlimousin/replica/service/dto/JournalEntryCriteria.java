package com.bonlimousin.replica.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.bonlimousin.replica.domain.enumeration.EntryStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.bonlimousin.replica.domain.JournalEntryEntity} entity. This class is used
 * in {@link com.bonlimousin.replica.web.rest.JournalEntryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /journal-entries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class JournalEntryCriteria implements Serializable, Criteria {
    /**
     * Class for filtering EntryStatus
     */
    public static class EntryStatusFilter extends Filter<EntryStatus> {

        public EntryStatusFilter() {
        }

        public EntryStatusFilter(EntryStatusFilter filter) {
            super(filter);
        }

        @Override
        public EntryStatusFilter copy() {
            return new EntryStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private EntryStatusFilter status;

    private InstantFilter date;

    private InstantFilter edited;

    private IntegerFilter herdId;

    private IntegerFilter newHerdId;

    private IntegerFilter subState1;

    private IntegerFilter subState2;

    private LongFilter bovineId;

    public JournalEntryCriteria() {
    }

    public JournalEntryCriteria(JournalEntryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.edited = other.edited == null ? null : other.edited.copy();
        this.herdId = other.herdId == null ? null : other.herdId.copy();
        this.newHerdId = other.newHerdId == null ? null : other.newHerdId.copy();
        this.subState1 = other.subState1 == null ? null : other.subState1.copy();
        this.subState2 = other.subState2 == null ? null : other.subState2.copy();
        this.bovineId = other.bovineId == null ? null : other.bovineId.copy();
    }

    @Override
    public JournalEntryCriteria copy() {
        return new JournalEntryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public EntryStatusFilter getStatus() {
        return status;
    }

    public void setStatus(EntryStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getDate() {
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public InstantFilter getEdited() {
        return edited;
    }

    public void setEdited(InstantFilter edited) {
        this.edited = edited;
    }

    public IntegerFilter getHerdId() {
        return herdId;
    }

    public void setHerdId(IntegerFilter herdId) {
        this.herdId = herdId;
    }

    public IntegerFilter getNewHerdId() {
        return newHerdId;
    }

    public void setNewHerdId(IntegerFilter newHerdId) {
        this.newHerdId = newHerdId;
    }

    public IntegerFilter getSubState1() {
        return subState1;
    }

    public void setSubState1(IntegerFilter subState1) {
        this.subState1 = subState1;
    }

    public IntegerFilter getSubState2() {
        return subState2;
    }

    public void setSubState2(IntegerFilter subState2) {
        this.subState2 = subState2;
    }

    public LongFilter getBovineId() {
        return bovineId;
    }

    public void setBovineId(LongFilter bovineId) {
        this.bovineId = bovineId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JournalEntryCriteria that = (JournalEntryCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(date, that.date) &&
            Objects.equals(edited, that.edited) &&
            Objects.equals(herdId, that.herdId) &&
            Objects.equals(newHerdId, that.newHerdId) &&
            Objects.equals(subState1, that.subState1) &&
            Objects.equals(subState2, that.subState2) &&
            Objects.equals(bovineId, that.bovineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        status,
        date,
        edited,
        herdId,
        newHerdId,
        subState1,
        subState2,
        bovineId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalEntryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (edited != null ? "edited=" + edited + ", " : "") +
                (herdId != null ? "herdId=" + herdId + ", " : "") +
                (newHerdId != null ? "newHerdId=" + newHerdId + ", " : "") +
                (subState1 != null ? "subState1=" + subState1 + ", " : "") +
                (subState2 != null ? "subState2=" + subState2 + ", " : "") +
                (bovineId != null ? "bovineId=" + bovineId + ", " : "") +
            "}";
    }

}

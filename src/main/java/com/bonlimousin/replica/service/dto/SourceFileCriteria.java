package com.bonlimousin.replica.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.bonlimousin.replica.domain.SourceFileEntity} entity. This class is used
 * in {@link com.bonlimousin.replica.web.rest.SourceFileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /source-files?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SourceFileCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private InstantFilter processed;

    private StringFilter outcome;

    public SourceFileCriteria() {
    }

    public SourceFileCriteria(SourceFileCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.processed = other.processed == null ? null : other.processed.copy();
        this.outcome = other.outcome == null ? null : other.outcome.copy();
    }

    @Override
    public SourceFileCriteria copy() {
        return new SourceFileCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public InstantFilter getProcessed() {
        return processed;
    }

    public void setProcessed(InstantFilter processed) {
        this.processed = processed;
    }

    public StringFilter getOutcome() {
        return outcome;
    }

    public void setOutcome(StringFilter outcome) {
        this.outcome = outcome;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SourceFileCriteria that = (SourceFileCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(processed, that.processed) &&
            Objects.equals(outcome, that.outcome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        processed,
        outcome
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SourceFileCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (processed != null ? "processed=" + processed + ", " : "") +
                (outcome != null ? "outcome=" + outcome + ", " : "") +
            "}";
    }

}

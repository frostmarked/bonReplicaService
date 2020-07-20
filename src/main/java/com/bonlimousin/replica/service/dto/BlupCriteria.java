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

/**
 * Criteria class for the {@link com.bonlimousin.replica.domain.BlupEntity} entity. This class is used
 * in {@link com.bonlimousin.replica.web.rest.BlupResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /blups?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BlupCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter t0;

    private IntegerFilter d0;

    private IntegerFilter m0;

    private IntegerFilter t200;

    private IntegerFilter d200;

    private IntegerFilter m200;

    private IntegerFilter t365;

    private IntegerFilter d365;

    private IntegerFilter total;

    private StringFilter status;

    private LongFilter bovineId;

    public BlupCriteria() {
    }

    public BlupCriteria(BlupCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.t0 = other.t0 == null ? null : other.t0.copy();
        this.d0 = other.d0 == null ? null : other.d0.copy();
        this.m0 = other.m0 == null ? null : other.m0.copy();
        this.t200 = other.t200 == null ? null : other.t200.copy();
        this.d200 = other.d200 == null ? null : other.d200.copy();
        this.m200 = other.m200 == null ? null : other.m200.copy();
        this.t365 = other.t365 == null ? null : other.t365.copy();
        this.d365 = other.d365 == null ? null : other.d365.copy();
        this.total = other.total == null ? null : other.total.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.bovineId = other.bovineId == null ? null : other.bovineId.copy();
    }

    @Override
    public BlupCriteria copy() {
        return new BlupCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter gett0() {
        return t0;
    }

    public void sett0(IntegerFilter t0) {
        this.t0 = t0;
    }

    public IntegerFilter getd0() {
        return d0;
    }

    public void setd0(IntegerFilter d0) {
        this.d0 = d0;
    }

    public IntegerFilter getm0() {
        return m0;
    }

    public void setm0(IntegerFilter m0) {
        this.m0 = m0;
    }

    public IntegerFilter gett200() {
        return t200;
    }

    public void sett200(IntegerFilter t200) {
        this.t200 = t200;
    }

    public IntegerFilter getd200() {
        return d200;
    }

    public void setd200(IntegerFilter d200) {
        this.d200 = d200;
    }

    public IntegerFilter getm200() {
        return m200;
    }

    public void setm200(IntegerFilter m200) {
        this.m200 = m200;
    }

    public IntegerFilter gett365() {
        return t365;
    }

    public void sett365(IntegerFilter t365) {
        this.t365 = t365;
    }

    public IntegerFilter getd365() {
        return d365;
    }

    public void setd365(IntegerFilter d365) {
        this.d365 = d365;
    }

    public IntegerFilter getTotal() {
        return total;
    }

    public void setTotal(IntegerFilter total) {
        this.total = total;
    }

    public StringFilter getStatus() {
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
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
        final BlupCriteria that = (BlupCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(t0, that.t0) &&
            Objects.equals(d0, that.d0) &&
            Objects.equals(m0, that.m0) &&
            Objects.equals(t200, that.t200) &&
            Objects.equals(d200, that.d200) &&
            Objects.equals(m200, that.m200) &&
            Objects.equals(t365, that.t365) &&
            Objects.equals(d365, that.d365) &&
            Objects.equals(total, that.total) &&
            Objects.equals(status, that.status) &&
            Objects.equals(bovineId, that.bovineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        t0,
        d0,
        m0,
        t200,
        d200,
        m200,
        t365,
        d365,
        total,
        status,
        bovineId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlupCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (t0 != null ? "t0=" + t0 + ", " : "") +
                (d0 != null ? "d0=" + d0 + ", " : "") +
                (m0 != null ? "m0=" + m0 + ", " : "") +
                (t200 != null ? "t200=" + t200 + ", " : "") +
                (d200 != null ? "d200=" + d200 + ", " : "") +
                (m200 != null ? "m200=" + m200 + ", " : "") +
                (t365 != null ? "t365=" + t365 + ", " : "") +
                (d365 != null ? "d365=" + d365 + ", " : "") +
                (total != null ? "total=" + total + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (bovineId != null ? "bovineId=" + bovineId + ", " : "") +
            "}";
    }

}

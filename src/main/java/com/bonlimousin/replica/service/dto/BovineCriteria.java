package com.bonlimousin.replica.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.HornStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.bonlimousin.replica.domain.BovineEntity} entity. This class is used
 * in {@link com.bonlimousin.replica.web.rest.BovineResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bovines?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BovineCriteria implements Serializable, Criteria {
    /**
     * Class for filtering Gender
     */
    public static class GenderFilter extends Filter<Gender> {

        public GenderFilter() {
        }

        public GenderFilter(GenderFilter filter) {
            super(filter);
        }

        @Override
        public GenderFilter copy() {
            return new GenderFilter(this);
        }

    }
    /**
     * Class for filtering BovineStatus
     */
    public static class BovineStatusFilter extends Filter<BovineStatus> {

        public BovineStatusFilter() {
        }

        public BovineStatusFilter(BovineStatusFilter filter) {
            super(filter);
        }

        @Override
        public BovineStatusFilter copy() {
            return new BovineStatusFilter(this);
        }

    }
    /**
     * Class for filtering HornStatus
     */
    public static class HornStatusFilter extends Filter<HornStatus> {

        public HornStatusFilter() {
        }

        public HornStatusFilter(HornStatusFilter filter) {
            super(filter);
        }

        @Override
        public HornStatusFilter copy() {
            return new HornStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter earTagId;

    private StringFilter masterIdentifier;

    private StringFilter country;

    private IntegerFilter herdId;

    private InstantFilter birthDate;

    private GenderFilter gender;

    private StringFilter name;

    private BovineStatusFilter bovineStatus;

    private HornStatusFilter hornStatus;

    private IntegerFilter matriId;

    private IntegerFilter patriId;

    private IntegerFilter weight0;

    private IntegerFilter weight200;

    private IntegerFilter weight365;

    private LongFilter journalEntriesId;

    private LongFilter sourceFileId;

    private LongFilter blupId;

    public BovineCriteria() {
    }

    public BovineCriteria(BovineCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.earTagId = other.earTagId == null ? null : other.earTagId.copy();
        this.masterIdentifier = other.masterIdentifier == null ? null : other.masterIdentifier.copy();
        this.country = other.country == null ? null : other.country.copy();
        this.herdId = other.herdId == null ? null : other.herdId.copy();
        this.birthDate = other.birthDate == null ? null : other.birthDate.copy();
        this.gender = other.gender == null ? null : other.gender.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.bovineStatus = other.bovineStatus == null ? null : other.bovineStatus.copy();
        this.hornStatus = other.hornStatus == null ? null : other.hornStatus.copy();
        this.matriId = other.matriId == null ? null : other.matriId.copy();
        this.patriId = other.patriId == null ? null : other.patriId.copy();
        this.weight0 = other.weight0 == null ? null : other.weight0.copy();
        this.weight200 = other.weight200 == null ? null : other.weight200.copy();
        this.weight365 = other.weight365 == null ? null : other.weight365.copy();
        this.journalEntriesId = other.journalEntriesId == null ? null : other.journalEntriesId.copy();
        this.sourceFileId = other.sourceFileId == null ? null : other.sourceFileId.copy();
        this.blupId = other.blupId == null ? null : other.blupId.copy();
    }

    @Override
    public BovineCriteria copy() {
        return new BovineCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getEarTagId() {
        return earTagId;
    }

    public void setEarTagId(IntegerFilter earTagId) {
        this.earTagId = earTagId;
    }

    public StringFilter getMasterIdentifier() {
        return masterIdentifier;
    }

    public void setMasterIdentifier(StringFilter masterIdentifier) {
        this.masterIdentifier = masterIdentifier;
    }

    public StringFilter getCountry() {
        return country;
    }

    public void setCountry(StringFilter country) {
        this.country = country;
    }

    public IntegerFilter getHerdId() {
        return herdId;
    }

    public void setHerdId(IntegerFilter herdId) {
        this.herdId = herdId;
    }

    public InstantFilter getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(InstantFilter birthDate) {
        this.birthDate = birthDate;
    }

    public GenderFilter getGender() {
        return gender;
    }

    public void setGender(GenderFilter gender) {
        this.gender = gender;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BovineStatusFilter getBovineStatus() {
        return bovineStatus;
    }

    public void setBovineStatus(BovineStatusFilter bovineStatus) {
        this.bovineStatus = bovineStatus;
    }

    public HornStatusFilter getHornStatus() {
        return hornStatus;
    }

    public void setHornStatus(HornStatusFilter hornStatus) {
        this.hornStatus = hornStatus;
    }

    public IntegerFilter getMatriId() {
        return matriId;
    }

    public void setMatriId(IntegerFilter matriId) {
        this.matriId = matriId;
    }

    public IntegerFilter getPatriId() {
        return patriId;
    }

    public void setPatriId(IntegerFilter patriId) {
        this.patriId = patriId;
    }

    public IntegerFilter getWeight0() {
        return weight0;
    }

    public void setWeight0(IntegerFilter weight0) {
        this.weight0 = weight0;
    }

    public IntegerFilter getWeight200() {
        return weight200;
    }

    public void setWeight200(IntegerFilter weight200) {
        this.weight200 = weight200;
    }

    public IntegerFilter getWeight365() {
        return weight365;
    }

    public void setWeight365(IntegerFilter weight365) {
        this.weight365 = weight365;
    }

    public LongFilter getJournalEntriesId() {
        return journalEntriesId;
    }

    public void setJournalEntriesId(LongFilter journalEntriesId) {
        this.journalEntriesId = journalEntriesId;
    }

    public LongFilter getSourceFileId() {
        return sourceFileId;
    }

    public void setSourceFileId(LongFilter sourceFileId) {
        this.sourceFileId = sourceFileId;
    }

    public LongFilter getBlupId() {
        return blupId;
    }

    public void setBlupId(LongFilter blupId) {
        this.blupId = blupId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BovineCriteria that = (BovineCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(earTagId, that.earTagId) &&
            Objects.equals(masterIdentifier, that.masterIdentifier) &&
            Objects.equals(country, that.country) &&
            Objects.equals(herdId, that.herdId) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(name, that.name) &&
            Objects.equals(bovineStatus, that.bovineStatus) &&
            Objects.equals(hornStatus, that.hornStatus) &&
            Objects.equals(matriId, that.matriId) &&
            Objects.equals(patriId, that.patriId) &&
            Objects.equals(weight0, that.weight0) &&
            Objects.equals(weight200, that.weight200) &&
            Objects.equals(weight365, that.weight365) &&
            Objects.equals(journalEntriesId, that.journalEntriesId) &&
            Objects.equals(sourceFileId, that.sourceFileId) &&
            Objects.equals(blupId, that.blupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        earTagId,
        masterIdentifier,
        country,
        herdId,
        birthDate,
        gender,
        name,
        bovineStatus,
        hornStatus,
        matriId,
        patriId,
        weight0,
        weight200,
        weight365,
        journalEntriesId,
        sourceFileId,
        blupId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BovineCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (earTagId != null ? "earTagId=" + earTagId + ", " : "") +
                (masterIdentifier != null ? "masterIdentifier=" + masterIdentifier + ", " : "") +
                (country != null ? "country=" + country + ", " : "") +
                (herdId != null ? "herdId=" + herdId + ", " : "") +
                (birthDate != null ? "birthDate=" + birthDate + ", " : "") +
                (gender != null ? "gender=" + gender + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (bovineStatus != null ? "bovineStatus=" + bovineStatus + ", " : "") +
                (hornStatus != null ? "hornStatus=" + hornStatus + ", " : "") +
                (matriId != null ? "matriId=" + matriId + ", " : "") +
                (patriId != null ? "patriId=" + patriId + ", " : "") +
                (weight0 != null ? "weight0=" + weight0 + ", " : "") +
                (weight200 != null ? "weight200=" + weight200 + ", " : "") +
                (weight365 != null ? "weight365=" + weight365 + ", " : "") +
                (journalEntriesId != null ? "journalEntriesId=" + journalEntriesId + ", " : "") +
                (sourceFileId != null ? "sourceFileId=" + sourceFileId + ", " : "") +
                (blupId != null ? "blupId=" + blupId + ", " : "") +
            "}";
    }

}

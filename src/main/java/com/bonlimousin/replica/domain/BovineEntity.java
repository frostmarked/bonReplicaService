package com.bonlimousin.replica.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.bonlimousin.replica.domain.enumeration.Gender;

import com.bonlimousin.replica.domain.enumeration.BovineStatus;

import com.bonlimousin.replica.domain.enumeration.HornStatus;

/**
 * A BovineEntity.
 */
@Entity
@Table(name = "bon_replica_bovine")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BovineEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "ear_tag_id", nullable = false, unique = true)
    private Integer earTagId;

    @NotNull
    @Size(max = 127)
    @Column(name = "master_identifier", length = 127, nullable = false, unique = true)
    private String masterIdentifier;

    @NotNull
    @Size(min = 2, max = 6)
    @Column(name = "country", length = 6, nullable = false)
    private String country;

    @NotNull
    @Min(value = 0)
    @Column(name = "herd_id", nullable = false)
    private Integer herdId;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private Instant birthDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "bovine_status", nullable = false)
    private BovineStatus bovineStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "horn_status", nullable = false)
    private HornStatus hornStatus;

    @NotNull
    @Column(name = "matri_id", nullable = false)
    private Integer matriId;

    @NotNull
    @Column(name = "patri_id", nullable = false)
    private Integer patriId;

    @Min(value = 0)
    @Max(value = 99)
    @Column(name = "weight_0")
    private Integer weight0;

    @Min(value = 0)
    @Max(value = 999)
    @Column(name = "weight_200")
    private Integer weight200;

    @Min(value = 0)
    @Max(value = 9999)
    @Column(name = "weight_365")
    private Integer weight365;

    @OneToMany(mappedBy = "bovine")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<JournalEntryEntity> journalEntries = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "bovines", allowSetters = true)
    private SourceFileEntity sourceFile;

    @OneToOne(mappedBy = "bovine")
    @JsonIgnore
    private BlupEntity blup;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEarTagId() {
        return earTagId;
    }

    public BovineEntity earTagId(Integer earTagId) {
        this.earTagId = earTagId;
        return this;
    }

    public void setEarTagId(Integer earTagId) {
        this.earTagId = earTagId;
    }

    public String getMasterIdentifier() {
        return masterIdentifier;
    }

    public BovineEntity masterIdentifier(String masterIdentifier) {
        this.masterIdentifier = masterIdentifier;
        return this;
    }

    public void setMasterIdentifier(String masterIdentifier) {
        this.masterIdentifier = masterIdentifier;
    }

    public String getCountry() {
        return country;
    }

    public BovineEntity country(String country) {
        this.country = country;
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getHerdId() {
        return herdId;
    }

    public BovineEntity herdId(Integer herdId) {
        this.herdId = herdId;
        return this;
    }

    public void setHerdId(Integer herdId) {
        this.herdId = herdId;
    }

    public Instant getBirthDate() {
        return birthDate;
    }

    public BovineEntity birthDate(Instant birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public BovineEntity gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public BovineEntity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BovineStatus getBovineStatus() {
        return bovineStatus;
    }

    public BovineEntity bovineStatus(BovineStatus bovineStatus) {
        this.bovineStatus = bovineStatus;
        return this;
    }

    public void setBovineStatus(BovineStatus bovineStatus) {
        this.bovineStatus = bovineStatus;
    }

    public HornStatus getHornStatus() {
        return hornStatus;
    }

    public BovineEntity hornStatus(HornStatus hornStatus) {
        this.hornStatus = hornStatus;
        return this;
    }

    public void setHornStatus(HornStatus hornStatus) {
        this.hornStatus = hornStatus;
    }

    public Integer getMatriId() {
        return matriId;
    }

    public BovineEntity matriId(Integer matriId) {
        this.matriId = matriId;
        return this;
    }

    public void setMatriId(Integer matriId) {
        this.matriId = matriId;
    }

    public Integer getPatriId() {
        return patriId;
    }

    public BovineEntity patriId(Integer patriId) {
        this.patriId = patriId;
        return this;
    }

    public void setPatriId(Integer patriId) {
        this.patriId = patriId;
    }

    public Integer getWeight0() {
        return weight0;
    }

    public BovineEntity weight0(Integer weight0) {
        this.weight0 = weight0;
        return this;
    }

    public void setWeight0(Integer weight0) {
        this.weight0 = weight0;
    }

    public Integer getWeight200() {
        return weight200;
    }

    public BovineEntity weight200(Integer weight200) {
        this.weight200 = weight200;
        return this;
    }

    public void setWeight200(Integer weight200) {
        this.weight200 = weight200;
    }

    public Integer getWeight365() {
        return weight365;
    }

    public BovineEntity weight365(Integer weight365) {
        this.weight365 = weight365;
        return this;
    }

    public void setWeight365(Integer weight365) {
        this.weight365 = weight365;
    }

    public Set<JournalEntryEntity> getJournalEntries() {
        return journalEntries;
    }

    public BovineEntity journalEntries(Set<JournalEntryEntity> journalEntries) {
        this.journalEntries = journalEntries;
        return this;
    }

    public BovineEntity addJournalEntries(JournalEntryEntity journalEntry) {
        this.journalEntries.add(journalEntry);
        journalEntry.setBovine(this);
        return this;
    }

    public BovineEntity removeJournalEntries(JournalEntryEntity journalEntry) {
        this.journalEntries.remove(journalEntry);
        journalEntry.setBovine(null);
        return this;
    }

    public void setJournalEntries(Set<JournalEntryEntity> journalEntries) {
        this.journalEntries = journalEntries;
    }

    public SourceFileEntity getSourceFile() {
        return sourceFile;
    }

    public BovineEntity sourceFile(SourceFileEntity sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public void setSourceFile(SourceFileEntity sourceFile) {
        this.sourceFile = sourceFile;
    }

    public BlupEntity getBlup() {
        return blup;
    }

    public BovineEntity blup(BlupEntity blup) {
        this.blup = blup;
        return this;
    }

    public void setBlup(BlupEntity blup) {
        this.blup = blup;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BovineEntity)) {
            return false;
        }
        return id != null && id.equals(((BovineEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BovineEntity{" +
            "id=" + getId() +
            ", earTagId=" + getEarTagId() +
            ", masterIdentifier='" + getMasterIdentifier() + "'" +
            ", country='" + getCountry() + "'" +
            ", herdId=" + getHerdId() +
            ", birthDate='" + getBirthDate() + "'" +
            ", gender='" + getGender() + "'" +
            ", name='" + getName() + "'" +
            ", bovineStatus='" + getBovineStatus() + "'" +
            ", hornStatus='" + getHornStatus() + "'" +
            ", matriId=" + getMatriId() +
            ", patriId=" + getPatriId() +
            ", weight0=" + getWeight0() +
            ", weight200=" + getWeight200() +
            ", weight365=" + getWeight365() +
            "}";
    }
}

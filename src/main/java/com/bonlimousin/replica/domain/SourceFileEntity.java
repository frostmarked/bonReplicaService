package com.bonlimousin.replica.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A SourceFileEntity.
 */
@Entity
@Table(name = "bon_replica_sourcefile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SourceFileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 127)
    @Column(name = "name", length = 127, nullable = false)
    private String name;

    @DiffIgnore
    @Lob
    @Column(name = "zip_file", nullable = false)
    private byte[] zipFile;

    @Column(name = "zip_file_content_type", nullable = false)
    private String zipFileContentType;

    @Column(name = "processed")
    private Instant processed;

    @Column(name = "outcome")
    private String outcome;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public SourceFileEntity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getZipFile() {
        return zipFile;
    }

    public SourceFileEntity zipFile(byte[] zipFile) {
        this.zipFile = zipFile;
        return this;
    }

    public void setZipFile(byte[] zipFile) {
        this.zipFile = zipFile;
    }

    public String getZipFileContentType() {
        return zipFileContentType;
    }

    public SourceFileEntity zipFileContentType(String zipFileContentType) {
        this.zipFileContentType = zipFileContentType;
        return this;
    }

    public void setZipFileContentType(String zipFileContentType) {
        this.zipFileContentType = zipFileContentType;
    }

    public Instant getProcessed() {
        return processed;
    }

    public SourceFileEntity processed(Instant processed) {
        this.processed = processed;
        return this;
    }

    public void setProcessed(Instant processed) {
        this.processed = processed;
    }

    public String getOutcome() {
        return outcome;
    }

    public SourceFileEntity outcome(String outcome) {
        this.outcome = outcome;
        return this;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SourceFileEntity)) {
            return false;
        }
        return id != null && id.equals(((SourceFileEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SourceFileEntity{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", zipFile='" + getZipFile() + "'" +
            ", zipFileContentType='" + getZipFileContentType() + "'" +
            ", processed='" + getProcessed() + "'" +
            ", outcome='" + getOutcome() + "'" +
            "}";
    }
}

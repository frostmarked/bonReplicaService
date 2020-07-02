package com.bonlimousin.replica.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A BlupEntity.
 */
@Entity
@Table(name = "bon_replica_blup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BlupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Min(value = 0)
    @Column(name = "t_0")
    private Integer t0;

    @Min(value = 0)
    @Column(name = "d_0")
    private Integer d0;

    @Min(value = 0)
    @Column(name = "m_0")
    private Integer m0;

    @Min(value = 0)
    @Column(name = "t_200")
    private Integer t200;

    @Min(value = 0)
    @Column(name = "d_200")
    private Integer d200;

    @Min(value = 0)
    @Column(name = "m_200")
    private Integer m200;

    @Min(value = 0)
    @Column(name = "t_365")
    private Integer t365;

    @Min(value = 0)
    @Column(name = "d_365")
    private Integer d365;

    @Min(value = 0)
    @Column(name = "total")
    private Integer total;

    @Size(min = 1)
    @Column(name = "status")
    private String status;

    @OneToOne(optional = false)
    @NotNull

    @MapsId
    @JoinColumn(name = "id")
    private BovineEntity bovine;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer gett0() {
        return t0;
    }

    public BlupEntity t0(Integer t0) {
        this.t0 = t0;
        return this;
    }

    public void sett0(Integer t0) {
        this.t0 = t0;
    }

    public Integer getd0() {
        return d0;
    }

    public BlupEntity d0(Integer d0) {
        this.d0 = d0;
        return this;
    }

    public void setd0(Integer d0) {
        this.d0 = d0;
    }

    public Integer getm0() {
        return m0;
    }

    public BlupEntity m0(Integer m0) {
        this.m0 = m0;
        return this;
    }

    public void setm0(Integer m0) {
        this.m0 = m0;
    }

    public Integer gett200() {
        return t200;
    }

    public BlupEntity t200(Integer t200) {
        this.t200 = t200;
        return this;
    }

    public void sett200(Integer t200) {
        this.t200 = t200;
    }

    public Integer getd200() {
        return d200;
    }

    public BlupEntity d200(Integer d200) {
        this.d200 = d200;
        return this;
    }

    public void setd200(Integer d200) {
        this.d200 = d200;
    }

    public Integer getm200() {
        return m200;
    }

    public BlupEntity m200(Integer m200) {
        this.m200 = m200;
        return this;
    }

    public void setm200(Integer m200) {
        this.m200 = m200;
    }

    public Integer gett365() {
        return t365;
    }

    public BlupEntity t365(Integer t365) {
        this.t365 = t365;
        return this;
    }

    public void sett365(Integer t365) {
        this.t365 = t365;
    }

    public Integer getd365() {
        return d365;
    }

    public BlupEntity d365(Integer d365) {
        this.d365 = d365;
        return this;
    }

    public void setd365(Integer d365) {
        this.d365 = d365;
    }

    public Integer getTotal() {
        return total;
    }

    public BlupEntity total(Integer total) {
        this.total = total;
        return this;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public BlupEntity status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BovineEntity getBovine() {
        return bovine;
    }

    public BlupEntity bovine(BovineEntity bovine) {
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
        if (!(o instanceof BlupEntity)) {
            return false;
        }
        return id != null && id.equals(((BlupEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlupEntity{" +
            "id=" + getId() +
            ", t0=" + gett0() +
            ", d0=" + getd0() +
            ", m0=" + getm0() +
            ", t200=" + gett200() +
            ", d200=" + getd200() +
            ", m200=" + getm200() +
            ", t365=" + gett365() +
            ", d365=" + getd365() +
            ", total=" + getTotal() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.JournalEntryEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.domain.BlupEntity;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import com.bonlimousin.replica.service.dto.BovineCriteria;
import com.bonlimousin.replica.service.BovineQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.HornStatus;
/**
 * Integration tests for the {@link BovineResource} REST controller.
 */
@SpringBootTest(classes = BonReplicaServiceApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class BovineResourceIT {

    private static final Integer DEFAULT_EAR_TAG_ID = 0;
    private static final Integer UPDATED_EAR_TAG_ID = 1;
    private static final Integer SMALLER_EAR_TAG_ID = 0 - 1;

    private static final String DEFAULT_MASTER_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_MASTER_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBB";

    private static final Integer DEFAULT_HERD_ID = 0;
    private static final Integer UPDATED_HERD_ID = 1;
    private static final Integer SMALLER_HERD_ID = 0 - 1;

    private static final Instant DEFAULT_BIRTH_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BIRTH_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Gender DEFAULT_GENDER = Gender.HEIFER;
    private static final Gender UPDATED_GENDER = Gender.BULL;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BovineStatus DEFAULT_BOVINE_STATUS = BovineStatus.UNKNOWN;
    private static final BovineStatus UPDATED_BOVINE_STATUS = BovineStatus.SOLD;

    private static final HornStatus DEFAULT_HORN_STATUS = HornStatus.UNKNOWN;
    private static final HornStatus UPDATED_HORN_STATUS = HornStatus.HORNED;

    private static final Integer DEFAULT_MATRI_ID = 1;
    private static final Integer UPDATED_MATRI_ID = 2;
    private static final Integer SMALLER_MATRI_ID = 1 - 1;

    private static final Integer DEFAULT_PATRI_ID = 1;
    private static final Integer UPDATED_PATRI_ID = 2;
    private static final Integer SMALLER_PATRI_ID = 1 - 1;

    private static final Integer DEFAULT_WEIGHT_0 = 0;
    private static final Integer UPDATED_WEIGHT_0 = 1;
    private static final Integer SMALLER_WEIGHT_0 = 0 - 1;

    private static final Integer DEFAULT_WEIGHT_200 = 0;
    private static final Integer UPDATED_WEIGHT_200 = 1;
    private static final Integer SMALLER_WEIGHT_200 = 0 - 1;

    private static final Integer DEFAULT_WEIGHT_365 = 0;
    private static final Integer UPDATED_WEIGHT_365 = 1;
    private static final Integer SMALLER_WEIGHT_365 = 0 - 1;

    @Autowired
    private BovineRepository bovineRepository;

    @Autowired
    private BovineService bovineService;

    @Autowired
    private BovineQueryService bovineQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBovineMockMvc;

    private BovineEntity bovineEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BovineEntity createEntity(EntityManager em) {
        BovineEntity bovineEntity = new BovineEntity()
            .earTagId(DEFAULT_EAR_TAG_ID)
            .masterIdentifier(DEFAULT_MASTER_IDENTIFIER)
            .country(DEFAULT_COUNTRY)
            .herdId(DEFAULT_HERD_ID)
            .birthDate(DEFAULT_BIRTH_DATE)
            .gender(DEFAULT_GENDER)
            .name(DEFAULT_NAME)
            .bovineStatus(DEFAULT_BOVINE_STATUS)
            .hornStatus(DEFAULT_HORN_STATUS)
            .matriId(DEFAULT_MATRI_ID)
            .patriId(DEFAULT_PATRI_ID)
            .weight0(DEFAULT_WEIGHT_0)
            .weight200(DEFAULT_WEIGHT_200)
            .weight365(DEFAULT_WEIGHT_365);
        return bovineEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BovineEntity createUpdatedEntity(EntityManager em) {
        BovineEntity bovineEntity = new BovineEntity()
            .earTagId(UPDATED_EAR_TAG_ID)
            .masterIdentifier(UPDATED_MASTER_IDENTIFIER)
            .country(UPDATED_COUNTRY)
            .herdId(UPDATED_HERD_ID)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .name(UPDATED_NAME)
            .bovineStatus(UPDATED_BOVINE_STATUS)
            .hornStatus(UPDATED_HORN_STATUS)
            .matriId(UPDATED_MATRI_ID)
            .patriId(UPDATED_PATRI_ID)
            .weight0(UPDATED_WEIGHT_0)
            .weight200(UPDATED_WEIGHT_200)
            .weight365(UPDATED_WEIGHT_365);
        return bovineEntity;
    }

    @BeforeEach
    public void initTest() {
        bovineEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void getAllBovines() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList
        restBovineMockMvc.perform(get("/api/bovines?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bovineEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].earTagId").value(hasItem(DEFAULT_EAR_TAG_ID)))
            .andExpect(jsonPath("$.[*].masterIdentifier").value(hasItem(DEFAULT_MASTER_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].herdId").value(hasItem(DEFAULT_HERD_ID)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].bovineStatus").value(hasItem(DEFAULT_BOVINE_STATUS.toString())))
            .andExpect(jsonPath("$.[*].hornStatus").value(hasItem(DEFAULT_HORN_STATUS.toString())))
            .andExpect(jsonPath("$.[*].matriId").value(hasItem(DEFAULT_MATRI_ID)))
            .andExpect(jsonPath("$.[*].patriId").value(hasItem(DEFAULT_PATRI_ID)))
            .andExpect(jsonPath("$.[*].weight0").value(hasItem(DEFAULT_WEIGHT_0)))
            .andExpect(jsonPath("$.[*].weight200").value(hasItem(DEFAULT_WEIGHT_200)))
            .andExpect(jsonPath("$.[*].weight365").value(hasItem(DEFAULT_WEIGHT_365)));
    }
    
    @Test
    @Transactional
    public void getBovine() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get the bovine
        restBovineMockMvc.perform(get("/api/bovines/{id}", bovineEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bovineEntity.getId().intValue()))
            .andExpect(jsonPath("$.earTagId").value(DEFAULT_EAR_TAG_ID))
            .andExpect(jsonPath("$.masterIdentifier").value(DEFAULT_MASTER_IDENTIFIER))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.herdId").value(DEFAULT_HERD_ID))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.bovineStatus").value(DEFAULT_BOVINE_STATUS.toString()))
            .andExpect(jsonPath("$.hornStatus").value(DEFAULT_HORN_STATUS.toString()))
            .andExpect(jsonPath("$.matriId").value(DEFAULT_MATRI_ID))
            .andExpect(jsonPath("$.patriId").value(DEFAULT_PATRI_ID))
            .andExpect(jsonPath("$.weight0").value(DEFAULT_WEIGHT_0))
            .andExpect(jsonPath("$.weight200").value(DEFAULT_WEIGHT_200))
            .andExpect(jsonPath("$.weight365").value(DEFAULT_WEIGHT_365));
    }


    @Test
    @Transactional
    public void getBovinesByIdFiltering() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        Long id = bovineEntity.getId();

        defaultBovineShouldBeFound("id.equals=" + id);
        defaultBovineShouldNotBeFound("id.notEquals=" + id);

        defaultBovineShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBovineShouldNotBeFound("id.greaterThan=" + id);

        defaultBovineShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBovineShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBovinesByEarTagIdIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where earTagId equals to DEFAULT_EAR_TAG_ID
        defaultBovineShouldBeFound("earTagId.equals=" + DEFAULT_EAR_TAG_ID);

        // Get all the bovineList where earTagId equals to UPDATED_EAR_TAG_ID
        defaultBovineShouldNotBeFound("earTagId.equals=" + UPDATED_EAR_TAG_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByEarTagIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where earTagId not equals to DEFAULT_EAR_TAG_ID
        defaultBovineShouldNotBeFound("earTagId.notEquals=" + DEFAULT_EAR_TAG_ID);

        // Get all the bovineList where earTagId not equals to UPDATED_EAR_TAG_ID
        defaultBovineShouldBeFound("earTagId.notEquals=" + UPDATED_EAR_TAG_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByEarTagIdIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where earTagId in DEFAULT_EAR_TAG_ID or UPDATED_EAR_TAG_ID
        defaultBovineShouldBeFound("earTagId.in=" + DEFAULT_EAR_TAG_ID + "," + UPDATED_EAR_TAG_ID);

        // Get all the bovineList where earTagId equals to UPDATED_EAR_TAG_ID
        defaultBovineShouldNotBeFound("earTagId.in=" + UPDATED_EAR_TAG_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByEarTagIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where earTagId is not null
        defaultBovineShouldBeFound("earTagId.specified=true");

        // Get all the bovineList where earTagId is null
        defaultBovineShouldNotBeFound("earTagId.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByEarTagIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where earTagId is greater than or equal to DEFAULT_EAR_TAG_ID
        defaultBovineShouldBeFound("earTagId.greaterThanOrEqual=" + DEFAULT_EAR_TAG_ID);

        // Get all the bovineList where earTagId is greater than or equal to UPDATED_EAR_TAG_ID
        defaultBovineShouldNotBeFound("earTagId.greaterThanOrEqual=" + UPDATED_EAR_TAG_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByEarTagIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where earTagId is less than or equal to DEFAULT_EAR_TAG_ID
        defaultBovineShouldBeFound("earTagId.lessThanOrEqual=" + DEFAULT_EAR_TAG_ID);

        // Get all the bovineList where earTagId is less than or equal to SMALLER_EAR_TAG_ID
        defaultBovineShouldNotBeFound("earTagId.lessThanOrEqual=" + SMALLER_EAR_TAG_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByEarTagIdIsLessThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where earTagId is less than DEFAULT_EAR_TAG_ID
        defaultBovineShouldNotBeFound("earTagId.lessThan=" + DEFAULT_EAR_TAG_ID);

        // Get all the bovineList where earTagId is less than UPDATED_EAR_TAG_ID
        defaultBovineShouldBeFound("earTagId.lessThan=" + UPDATED_EAR_TAG_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByEarTagIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where earTagId is greater than DEFAULT_EAR_TAG_ID
        defaultBovineShouldNotBeFound("earTagId.greaterThan=" + DEFAULT_EAR_TAG_ID);

        // Get all the bovineList where earTagId is greater than SMALLER_EAR_TAG_ID
        defaultBovineShouldBeFound("earTagId.greaterThan=" + SMALLER_EAR_TAG_ID);
    }


    @Test
    @Transactional
    public void getAllBovinesByMasterIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where masterIdentifier equals to DEFAULT_MASTER_IDENTIFIER
        defaultBovineShouldBeFound("masterIdentifier.equals=" + DEFAULT_MASTER_IDENTIFIER);

        // Get all the bovineList where masterIdentifier equals to UPDATED_MASTER_IDENTIFIER
        defaultBovineShouldNotBeFound("masterIdentifier.equals=" + UPDATED_MASTER_IDENTIFIER);
    }

    @Test
    @Transactional
    public void getAllBovinesByMasterIdentifierIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where masterIdentifier not equals to DEFAULT_MASTER_IDENTIFIER
        defaultBovineShouldNotBeFound("masterIdentifier.notEquals=" + DEFAULT_MASTER_IDENTIFIER);

        // Get all the bovineList where masterIdentifier not equals to UPDATED_MASTER_IDENTIFIER
        defaultBovineShouldBeFound("masterIdentifier.notEquals=" + UPDATED_MASTER_IDENTIFIER);
    }

    @Test
    @Transactional
    public void getAllBovinesByMasterIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where masterIdentifier in DEFAULT_MASTER_IDENTIFIER or UPDATED_MASTER_IDENTIFIER
        defaultBovineShouldBeFound("masterIdentifier.in=" + DEFAULT_MASTER_IDENTIFIER + "," + UPDATED_MASTER_IDENTIFIER);

        // Get all the bovineList where masterIdentifier equals to UPDATED_MASTER_IDENTIFIER
        defaultBovineShouldNotBeFound("masterIdentifier.in=" + UPDATED_MASTER_IDENTIFIER);
    }

    @Test
    @Transactional
    public void getAllBovinesByMasterIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where masterIdentifier is not null
        defaultBovineShouldBeFound("masterIdentifier.specified=true");

        // Get all the bovineList where masterIdentifier is null
        defaultBovineShouldNotBeFound("masterIdentifier.specified=false");
    }
                @Test
    @Transactional
    public void getAllBovinesByMasterIdentifierContainsSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where masterIdentifier contains DEFAULT_MASTER_IDENTIFIER
        defaultBovineShouldBeFound("masterIdentifier.contains=" + DEFAULT_MASTER_IDENTIFIER);

        // Get all the bovineList where masterIdentifier contains UPDATED_MASTER_IDENTIFIER
        defaultBovineShouldNotBeFound("masterIdentifier.contains=" + UPDATED_MASTER_IDENTIFIER);
    }

    @Test
    @Transactional
    public void getAllBovinesByMasterIdentifierNotContainsSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where masterIdentifier does not contain DEFAULT_MASTER_IDENTIFIER
        defaultBovineShouldNotBeFound("masterIdentifier.doesNotContain=" + DEFAULT_MASTER_IDENTIFIER);

        // Get all the bovineList where masterIdentifier does not contain UPDATED_MASTER_IDENTIFIER
        defaultBovineShouldBeFound("masterIdentifier.doesNotContain=" + UPDATED_MASTER_IDENTIFIER);
    }


    @Test
    @Transactional
    public void getAllBovinesByCountryIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where country equals to DEFAULT_COUNTRY
        defaultBovineShouldBeFound("country.equals=" + DEFAULT_COUNTRY);

        // Get all the bovineList where country equals to UPDATED_COUNTRY
        defaultBovineShouldNotBeFound("country.equals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllBovinesByCountryIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where country not equals to DEFAULT_COUNTRY
        defaultBovineShouldNotBeFound("country.notEquals=" + DEFAULT_COUNTRY);

        // Get all the bovineList where country not equals to UPDATED_COUNTRY
        defaultBovineShouldBeFound("country.notEquals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllBovinesByCountryIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where country in DEFAULT_COUNTRY or UPDATED_COUNTRY
        defaultBovineShouldBeFound("country.in=" + DEFAULT_COUNTRY + "," + UPDATED_COUNTRY);

        // Get all the bovineList where country equals to UPDATED_COUNTRY
        defaultBovineShouldNotBeFound("country.in=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllBovinesByCountryIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where country is not null
        defaultBovineShouldBeFound("country.specified=true");

        // Get all the bovineList where country is null
        defaultBovineShouldNotBeFound("country.specified=false");
    }
                @Test
    @Transactional
    public void getAllBovinesByCountryContainsSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where country contains DEFAULT_COUNTRY
        defaultBovineShouldBeFound("country.contains=" + DEFAULT_COUNTRY);

        // Get all the bovineList where country contains UPDATED_COUNTRY
        defaultBovineShouldNotBeFound("country.contains=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllBovinesByCountryNotContainsSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where country does not contain DEFAULT_COUNTRY
        defaultBovineShouldNotBeFound("country.doesNotContain=" + DEFAULT_COUNTRY);

        // Get all the bovineList where country does not contain UPDATED_COUNTRY
        defaultBovineShouldBeFound("country.doesNotContain=" + UPDATED_COUNTRY);
    }


    @Test
    @Transactional
    public void getAllBovinesByHerdIdIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where herdId equals to DEFAULT_HERD_ID
        defaultBovineShouldBeFound("herdId.equals=" + DEFAULT_HERD_ID);

        // Get all the bovineList where herdId equals to UPDATED_HERD_ID
        defaultBovineShouldNotBeFound("herdId.equals=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByHerdIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where herdId not equals to DEFAULT_HERD_ID
        defaultBovineShouldNotBeFound("herdId.notEquals=" + DEFAULT_HERD_ID);

        // Get all the bovineList where herdId not equals to UPDATED_HERD_ID
        defaultBovineShouldBeFound("herdId.notEquals=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByHerdIdIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where herdId in DEFAULT_HERD_ID or UPDATED_HERD_ID
        defaultBovineShouldBeFound("herdId.in=" + DEFAULT_HERD_ID + "," + UPDATED_HERD_ID);

        // Get all the bovineList where herdId equals to UPDATED_HERD_ID
        defaultBovineShouldNotBeFound("herdId.in=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByHerdIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where herdId is not null
        defaultBovineShouldBeFound("herdId.specified=true");

        // Get all the bovineList where herdId is null
        defaultBovineShouldNotBeFound("herdId.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByHerdIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where herdId is greater than or equal to DEFAULT_HERD_ID
        defaultBovineShouldBeFound("herdId.greaterThanOrEqual=" + DEFAULT_HERD_ID);

        // Get all the bovineList where herdId is greater than or equal to UPDATED_HERD_ID
        defaultBovineShouldNotBeFound("herdId.greaterThanOrEqual=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByHerdIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where herdId is less than or equal to DEFAULT_HERD_ID
        defaultBovineShouldBeFound("herdId.lessThanOrEqual=" + DEFAULT_HERD_ID);

        // Get all the bovineList where herdId is less than or equal to SMALLER_HERD_ID
        defaultBovineShouldNotBeFound("herdId.lessThanOrEqual=" + SMALLER_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByHerdIdIsLessThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where herdId is less than DEFAULT_HERD_ID
        defaultBovineShouldNotBeFound("herdId.lessThan=" + DEFAULT_HERD_ID);

        // Get all the bovineList where herdId is less than UPDATED_HERD_ID
        defaultBovineShouldBeFound("herdId.lessThan=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByHerdIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where herdId is greater than DEFAULT_HERD_ID
        defaultBovineShouldNotBeFound("herdId.greaterThan=" + DEFAULT_HERD_ID);

        // Get all the bovineList where herdId is greater than SMALLER_HERD_ID
        defaultBovineShouldBeFound("herdId.greaterThan=" + SMALLER_HERD_ID);
    }


    @Test
    @Transactional
    public void getAllBovinesByBirthDateIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where birthDate equals to DEFAULT_BIRTH_DATE
        defaultBovineShouldBeFound("birthDate.equals=" + DEFAULT_BIRTH_DATE);

        // Get all the bovineList where birthDate equals to UPDATED_BIRTH_DATE
        defaultBovineShouldNotBeFound("birthDate.equals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    public void getAllBovinesByBirthDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where birthDate not equals to DEFAULT_BIRTH_DATE
        defaultBovineShouldNotBeFound("birthDate.notEquals=" + DEFAULT_BIRTH_DATE);

        // Get all the bovineList where birthDate not equals to UPDATED_BIRTH_DATE
        defaultBovineShouldBeFound("birthDate.notEquals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    public void getAllBovinesByBirthDateIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where birthDate in DEFAULT_BIRTH_DATE or UPDATED_BIRTH_DATE
        defaultBovineShouldBeFound("birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE);

        // Get all the bovineList where birthDate equals to UPDATED_BIRTH_DATE
        defaultBovineShouldNotBeFound("birthDate.in=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    public void getAllBovinesByBirthDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where birthDate is not null
        defaultBovineShouldBeFound("birthDate.specified=true");

        // Get all the bovineList where birthDate is null
        defaultBovineShouldNotBeFound("birthDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where gender equals to DEFAULT_GENDER
        defaultBovineShouldBeFound("gender.equals=" + DEFAULT_GENDER);

        // Get all the bovineList where gender equals to UPDATED_GENDER
        defaultBovineShouldNotBeFound("gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    public void getAllBovinesByGenderIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where gender not equals to DEFAULT_GENDER
        defaultBovineShouldNotBeFound("gender.notEquals=" + DEFAULT_GENDER);

        // Get all the bovineList where gender not equals to UPDATED_GENDER
        defaultBovineShouldBeFound("gender.notEquals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    public void getAllBovinesByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where gender in DEFAULT_GENDER or UPDATED_GENDER
        defaultBovineShouldBeFound("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER);

        // Get all the bovineList where gender equals to UPDATED_GENDER
        defaultBovineShouldNotBeFound("gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    public void getAllBovinesByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where gender is not null
        defaultBovineShouldBeFound("gender.specified=true");

        // Get all the bovineList where gender is null
        defaultBovineShouldNotBeFound("gender.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where name equals to DEFAULT_NAME
        defaultBovineShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the bovineList where name equals to UPDATED_NAME
        defaultBovineShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBovinesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where name not equals to DEFAULT_NAME
        defaultBovineShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the bovineList where name not equals to UPDATED_NAME
        defaultBovineShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBovinesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBovineShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the bovineList where name equals to UPDATED_NAME
        defaultBovineShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBovinesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where name is not null
        defaultBovineShouldBeFound("name.specified=true");

        // Get all the bovineList where name is null
        defaultBovineShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllBovinesByNameContainsSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where name contains DEFAULT_NAME
        defaultBovineShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the bovineList where name contains UPDATED_NAME
        defaultBovineShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBovinesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where name does not contain DEFAULT_NAME
        defaultBovineShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the bovineList where name does not contain UPDATED_NAME
        defaultBovineShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllBovinesByBovineStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where bovineStatus equals to DEFAULT_BOVINE_STATUS
        defaultBovineShouldBeFound("bovineStatus.equals=" + DEFAULT_BOVINE_STATUS);

        // Get all the bovineList where bovineStatus equals to UPDATED_BOVINE_STATUS
        defaultBovineShouldNotBeFound("bovineStatus.equals=" + UPDATED_BOVINE_STATUS);
    }

    @Test
    @Transactional
    public void getAllBovinesByBovineStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where bovineStatus not equals to DEFAULT_BOVINE_STATUS
        defaultBovineShouldNotBeFound("bovineStatus.notEquals=" + DEFAULT_BOVINE_STATUS);

        // Get all the bovineList where bovineStatus not equals to UPDATED_BOVINE_STATUS
        defaultBovineShouldBeFound("bovineStatus.notEquals=" + UPDATED_BOVINE_STATUS);
    }

    @Test
    @Transactional
    public void getAllBovinesByBovineStatusIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where bovineStatus in DEFAULT_BOVINE_STATUS or UPDATED_BOVINE_STATUS
        defaultBovineShouldBeFound("bovineStatus.in=" + DEFAULT_BOVINE_STATUS + "," + UPDATED_BOVINE_STATUS);

        // Get all the bovineList where bovineStatus equals to UPDATED_BOVINE_STATUS
        defaultBovineShouldNotBeFound("bovineStatus.in=" + UPDATED_BOVINE_STATUS);
    }

    @Test
    @Transactional
    public void getAllBovinesByBovineStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where bovineStatus is not null
        defaultBovineShouldBeFound("bovineStatus.specified=true");

        // Get all the bovineList where bovineStatus is null
        defaultBovineShouldNotBeFound("bovineStatus.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByHornStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where hornStatus equals to DEFAULT_HORN_STATUS
        defaultBovineShouldBeFound("hornStatus.equals=" + DEFAULT_HORN_STATUS);

        // Get all the bovineList where hornStatus equals to UPDATED_HORN_STATUS
        defaultBovineShouldNotBeFound("hornStatus.equals=" + UPDATED_HORN_STATUS);
    }

    @Test
    @Transactional
    public void getAllBovinesByHornStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where hornStatus not equals to DEFAULT_HORN_STATUS
        defaultBovineShouldNotBeFound("hornStatus.notEquals=" + DEFAULT_HORN_STATUS);

        // Get all the bovineList where hornStatus not equals to UPDATED_HORN_STATUS
        defaultBovineShouldBeFound("hornStatus.notEquals=" + UPDATED_HORN_STATUS);
    }

    @Test
    @Transactional
    public void getAllBovinesByHornStatusIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where hornStatus in DEFAULT_HORN_STATUS or UPDATED_HORN_STATUS
        defaultBovineShouldBeFound("hornStatus.in=" + DEFAULT_HORN_STATUS + "," + UPDATED_HORN_STATUS);

        // Get all the bovineList where hornStatus equals to UPDATED_HORN_STATUS
        defaultBovineShouldNotBeFound("hornStatus.in=" + UPDATED_HORN_STATUS);
    }

    @Test
    @Transactional
    public void getAllBovinesByHornStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where hornStatus is not null
        defaultBovineShouldBeFound("hornStatus.specified=true");

        // Get all the bovineList where hornStatus is null
        defaultBovineShouldNotBeFound("hornStatus.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByMatriIdIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where matriId equals to DEFAULT_MATRI_ID
        defaultBovineShouldBeFound("matriId.equals=" + DEFAULT_MATRI_ID);

        // Get all the bovineList where matriId equals to UPDATED_MATRI_ID
        defaultBovineShouldNotBeFound("matriId.equals=" + UPDATED_MATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByMatriIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where matriId not equals to DEFAULT_MATRI_ID
        defaultBovineShouldNotBeFound("matriId.notEquals=" + DEFAULT_MATRI_ID);

        // Get all the bovineList where matriId not equals to UPDATED_MATRI_ID
        defaultBovineShouldBeFound("matriId.notEquals=" + UPDATED_MATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByMatriIdIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where matriId in DEFAULT_MATRI_ID or UPDATED_MATRI_ID
        defaultBovineShouldBeFound("matriId.in=" + DEFAULT_MATRI_ID + "," + UPDATED_MATRI_ID);

        // Get all the bovineList where matriId equals to UPDATED_MATRI_ID
        defaultBovineShouldNotBeFound("matriId.in=" + UPDATED_MATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByMatriIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where matriId is not null
        defaultBovineShouldBeFound("matriId.specified=true");

        // Get all the bovineList where matriId is null
        defaultBovineShouldNotBeFound("matriId.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByMatriIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where matriId is greater than or equal to DEFAULT_MATRI_ID
        defaultBovineShouldBeFound("matriId.greaterThanOrEqual=" + DEFAULT_MATRI_ID);

        // Get all the bovineList where matriId is greater than or equal to UPDATED_MATRI_ID
        defaultBovineShouldNotBeFound("matriId.greaterThanOrEqual=" + UPDATED_MATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByMatriIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where matriId is less than or equal to DEFAULT_MATRI_ID
        defaultBovineShouldBeFound("matriId.lessThanOrEqual=" + DEFAULT_MATRI_ID);

        // Get all the bovineList where matriId is less than or equal to SMALLER_MATRI_ID
        defaultBovineShouldNotBeFound("matriId.lessThanOrEqual=" + SMALLER_MATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByMatriIdIsLessThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where matriId is less than DEFAULT_MATRI_ID
        defaultBovineShouldNotBeFound("matriId.lessThan=" + DEFAULT_MATRI_ID);

        // Get all the bovineList where matriId is less than UPDATED_MATRI_ID
        defaultBovineShouldBeFound("matriId.lessThan=" + UPDATED_MATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByMatriIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where matriId is greater than DEFAULT_MATRI_ID
        defaultBovineShouldNotBeFound("matriId.greaterThan=" + DEFAULT_MATRI_ID);

        // Get all the bovineList where matriId is greater than SMALLER_MATRI_ID
        defaultBovineShouldBeFound("matriId.greaterThan=" + SMALLER_MATRI_ID);
    }


    @Test
    @Transactional
    public void getAllBovinesByPatriIdIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where patriId equals to DEFAULT_PATRI_ID
        defaultBovineShouldBeFound("patriId.equals=" + DEFAULT_PATRI_ID);

        // Get all the bovineList where patriId equals to UPDATED_PATRI_ID
        defaultBovineShouldNotBeFound("patriId.equals=" + UPDATED_PATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByPatriIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where patriId not equals to DEFAULT_PATRI_ID
        defaultBovineShouldNotBeFound("patriId.notEquals=" + DEFAULT_PATRI_ID);

        // Get all the bovineList where patriId not equals to UPDATED_PATRI_ID
        defaultBovineShouldBeFound("patriId.notEquals=" + UPDATED_PATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByPatriIdIsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where patriId in DEFAULT_PATRI_ID or UPDATED_PATRI_ID
        defaultBovineShouldBeFound("patriId.in=" + DEFAULT_PATRI_ID + "," + UPDATED_PATRI_ID);

        // Get all the bovineList where patriId equals to UPDATED_PATRI_ID
        defaultBovineShouldNotBeFound("patriId.in=" + UPDATED_PATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByPatriIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where patriId is not null
        defaultBovineShouldBeFound("patriId.specified=true");

        // Get all the bovineList where patriId is null
        defaultBovineShouldNotBeFound("patriId.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByPatriIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where patriId is greater than or equal to DEFAULT_PATRI_ID
        defaultBovineShouldBeFound("patriId.greaterThanOrEqual=" + DEFAULT_PATRI_ID);

        // Get all the bovineList where patriId is greater than or equal to UPDATED_PATRI_ID
        defaultBovineShouldNotBeFound("patriId.greaterThanOrEqual=" + UPDATED_PATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByPatriIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where patriId is less than or equal to DEFAULT_PATRI_ID
        defaultBovineShouldBeFound("patriId.lessThanOrEqual=" + DEFAULT_PATRI_ID);

        // Get all the bovineList where patriId is less than or equal to SMALLER_PATRI_ID
        defaultBovineShouldNotBeFound("patriId.lessThanOrEqual=" + SMALLER_PATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByPatriIdIsLessThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where patriId is less than DEFAULT_PATRI_ID
        defaultBovineShouldNotBeFound("patriId.lessThan=" + DEFAULT_PATRI_ID);

        // Get all the bovineList where patriId is less than UPDATED_PATRI_ID
        defaultBovineShouldBeFound("patriId.lessThan=" + UPDATED_PATRI_ID);
    }

    @Test
    @Transactional
    public void getAllBovinesByPatriIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where patriId is greater than DEFAULT_PATRI_ID
        defaultBovineShouldNotBeFound("patriId.greaterThan=" + DEFAULT_PATRI_ID);

        // Get all the bovineList where patriId is greater than SMALLER_PATRI_ID
        defaultBovineShouldBeFound("patriId.greaterThan=" + SMALLER_PATRI_ID);
    }


    @Test
    @Transactional
    public void getAllBovinesByWeight0IsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight0 equals to DEFAULT_WEIGHT_0
        defaultBovineShouldBeFound("weight0.equals=" + DEFAULT_WEIGHT_0);

        // Get all the bovineList where weight0 equals to UPDATED_WEIGHT_0
        defaultBovineShouldNotBeFound("weight0.equals=" + UPDATED_WEIGHT_0);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight0IsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight0 not equals to DEFAULT_WEIGHT_0
        defaultBovineShouldNotBeFound("weight0.notEquals=" + DEFAULT_WEIGHT_0);

        // Get all the bovineList where weight0 not equals to UPDATED_WEIGHT_0
        defaultBovineShouldBeFound("weight0.notEquals=" + UPDATED_WEIGHT_0);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight0IsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight0 in DEFAULT_WEIGHT_0 or UPDATED_WEIGHT_0
        defaultBovineShouldBeFound("weight0.in=" + DEFAULT_WEIGHT_0 + "," + UPDATED_WEIGHT_0);

        // Get all the bovineList where weight0 equals to UPDATED_WEIGHT_0
        defaultBovineShouldNotBeFound("weight0.in=" + UPDATED_WEIGHT_0);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight0IsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight0 is not null
        defaultBovineShouldBeFound("weight0.specified=true");

        // Get all the bovineList where weight0 is null
        defaultBovineShouldNotBeFound("weight0.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight0IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight0 is greater than or equal to DEFAULT_WEIGHT_0
        defaultBovineShouldBeFound("weight0.greaterThanOrEqual=" + DEFAULT_WEIGHT_0);

        // Get all the bovineList where weight0 is greater than or equal to (DEFAULT_WEIGHT_0 + 1)
        defaultBovineShouldNotBeFound("weight0.greaterThanOrEqual=" + (DEFAULT_WEIGHT_0 + 1));
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight0IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight0 is less than or equal to DEFAULT_WEIGHT_0
        defaultBovineShouldBeFound("weight0.lessThanOrEqual=" + DEFAULT_WEIGHT_0);

        // Get all the bovineList where weight0 is less than or equal to SMALLER_WEIGHT_0
        defaultBovineShouldNotBeFound("weight0.lessThanOrEqual=" + SMALLER_WEIGHT_0);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight0IsLessThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight0 is less than DEFAULT_WEIGHT_0
        defaultBovineShouldNotBeFound("weight0.lessThan=" + DEFAULT_WEIGHT_0);

        // Get all the bovineList where weight0 is less than (DEFAULT_WEIGHT_0 + 1)
        defaultBovineShouldBeFound("weight0.lessThan=" + (DEFAULT_WEIGHT_0 + 1));
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight0IsGreaterThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight0 is greater than DEFAULT_WEIGHT_0
        defaultBovineShouldNotBeFound("weight0.greaterThan=" + DEFAULT_WEIGHT_0);

        // Get all the bovineList where weight0 is greater than SMALLER_WEIGHT_0
        defaultBovineShouldBeFound("weight0.greaterThan=" + SMALLER_WEIGHT_0);
    }


    @Test
    @Transactional
    public void getAllBovinesByWeight200IsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight200 equals to DEFAULT_WEIGHT_200
        defaultBovineShouldBeFound("weight200.equals=" + DEFAULT_WEIGHT_200);

        // Get all the bovineList where weight200 equals to UPDATED_WEIGHT_200
        defaultBovineShouldNotBeFound("weight200.equals=" + UPDATED_WEIGHT_200);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight200IsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight200 not equals to DEFAULT_WEIGHT_200
        defaultBovineShouldNotBeFound("weight200.notEquals=" + DEFAULT_WEIGHT_200);

        // Get all the bovineList where weight200 not equals to UPDATED_WEIGHT_200
        defaultBovineShouldBeFound("weight200.notEquals=" + UPDATED_WEIGHT_200);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight200IsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight200 in DEFAULT_WEIGHT_200 or UPDATED_WEIGHT_200
        defaultBovineShouldBeFound("weight200.in=" + DEFAULT_WEIGHT_200 + "," + UPDATED_WEIGHT_200);

        // Get all the bovineList where weight200 equals to UPDATED_WEIGHT_200
        defaultBovineShouldNotBeFound("weight200.in=" + UPDATED_WEIGHT_200);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight200IsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight200 is not null
        defaultBovineShouldBeFound("weight200.specified=true");

        // Get all the bovineList where weight200 is null
        defaultBovineShouldNotBeFound("weight200.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight200IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight200 is greater than or equal to DEFAULT_WEIGHT_200
        defaultBovineShouldBeFound("weight200.greaterThanOrEqual=" + DEFAULT_WEIGHT_200);

        // Get all the bovineList where weight200 is greater than or equal to (DEFAULT_WEIGHT_200 + 1)
        defaultBovineShouldNotBeFound("weight200.greaterThanOrEqual=" + (DEFAULT_WEIGHT_200 + 1));
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight200IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight200 is less than or equal to DEFAULT_WEIGHT_200
        defaultBovineShouldBeFound("weight200.lessThanOrEqual=" + DEFAULT_WEIGHT_200);

        // Get all the bovineList where weight200 is less than or equal to SMALLER_WEIGHT_200
        defaultBovineShouldNotBeFound("weight200.lessThanOrEqual=" + SMALLER_WEIGHT_200);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight200IsLessThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight200 is less than DEFAULT_WEIGHT_200
        defaultBovineShouldNotBeFound("weight200.lessThan=" + DEFAULT_WEIGHT_200);

        // Get all the bovineList where weight200 is less than (DEFAULT_WEIGHT_200 + 1)
        defaultBovineShouldBeFound("weight200.lessThan=" + (DEFAULT_WEIGHT_200 + 1));
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight200IsGreaterThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight200 is greater than DEFAULT_WEIGHT_200
        defaultBovineShouldNotBeFound("weight200.greaterThan=" + DEFAULT_WEIGHT_200);

        // Get all the bovineList where weight200 is greater than SMALLER_WEIGHT_200
        defaultBovineShouldBeFound("weight200.greaterThan=" + SMALLER_WEIGHT_200);
    }


    @Test
    @Transactional
    public void getAllBovinesByWeight365IsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight365 equals to DEFAULT_WEIGHT_365
        defaultBovineShouldBeFound("weight365.equals=" + DEFAULT_WEIGHT_365);

        // Get all the bovineList where weight365 equals to UPDATED_WEIGHT_365
        defaultBovineShouldNotBeFound("weight365.equals=" + UPDATED_WEIGHT_365);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight365IsNotEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight365 not equals to DEFAULT_WEIGHT_365
        defaultBovineShouldNotBeFound("weight365.notEquals=" + DEFAULT_WEIGHT_365);

        // Get all the bovineList where weight365 not equals to UPDATED_WEIGHT_365
        defaultBovineShouldBeFound("weight365.notEquals=" + UPDATED_WEIGHT_365);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight365IsInShouldWork() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight365 in DEFAULT_WEIGHT_365 or UPDATED_WEIGHT_365
        defaultBovineShouldBeFound("weight365.in=" + DEFAULT_WEIGHT_365 + "," + UPDATED_WEIGHT_365);

        // Get all the bovineList where weight365 equals to UPDATED_WEIGHT_365
        defaultBovineShouldNotBeFound("weight365.in=" + UPDATED_WEIGHT_365);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight365IsNullOrNotNull() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight365 is not null
        defaultBovineShouldBeFound("weight365.specified=true");

        // Get all the bovineList where weight365 is null
        defaultBovineShouldNotBeFound("weight365.specified=false");
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight365IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight365 is greater than or equal to DEFAULT_WEIGHT_365
        defaultBovineShouldBeFound("weight365.greaterThanOrEqual=" + DEFAULT_WEIGHT_365);

        // Get all the bovineList where weight365 is greater than or equal to (DEFAULT_WEIGHT_365 + 1)
        defaultBovineShouldNotBeFound("weight365.greaterThanOrEqual=" + (DEFAULT_WEIGHT_365 + 1));
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight365IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight365 is less than or equal to DEFAULT_WEIGHT_365
        defaultBovineShouldBeFound("weight365.lessThanOrEqual=" + DEFAULT_WEIGHT_365);

        // Get all the bovineList where weight365 is less than or equal to SMALLER_WEIGHT_365
        defaultBovineShouldNotBeFound("weight365.lessThanOrEqual=" + SMALLER_WEIGHT_365);
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight365IsLessThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight365 is less than DEFAULT_WEIGHT_365
        defaultBovineShouldNotBeFound("weight365.lessThan=" + DEFAULT_WEIGHT_365);

        // Get all the bovineList where weight365 is less than (DEFAULT_WEIGHT_365 + 1)
        defaultBovineShouldBeFound("weight365.lessThan=" + (DEFAULT_WEIGHT_365 + 1));
    }

    @Test
    @Transactional
    public void getAllBovinesByWeight365IsGreaterThanSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);

        // Get all the bovineList where weight365 is greater than DEFAULT_WEIGHT_365
        defaultBovineShouldNotBeFound("weight365.greaterThan=" + DEFAULT_WEIGHT_365);

        // Get all the bovineList where weight365 is greater than SMALLER_WEIGHT_365
        defaultBovineShouldBeFound("weight365.greaterThan=" + SMALLER_WEIGHT_365);
    }


    @Test
    @Transactional
    public void getAllBovinesByJournalEntriesIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);
        JournalEntryEntity journalEntries = JournalEntryResourceIT.createEntity(em);
        em.persist(journalEntries);
        em.flush();
        bovineEntity.addJournalEntries(journalEntries);
        bovineRepository.saveAndFlush(bovineEntity);
        Long journalEntriesId = journalEntries.getId();

        // Get all the bovineList where journalEntries equals to journalEntriesId
        defaultBovineShouldBeFound("journalEntriesId.equals=" + journalEntriesId);

        // Get all the bovineList where journalEntries equals to journalEntriesId + 1
        defaultBovineShouldNotBeFound("journalEntriesId.equals=" + (journalEntriesId + 1));
    }


    @Test
    @Transactional
    public void getAllBovinesBySourceFileIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);
        SourceFileEntity sourceFile = SourceFileResourceIT.createEntity(em);
        em.persist(sourceFile);
        em.flush();
        bovineEntity.setSourceFile(sourceFile);
        bovineRepository.saveAndFlush(bovineEntity);
        Long sourceFileId = sourceFile.getId();

        // Get all the bovineList where sourceFile equals to sourceFileId
        defaultBovineShouldBeFound("sourceFileId.equals=" + sourceFileId);

        // Get all the bovineList where sourceFile equals to sourceFileId + 1
        defaultBovineShouldNotBeFound("sourceFileId.equals=" + (sourceFileId + 1));
    }


    @Test
    @Transactional
    public void getAllBovinesByBlupIsEqualToSomething() throws Exception {
        // Initialize the database
        bovineRepository.saveAndFlush(bovineEntity);
        BlupEntity blup = BlupResourceIT.createEntity(em);
        em.persist(blup);
        em.flush();
        bovineEntity.setBlup(blup);
        blup.setBovine(bovineEntity);
        bovineRepository.saveAndFlush(bovineEntity);
        Long blupId = blup.getId();

        // Get all the bovineList where blup equals to blupId
        defaultBovineShouldBeFound("blupId.equals=" + blupId);

        // Get all the bovineList where blup equals to blupId + 1
        defaultBovineShouldNotBeFound("blupId.equals=" + (blupId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBovineShouldBeFound(String filter) throws Exception {
        restBovineMockMvc.perform(get("/api/bovines?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bovineEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].earTagId").value(hasItem(DEFAULT_EAR_TAG_ID)))
            .andExpect(jsonPath("$.[*].masterIdentifier").value(hasItem(DEFAULT_MASTER_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].herdId").value(hasItem(DEFAULT_HERD_ID)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].bovineStatus").value(hasItem(DEFAULT_BOVINE_STATUS.toString())))
            .andExpect(jsonPath("$.[*].hornStatus").value(hasItem(DEFAULT_HORN_STATUS.toString())))
            .andExpect(jsonPath("$.[*].matriId").value(hasItem(DEFAULT_MATRI_ID)))
            .andExpect(jsonPath("$.[*].patriId").value(hasItem(DEFAULT_PATRI_ID)))
            .andExpect(jsonPath("$.[*].weight0").value(hasItem(DEFAULT_WEIGHT_0)))
            .andExpect(jsonPath("$.[*].weight200").value(hasItem(DEFAULT_WEIGHT_200)))
            .andExpect(jsonPath("$.[*].weight365").value(hasItem(DEFAULT_WEIGHT_365)));

        // Check, that the count call also returns 1
        restBovineMockMvc.perform(get("/api/bovines/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBovineShouldNotBeFound(String filter) throws Exception {
        restBovineMockMvc.perform(get("/api/bovines?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBovineMockMvc.perform(get("/api/bovines/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingBovine() throws Exception {
        // Get the bovine
        restBovineMockMvc.perform(get("/api/bovines/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }
}

package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.JournalEntryEntity;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.repository.JournalEntryRepository;
import com.bonlimousin.replica.service.JournalEntryService;
import com.bonlimousin.replica.service.dto.JournalEntryCriteria;
import com.bonlimousin.replica.service.JournalEntryQueryService;

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

import com.bonlimousin.replica.domain.enumeration.EntryStatus;
/**
 * Integration tests for the {@link JournalEntryResource} REST controller.
 */
@SpringBootTest(classes = BonReplicaServiceApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class JournalEntryResourceIT {

    private static final EntryStatus DEFAULT_STATUS = EntryStatus.FOD;
    private static final EntryStatus UPDATED_STATUS = EntryStatus.IB;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EDITED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EDITED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_HERD_ID = 0;
    private static final Integer UPDATED_HERD_ID = 1;
    private static final Integer SMALLER_HERD_ID = 0 - 1;

    private static final Integer DEFAULT_NEW_HERD_ID = 0;
    private static final Integer UPDATED_NEW_HERD_ID = 1;
    private static final Integer SMALLER_NEW_HERD_ID = 0 - 1;

    private static final Integer DEFAULT_SUB_STATE_1 = 1;
    private static final Integer UPDATED_SUB_STATE_1 = 2;
    private static final Integer SMALLER_SUB_STATE_1 = 1 - 1;

    private static final Integer DEFAULT_SUB_STATE_2 = 1;
    private static final Integer UPDATED_SUB_STATE_2 = 2;
    private static final Integer SMALLER_SUB_STATE_2 = 1 - 1;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private JournalEntryQueryService journalEntryQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJournalEntryMockMvc;

    private JournalEntryEntity journalEntryEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalEntryEntity createEntity(EntityManager em) {
        JournalEntryEntity journalEntryEntity = new JournalEntryEntity()
            .status(DEFAULT_STATUS)
            .date(DEFAULT_DATE)
            .edited(DEFAULT_EDITED)
            .herdId(DEFAULT_HERD_ID)
            .newHerdId(DEFAULT_NEW_HERD_ID)
            .subState1(DEFAULT_SUB_STATE_1)
            .subState2(DEFAULT_SUB_STATE_2);
        return journalEntryEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalEntryEntity createUpdatedEntity(EntityManager em) {
        JournalEntryEntity journalEntryEntity = new JournalEntryEntity()
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE)
            .edited(UPDATED_EDITED)
            .herdId(UPDATED_HERD_ID)
            .newHerdId(UPDATED_NEW_HERD_ID)
            .subState1(UPDATED_SUB_STATE_1)
            .subState2(UPDATED_SUB_STATE_2);
        return journalEntryEntity;
    }

    @BeforeEach
    public void initTest() {
        journalEntryEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void getAllJournalEntries() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList
        restJournalEntryMockMvc.perform(get("/api/journal-entries?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalEntryEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(DEFAULT_EDITED.toString())))
            .andExpect(jsonPath("$.[*].herdId").value(hasItem(DEFAULT_HERD_ID)))
            .andExpect(jsonPath("$.[*].newHerdId").value(hasItem(DEFAULT_NEW_HERD_ID)))
            .andExpect(jsonPath("$.[*].subState1").value(hasItem(DEFAULT_SUB_STATE_1)))
            .andExpect(jsonPath("$.[*].subState2").value(hasItem(DEFAULT_SUB_STATE_2)));
    }
    
    @Test
    @Transactional
    public void getJournalEntry() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get the journalEntry
        restJournalEntryMockMvc.perform(get("/api/journal-entries/{id}", journalEntryEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(journalEntryEntity.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.edited").value(DEFAULT_EDITED.toString()))
            .andExpect(jsonPath("$.herdId").value(DEFAULT_HERD_ID))
            .andExpect(jsonPath("$.newHerdId").value(DEFAULT_NEW_HERD_ID))
            .andExpect(jsonPath("$.subState1").value(DEFAULT_SUB_STATE_1))
            .andExpect(jsonPath("$.subState2").value(DEFAULT_SUB_STATE_2));
    }


    @Test
    @Transactional
    public void getJournalEntriesByIdFiltering() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        Long id = journalEntryEntity.getId();

        defaultJournalEntryShouldBeFound("id.equals=" + id);
        defaultJournalEntryShouldNotBeFound("id.notEquals=" + id);

        defaultJournalEntryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultJournalEntryShouldNotBeFound("id.greaterThan=" + id);

        defaultJournalEntryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultJournalEntryShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllJournalEntriesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where status equals to DEFAULT_STATUS
        defaultJournalEntryShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the journalEntryList where status equals to UPDATED_STATUS
        defaultJournalEntryShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where status not equals to DEFAULT_STATUS
        defaultJournalEntryShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the journalEntryList where status not equals to UPDATED_STATUS
        defaultJournalEntryShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultJournalEntryShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the journalEntryList where status equals to UPDATED_STATUS
        defaultJournalEntryShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where status is not null
        defaultJournalEntryShouldBeFound("status.specified=true");

        // Get all the journalEntryList where status is null
        defaultJournalEntryShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where date equals to DEFAULT_DATE
        defaultJournalEntryShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the journalEntryList where date equals to UPDATED_DATE
        defaultJournalEntryShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where date not equals to DEFAULT_DATE
        defaultJournalEntryShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the journalEntryList where date not equals to UPDATED_DATE
        defaultJournalEntryShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where date in DEFAULT_DATE or UPDATED_DATE
        defaultJournalEntryShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the journalEntryList where date equals to UPDATED_DATE
        defaultJournalEntryShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where date is not null
        defaultJournalEntryShouldBeFound("date.specified=true");

        // Get all the journalEntryList where date is null
        defaultJournalEntryShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByEditedIsEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where edited equals to DEFAULT_EDITED
        defaultJournalEntryShouldBeFound("edited.equals=" + DEFAULT_EDITED);

        // Get all the journalEntryList where edited equals to UPDATED_EDITED
        defaultJournalEntryShouldNotBeFound("edited.equals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByEditedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where edited not equals to DEFAULT_EDITED
        defaultJournalEntryShouldNotBeFound("edited.notEquals=" + DEFAULT_EDITED);

        // Get all the journalEntryList where edited not equals to UPDATED_EDITED
        defaultJournalEntryShouldBeFound("edited.notEquals=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByEditedIsInShouldWork() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where edited in DEFAULT_EDITED or UPDATED_EDITED
        defaultJournalEntryShouldBeFound("edited.in=" + DEFAULT_EDITED + "," + UPDATED_EDITED);

        // Get all the journalEntryList where edited equals to UPDATED_EDITED
        defaultJournalEntryShouldNotBeFound("edited.in=" + UPDATED_EDITED);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByEditedIsNullOrNotNull() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where edited is not null
        defaultJournalEntryShouldBeFound("edited.specified=true");

        // Get all the journalEntryList where edited is null
        defaultJournalEntryShouldNotBeFound("edited.specified=false");
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByHerdIdIsEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where herdId equals to DEFAULT_HERD_ID
        defaultJournalEntryShouldBeFound("herdId.equals=" + DEFAULT_HERD_ID);

        // Get all the journalEntryList where herdId equals to UPDATED_HERD_ID
        defaultJournalEntryShouldNotBeFound("herdId.equals=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByHerdIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where herdId not equals to DEFAULT_HERD_ID
        defaultJournalEntryShouldNotBeFound("herdId.notEquals=" + DEFAULT_HERD_ID);

        // Get all the journalEntryList where herdId not equals to UPDATED_HERD_ID
        defaultJournalEntryShouldBeFound("herdId.notEquals=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByHerdIdIsInShouldWork() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where herdId in DEFAULT_HERD_ID or UPDATED_HERD_ID
        defaultJournalEntryShouldBeFound("herdId.in=" + DEFAULT_HERD_ID + "," + UPDATED_HERD_ID);

        // Get all the journalEntryList where herdId equals to UPDATED_HERD_ID
        defaultJournalEntryShouldNotBeFound("herdId.in=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByHerdIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where herdId is not null
        defaultJournalEntryShouldBeFound("herdId.specified=true");

        // Get all the journalEntryList where herdId is null
        defaultJournalEntryShouldNotBeFound("herdId.specified=false");
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByHerdIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where herdId is greater than or equal to DEFAULT_HERD_ID
        defaultJournalEntryShouldBeFound("herdId.greaterThanOrEqual=" + DEFAULT_HERD_ID);

        // Get all the journalEntryList where herdId is greater than or equal to UPDATED_HERD_ID
        defaultJournalEntryShouldNotBeFound("herdId.greaterThanOrEqual=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByHerdIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where herdId is less than or equal to DEFAULT_HERD_ID
        defaultJournalEntryShouldBeFound("herdId.lessThanOrEqual=" + DEFAULT_HERD_ID);

        // Get all the journalEntryList where herdId is less than or equal to SMALLER_HERD_ID
        defaultJournalEntryShouldNotBeFound("herdId.lessThanOrEqual=" + SMALLER_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByHerdIdIsLessThanSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where herdId is less than DEFAULT_HERD_ID
        defaultJournalEntryShouldNotBeFound("herdId.lessThan=" + DEFAULT_HERD_ID);

        // Get all the journalEntryList where herdId is less than UPDATED_HERD_ID
        defaultJournalEntryShouldBeFound("herdId.lessThan=" + UPDATED_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByHerdIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where herdId is greater than DEFAULT_HERD_ID
        defaultJournalEntryShouldNotBeFound("herdId.greaterThan=" + DEFAULT_HERD_ID);

        // Get all the journalEntryList where herdId is greater than SMALLER_HERD_ID
        defaultJournalEntryShouldBeFound("herdId.greaterThan=" + SMALLER_HERD_ID);
    }


    @Test
    @Transactional
    public void getAllJournalEntriesByNewHerdIdIsEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where newHerdId equals to DEFAULT_NEW_HERD_ID
        defaultJournalEntryShouldBeFound("newHerdId.equals=" + DEFAULT_NEW_HERD_ID);

        // Get all the journalEntryList where newHerdId equals to UPDATED_NEW_HERD_ID
        defaultJournalEntryShouldNotBeFound("newHerdId.equals=" + UPDATED_NEW_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByNewHerdIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where newHerdId not equals to DEFAULT_NEW_HERD_ID
        defaultJournalEntryShouldNotBeFound("newHerdId.notEquals=" + DEFAULT_NEW_HERD_ID);

        // Get all the journalEntryList where newHerdId not equals to UPDATED_NEW_HERD_ID
        defaultJournalEntryShouldBeFound("newHerdId.notEquals=" + UPDATED_NEW_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByNewHerdIdIsInShouldWork() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where newHerdId in DEFAULT_NEW_HERD_ID or UPDATED_NEW_HERD_ID
        defaultJournalEntryShouldBeFound("newHerdId.in=" + DEFAULT_NEW_HERD_ID + "," + UPDATED_NEW_HERD_ID);

        // Get all the journalEntryList where newHerdId equals to UPDATED_NEW_HERD_ID
        defaultJournalEntryShouldNotBeFound("newHerdId.in=" + UPDATED_NEW_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByNewHerdIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where newHerdId is not null
        defaultJournalEntryShouldBeFound("newHerdId.specified=true");

        // Get all the journalEntryList where newHerdId is null
        defaultJournalEntryShouldNotBeFound("newHerdId.specified=false");
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByNewHerdIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where newHerdId is greater than or equal to DEFAULT_NEW_HERD_ID
        defaultJournalEntryShouldBeFound("newHerdId.greaterThanOrEqual=" + DEFAULT_NEW_HERD_ID);

        // Get all the journalEntryList where newHerdId is greater than or equal to UPDATED_NEW_HERD_ID
        defaultJournalEntryShouldNotBeFound("newHerdId.greaterThanOrEqual=" + UPDATED_NEW_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByNewHerdIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where newHerdId is less than or equal to DEFAULT_NEW_HERD_ID
        defaultJournalEntryShouldBeFound("newHerdId.lessThanOrEqual=" + DEFAULT_NEW_HERD_ID);

        // Get all the journalEntryList where newHerdId is less than or equal to SMALLER_NEW_HERD_ID
        defaultJournalEntryShouldNotBeFound("newHerdId.lessThanOrEqual=" + SMALLER_NEW_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByNewHerdIdIsLessThanSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where newHerdId is less than DEFAULT_NEW_HERD_ID
        defaultJournalEntryShouldNotBeFound("newHerdId.lessThan=" + DEFAULT_NEW_HERD_ID);

        // Get all the journalEntryList where newHerdId is less than UPDATED_NEW_HERD_ID
        defaultJournalEntryShouldBeFound("newHerdId.lessThan=" + UPDATED_NEW_HERD_ID);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesByNewHerdIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where newHerdId is greater than DEFAULT_NEW_HERD_ID
        defaultJournalEntryShouldNotBeFound("newHerdId.greaterThan=" + DEFAULT_NEW_HERD_ID);

        // Get all the journalEntryList where newHerdId is greater than SMALLER_NEW_HERD_ID
        defaultJournalEntryShouldBeFound("newHerdId.greaterThan=" + SMALLER_NEW_HERD_ID);
    }


    @Test
    @Transactional
    public void getAllJournalEntriesBySubState1IsEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState1 equals to DEFAULT_SUB_STATE_1
        defaultJournalEntryShouldBeFound("subState1.equals=" + DEFAULT_SUB_STATE_1);

        // Get all the journalEntryList where subState1 equals to UPDATED_SUB_STATE_1
        defaultJournalEntryShouldNotBeFound("subState1.equals=" + UPDATED_SUB_STATE_1);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState1IsNotEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState1 not equals to DEFAULT_SUB_STATE_1
        defaultJournalEntryShouldNotBeFound("subState1.notEquals=" + DEFAULT_SUB_STATE_1);

        // Get all the journalEntryList where subState1 not equals to UPDATED_SUB_STATE_1
        defaultJournalEntryShouldBeFound("subState1.notEquals=" + UPDATED_SUB_STATE_1);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState1IsInShouldWork() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState1 in DEFAULT_SUB_STATE_1 or UPDATED_SUB_STATE_1
        defaultJournalEntryShouldBeFound("subState1.in=" + DEFAULT_SUB_STATE_1 + "," + UPDATED_SUB_STATE_1);

        // Get all the journalEntryList where subState1 equals to UPDATED_SUB_STATE_1
        defaultJournalEntryShouldNotBeFound("subState1.in=" + UPDATED_SUB_STATE_1);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState1IsNullOrNotNull() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState1 is not null
        defaultJournalEntryShouldBeFound("subState1.specified=true");

        // Get all the journalEntryList where subState1 is null
        defaultJournalEntryShouldNotBeFound("subState1.specified=false");
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState1IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState1 is greater than or equal to DEFAULT_SUB_STATE_1
        defaultJournalEntryShouldBeFound("subState1.greaterThanOrEqual=" + DEFAULT_SUB_STATE_1);

        // Get all the journalEntryList where subState1 is greater than or equal to UPDATED_SUB_STATE_1
        defaultJournalEntryShouldNotBeFound("subState1.greaterThanOrEqual=" + UPDATED_SUB_STATE_1);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState1IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState1 is less than or equal to DEFAULT_SUB_STATE_1
        defaultJournalEntryShouldBeFound("subState1.lessThanOrEqual=" + DEFAULT_SUB_STATE_1);

        // Get all the journalEntryList where subState1 is less than or equal to SMALLER_SUB_STATE_1
        defaultJournalEntryShouldNotBeFound("subState1.lessThanOrEqual=" + SMALLER_SUB_STATE_1);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState1IsLessThanSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState1 is less than DEFAULT_SUB_STATE_1
        defaultJournalEntryShouldNotBeFound("subState1.lessThan=" + DEFAULT_SUB_STATE_1);

        // Get all the journalEntryList where subState1 is less than UPDATED_SUB_STATE_1
        defaultJournalEntryShouldBeFound("subState1.lessThan=" + UPDATED_SUB_STATE_1);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState1IsGreaterThanSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState1 is greater than DEFAULT_SUB_STATE_1
        defaultJournalEntryShouldNotBeFound("subState1.greaterThan=" + DEFAULT_SUB_STATE_1);

        // Get all the journalEntryList where subState1 is greater than SMALLER_SUB_STATE_1
        defaultJournalEntryShouldBeFound("subState1.greaterThan=" + SMALLER_SUB_STATE_1);
    }


    @Test
    @Transactional
    public void getAllJournalEntriesBySubState2IsEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState2 equals to DEFAULT_SUB_STATE_2
        defaultJournalEntryShouldBeFound("subState2.equals=" + DEFAULT_SUB_STATE_2);

        // Get all the journalEntryList where subState2 equals to UPDATED_SUB_STATE_2
        defaultJournalEntryShouldNotBeFound("subState2.equals=" + UPDATED_SUB_STATE_2);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState2IsNotEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState2 not equals to DEFAULT_SUB_STATE_2
        defaultJournalEntryShouldNotBeFound("subState2.notEquals=" + DEFAULT_SUB_STATE_2);

        // Get all the journalEntryList where subState2 not equals to UPDATED_SUB_STATE_2
        defaultJournalEntryShouldBeFound("subState2.notEquals=" + UPDATED_SUB_STATE_2);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState2IsInShouldWork() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState2 in DEFAULT_SUB_STATE_2 or UPDATED_SUB_STATE_2
        defaultJournalEntryShouldBeFound("subState2.in=" + DEFAULT_SUB_STATE_2 + "," + UPDATED_SUB_STATE_2);

        // Get all the journalEntryList where subState2 equals to UPDATED_SUB_STATE_2
        defaultJournalEntryShouldNotBeFound("subState2.in=" + UPDATED_SUB_STATE_2);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState2IsNullOrNotNull() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState2 is not null
        defaultJournalEntryShouldBeFound("subState2.specified=true");

        // Get all the journalEntryList where subState2 is null
        defaultJournalEntryShouldNotBeFound("subState2.specified=false");
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState2IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState2 is greater than or equal to DEFAULT_SUB_STATE_2
        defaultJournalEntryShouldBeFound("subState2.greaterThanOrEqual=" + DEFAULT_SUB_STATE_2);

        // Get all the journalEntryList where subState2 is greater than or equal to UPDATED_SUB_STATE_2
        defaultJournalEntryShouldNotBeFound("subState2.greaterThanOrEqual=" + UPDATED_SUB_STATE_2);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState2IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState2 is less than or equal to DEFAULT_SUB_STATE_2
        defaultJournalEntryShouldBeFound("subState2.lessThanOrEqual=" + DEFAULT_SUB_STATE_2);

        // Get all the journalEntryList where subState2 is less than or equal to SMALLER_SUB_STATE_2
        defaultJournalEntryShouldNotBeFound("subState2.lessThanOrEqual=" + SMALLER_SUB_STATE_2);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState2IsLessThanSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState2 is less than DEFAULT_SUB_STATE_2
        defaultJournalEntryShouldNotBeFound("subState2.lessThan=" + DEFAULT_SUB_STATE_2);

        // Get all the journalEntryList where subState2 is less than UPDATED_SUB_STATE_2
        defaultJournalEntryShouldBeFound("subState2.lessThan=" + UPDATED_SUB_STATE_2);
    }

    @Test
    @Transactional
    public void getAllJournalEntriesBySubState2IsGreaterThanSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);

        // Get all the journalEntryList where subState2 is greater than DEFAULT_SUB_STATE_2
        defaultJournalEntryShouldNotBeFound("subState2.greaterThan=" + DEFAULT_SUB_STATE_2);

        // Get all the journalEntryList where subState2 is greater than SMALLER_SUB_STATE_2
        defaultJournalEntryShouldBeFound("subState2.greaterThan=" + SMALLER_SUB_STATE_2);
    }


    @Test
    @Transactional
    public void getAllJournalEntriesByBovineIsEqualToSomething() throws Exception {
        // Initialize the database
        journalEntryRepository.saveAndFlush(journalEntryEntity);
        BovineEntity bovine = BovineResourceIT.createEntity(em);
        em.persist(bovine);
        em.flush();
        journalEntryEntity.setBovine(bovine);
        journalEntryRepository.saveAndFlush(journalEntryEntity);
        Long bovineId = bovine.getId();

        // Get all the journalEntryList where bovine equals to bovineId
        defaultJournalEntryShouldBeFound("bovineId.equals=" + bovineId);

        // Get all the journalEntryList where bovine equals to bovineId + 1
        defaultJournalEntryShouldNotBeFound("bovineId.equals=" + (bovineId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJournalEntryShouldBeFound(String filter) throws Exception {
        restJournalEntryMockMvc.perform(get("/api/journal-entries?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalEntryEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].edited").value(hasItem(DEFAULT_EDITED.toString())))
            .andExpect(jsonPath("$.[*].herdId").value(hasItem(DEFAULT_HERD_ID)))
            .andExpect(jsonPath("$.[*].newHerdId").value(hasItem(DEFAULT_NEW_HERD_ID)))
            .andExpect(jsonPath("$.[*].subState1").value(hasItem(DEFAULT_SUB_STATE_1)))
            .andExpect(jsonPath("$.[*].subState2").value(hasItem(DEFAULT_SUB_STATE_2)));

        // Check, that the count call also returns 1
        restJournalEntryMockMvc.perform(get("/api/journal-entries/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJournalEntryShouldNotBeFound(String filter) throws Exception {
        restJournalEntryMockMvc.perform(get("/api/journal-entries?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJournalEntryMockMvc.perform(get("/api/journal-entries/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingJournalEntry() throws Exception {
        // Get the journalEntry
        restJournalEntryMockMvc.perform(get("/api/journal-entries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }
}

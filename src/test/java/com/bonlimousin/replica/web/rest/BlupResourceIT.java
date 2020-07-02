package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.BlupEntity;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.repository.BlupRepository;
import com.bonlimousin.replica.service.BlupService;
import com.bonlimousin.replica.service.dto.BlupCriteria;
import com.bonlimousin.replica.service.BlupQueryService;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BlupResource} REST controller.
 */
@SpringBootTest(classes = BonReplicaServiceApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class BlupResourceIT {

    private static final Integer DEFAULT_T_0 = 0;
    private static final Integer UPDATED_T_0 = 1;
    private static final Integer SMALLER_T_0 = 0 - 1;

    private static final Integer DEFAULT_D_0 = 0;
    private static final Integer UPDATED_D_0 = 1;
    private static final Integer SMALLER_D_0 = 0 - 1;

    private static final Integer DEFAULT_M_0 = 0;
    private static final Integer UPDATED_M_0 = 1;
    private static final Integer SMALLER_M_0 = 0 - 1;

    private static final Integer DEFAULT_T_200 = 0;
    private static final Integer UPDATED_T_200 = 1;
    private static final Integer SMALLER_T_200 = 0 - 1;

    private static final Integer DEFAULT_D_200 = 0;
    private static final Integer UPDATED_D_200 = 1;
    private static final Integer SMALLER_D_200 = 0 - 1;

    private static final Integer DEFAULT_M_200 = 0;
    private static final Integer UPDATED_M_200 = 1;
    private static final Integer SMALLER_M_200 = 0 - 1;

    private static final Integer DEFAULT_T_365 = 0;
    private static final Integer UPDATED_T_365 = 1;
    private static final Integer SMALLER_T_365 = 0 - 1;

    private static final Integer DEFAULT_D_365 = 0;
    private static final Integer UPDATED_D_365 = 1;
    private static final Integer SMALLER_D_365 = 0 - 1;

    private static final Integer DEFAULT_TOTAL = 0;
    private static final Integer UPDATED_TOTAL = 1;
    private static final Integer SMALLER_TOTAL = 0 - 1;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    @Autowired
    private BlupRepository blupRepository;

    @Autowired
    private BlupService blupService;

    @Autowired
    private BlupQueryService blupQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBlupMockMvc;

    private BlupEntity blupEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlupEntity createEntity(EntityManager em) {
        BlupEntity blupEntity = new BlupEntity()
            .t0(DEFAULT_T_0)
            .d0(DEFAULT_D_0)
            .m0(DEFAULT_M_0)
            .t200(DEFAULT_T_200)
            .d200(DEFAULT_D_200)
            .m200(DEFAULT_M_200)
            .t365(DEFAULT_T_365)
            .d365(DEFAULT_D_365)
            .total(DEFAULT_TOTAL)
            .status(DEFAULT_STATUS);
        // Add required entity
        BovineEntity bovine;
        if (TestUtil.findAll(em, Bovine.class).isEmpty()) {
            bovine = BovineResourceIT.createEntity(em);
            em.persist(bovine);
            em.flush();
        } else {
            bovine = TestUtil.findAll(em, Bovine.class).get(0);
        }
        blupEntity.setBovine(bovine);
        return blupEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlupEntity createUpdatedEntity(EntityManager em) {
        BlupEntity blupEntity = new BlupEntity()
            .t0(UPDATED_T_0)
            .d0(UPDATED_D_0)
            .m0(UPDATED_M_0)
            .t200(UPDATED_T_200)
            .d200(UPDATED_D_200)
            .m200(UPDATED_M_200)
            .t365(UPDATED_T_365)
            .d365(UPDATED_D_365)
            .total(UPDATED_TOTAL)
            .status(UPDATED_STATUS);
        // Add required entity
        BovineEntity bovine;
        if (TestUtil.findAll(em, Bovine.class).isEmpty()) {
            bovine = BovineResourceIT.createUpdatedEntity(em);
            em.persist(bovine);
            em.flush();
        } else {
            bovine = TestUtil.findAll(em, Bovine.class).get(0);
        }
        blupEntity.setBovine(bovine);
        return blupEntity;
    }

    @BeforeEach
    public void initTest() {
        blupEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void getAllBlups() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList
        restBlupMockMvc.perform(get("/api/blups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blupEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].t0").value(hasItem(DEFAULT_T_0)))
            .andExpect(jsonPath("$.[*].d0").value(hasItem(DEFAULT_D_0)))
            .andExpect(jsonPath("$.[*].m0").value(hasItem(DEFAULT_M_0)))
            .andExpect(jsonPath("$.[*].t200").value(hasItem(DEFAULT_T_200)))
            .andExpect(jsonPath("$.[*].d200").value(hasItem(DEFAULT_D_200)))
            .andExpect(jsonPath("$.[*].m200").value(hasItem(DEFAULT_M_200)))
            .andExpect(jsonPath("$.[*].t365").value(hasItem(DEFAULT_T_365)))
            .andExpect(jsonPath("$.[*].d365").value(hasItem(DEFAULT_D_365)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }
    
    @Test
    @Transactional
    public void getBlup() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get the blup
        restBlupMockMvc.perform(get("/api/blups/{id}", blupEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(blupEntity.getId().intValue()))
            .andExpect(jsonPath("$.t0").value(DEFAULT_T_0))
            .andExpect(jsonPath("$.d0").value(DEFAULT_D_0))
            .andExpect(jsonPath("$.m0").value(DEFAULT_M_0))
            .andExpect(jsonPath("$.t200").value(DEFAULT_T_200))
            .andExpect(jsonPath("$.d200").value(DEFAULT_D_200))
            .andExpect(jsonPath("$.m200").value(DEFAULT_M_200))
            .andExpect(jsonPath("$.t365").value(DEFAULT_T_365))
            .andExpect(jsonPath("$.d365").value(DEFAULT_D_365))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }


    @Test
    @Transactional
    public void getBlupsByIdFiltering() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        Long id = blupEntity.getId();

        defaultBlupShouldBeFound("id.equals=" + id);
        defaultBlupShouldNotBeFound("id.notEquals=" + id);

        defaultBlupShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBlupShouldNotBeFound("id.greaterThan=" + id);

        defaultBlupShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBlupShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBlupsByt0IsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t0 equals to DEFAULT_T_0
        defaultBlupShouldBeFound("t0.equals=" + DEFAULT_T_0);

        // Get all the blupList where t0 equals to UPDATED_T_0
        defaultBlupShouldNotBeFound("t0.equals=" + UPDATED_T_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByt0IsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t0 not equals to DEFAULT_T_0
        defaultBlupShouldNotBeFound("t0.notEquals=" + DEFAULT_T_0);

        // Get all the blupList where t0 not equals to UPDATED_T_0
        defaultBlupShouldBeFound("t0.notEquals=" + UPDATED_T_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByt0IsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t0 in DEFAULT_T_0 or UPDATED_T_0
        defaultBlupShouldBeFound("t0.in=" + DEFAULT_T_0 + "," + UPDATED_T_0);

        // Get all the blupList where t0 equals to UPDATED_T_0
        defaultBlupShouldNotBeFound("t0.in=" + UPDATED_T_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByt0IsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t0 is not null
        defaultBlupShouldBeFound("t0.specified=true");

        // Get all the blupList where t0 is null
        defaultBlupShouldNotBeFound("t0.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsByt0IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t0 is greater than or equal to DEFAULT_T_0
        defaultBlupShouldBeFound("t0.greaterThanOrEqual=" + DEFAULT_T_0);

        // Get all the blupList where t0 is greater than or equal to UPDATED_T_0
        defaultBlupShouldNotBeFound("t0.greaterThanOrEqual=" + UPDATED_T_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByt0IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t0 is less than or equal to DEFAULT_T_0
        defaultBlupShouldBeFound("t0.lessThanOrEqual=" + DEFAULT_T_0);

        // Get all the blupList where t0 is less than or equal to SMALLER_T_0
        defaultBlupShouldNotBeFound("t0.lessThanOrEqual=" + SMALLER_T_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByt0IsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t0 is less than DEFAULT_T_0
        defaultBlupShouldNotBeFound("t0.lessThan=" + DEFAULT_T_0);

        // Get all the blupList where t0 is less than UPDATED_T_0
        defaultBlupShouldBeFound("t0.lessThan=" + UPDATED_T_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByt0IsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t0 is greater than DEFAULT_T_0
        defaultBlupShouldNotBeFound("t0.greaterThan=" + DEFAULT_T_0);

        // Get all the blupList where t0 is greater than SMALLER_T_0
        defaultBlupShouldBeFound("t0.greaterThan=" + SMALLER_T_0);
    }


    @Test
    @Transactional
    public void getAllBlupsByd0IsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d0 equals to DEFAULT_D_0
        defaultBlupShouldBeFound("d0.equals=" + DEFAULT_D_0);

        // Get all the blupList where d0 equals to UPDATED_D_0
        defaultBlupShouldNotBeFound("d0.equals=" + UPDATED_D_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByd0IsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d0 not equals to DEFAULT_D_0
        defaultBlupShouldNotBeFound("d0.notEquals=" + DEFAULT_D_0);

        // Get all the blupList where d0 not equals to UPDATED_D_0
        defaultBlupShouldBeFound("d0.notEquals=" + UPDATED_D_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByd0IsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d0 in DEFAULT_D_0 or UPDATED_D_0
        defaultBlupShouldBeFound("d0.in=" + DEFAULT_D_0 + "," + UPDATED_D_0);

        // Get all the blupList where d0 equals to UPDATED_D_0
        defaultBlupShouldNotBeFound("d0.in=" + UPDATED_D_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByd0IsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d0 is not null
        defaultBlupShouldBeFound("d0.specified=true");

        // Get all the blupList where d0 is null
        defaultBlupShouldNotBeFound("d0.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsByd0IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d0 is greater than or equal to DEFAULT_D_0
        defaultBlupShouldBeFound("d0.greaterThanOrEqual=" + DEFAULT_D_0);

        // Get all the blupList where d0 is greater than or equal to UPDATED_D_0
        defaultBlupShouldNotBeFound("d0.greaterThanOrEqual=" + UPDATED_D_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByd0IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d0 is less than or equal to DEFAULT_D_0
        defaultBlupShouldBeFound("d0.lessThanOrEqual=" + DEFAULT_D_0);

        // Get all the blupList where d0 is less than or equal to SMALLER_D_0
        defaultBlupShouldNotBeFound("d0.lessThanOrEqual=" + SMALLER_D_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByd0IsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d0 is less than DEFAULT_D_0
        defaultBlupShouldNotBeFound("d0.lessThan=" + DEFAULT_D_0);

        // Get all the blupList where d0 is less than UPDATED_D_0
        defaultBlupShouldBeFound("d0.lessThan=" + UPDATED_D_0);
    }

    @Test
    @Transactional
    public void getAllBlupsByd0IsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d0 is greater than DEFAULT_D_0
        defaultBlupShouldNotBeFound("d0.greaterThan=" + DEFAULT_D_0);

        // Get all the blupList where d0 is greater than SMALLER_D_0
        defaultBlupShouldBeFound("d0.greaterThan=" + SMALLER_D_0);
    }


    @Test
    @Transactional
    public void getAllBlupsBym0IsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m0 equals to DEFAULT_M_0
        defaultBlupShouldBeFound("m0.equals=" + DEFAULT_M_0);

        // Get all the blupList where m0 equals to UPDATED_M_0
        defaultBlupShouldNotBeFound("m0.equals=" + UPDATED_M_0);
    }

    @Test
    @Transactional
    public void getAllBlupsBym0IsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m0 not equals to DEFAULT_M_0
        defaultBlupShouldNotBeFound("m0.notEquals=" + DEFAULT_M_0);

        // Get all the blupList where m0 not equals to UPDATED_M_0
        defaultBlupShouldBeFound("m0.notEquals=" + UPDATED_M_0);
    }

    @Test
    @Transactional
    public void getAllBlupsBym0IsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m0 in DEFAULT_M_0 or UPDATED_M_0
        defaultBlupShouldBeFound("m0.in=" + DEFAULT_M_0 + "," + UPDATED_M_0);

        // Get all the blupList where m0 equals to UPDATED_M_0
        defaultBlupShouldNotBeFound("m0.in=" + UPDATED_M_0);
    }

    @Test
    @Transactional
    public void getAllBlupsBym0IsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m0 is not null
        defaultBlupShouldBeFound("m0.specified=true");

        // Get all the blupList where m0 is null
        defaultBlupShouldNotBeFound("m0.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsBym0IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m0 is greater than or equal to DEFAULT_M_0
        defaultBlupShouldBeFound("m0.greaterThanOrEqual=" + DEFAULT_M_0);

        // Get all the blupList where m0 is greater than or equal to UPDATED_M_0
        defaultBlupShouldNotBeFound("m0.greaterThanOrEqual=" + UPDATED_M_0);
    }

    @Test
    @Transactional
    public void getAllBlupsBym0IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m0 is less than or equal to DEFAULT_M_0
        defaultBlupShouldBeFound("m0.lessThanOrEqual=" + DEFAULT_M_0);

        // Get all the blupList where m0 is less than or equal to SMALLER_M_0
        defaultBlupShouldNotBeFound("m0.lessThanOrEqual=" + SMALLER_M_0);
    }

    @Test
    @Transactional
    public void getAllBlupsBym0IsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m0 is less than DEFAULT_M_0
        defaultBlupShouldNotBeFound("m0.lessThan=" + DEFAULT_M_0);

        // Get all the blupList where m0 is less than UPDATED_M_0
        defaultBlupShouldBeFound("m0.lessThan=" + UPDATED_M_0);
    }

    @Test
    @Transactional
    public void getAllBlupsBym0IsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m0 is greater than DEFAULT_M_0
        defaultBlupShouldNotBeFound("m0.greaterThan=" + DEFAULT_M_0);

        // Get all the blupList where m0 is greater than SMALLER_M_0
        defaultBlupShouldBeFound("m0.greaterThan=" + SMALLER_M_0);
    }


    @Test
    @Transactional
    public void getAllBlupsByt200IsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t200 equals to DEFAULT_T_200
        defaultBlupShouldBeFound("t200.equals=" + DEFAULT_T_200);

        // Get all the blupList where t200 equals to UPDATED_T_200
        defaultBlupShouldNotBeFound("t200.equals=" + UPDATED_T_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByt200IsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t200 not equals to DEFAULT_T_200
        defaultBlupShouldNotBeFound("t200.notEquals=" + DEFAULT_T_200);

        // Get all the blupList where t200 not equals to UPDATED_T_200
        defaultBlupShouldBeFound("t200.notEquals=" + UPDATED_T_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByt200IsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t200 in DEFAULT_T_200 or UPDATED_T_200
        defaultBlupShouldBeFound("t200.in=" + DEFAULT_T_200 + "," + UPDATED_T_200);

        // Get all the blupList where t200 equals to UPDATED_T_200
        defaultBlupShouldNotBeFound("t200.in=" + UPDATED_T_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByt200IsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t200 is not null
        defaultBlupShouldBeFound("t200.specified=true");

        // Get all the blupList where t200 is null
        defaultBlupShouldNotBeFound("t200.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsByt200IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t200 is greater than or equal to DEFAULT_T_200
        defaultBlupShouldBeFound("t200.greaterThanOrEqual=" + DEFAULT_T_200);

        // Get all the blupList where t200 is greater than or equal to UPDATED_T_200
        defaultBlupShouldNotBeFound("t200.greaterThanOrEqual=" + UPDATED_T_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByt200IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t200 is less than or equal to DEFAULT_T_200
        defaultBlupShouldBeFound("t200.lessThanOrEqual=" + DEFAULT_T_200);

        // Get all the blupList where t200 is less than or equal to SMALLER_T_200
        defaultBlupShouldNotBeFound("t200.lessThanOrEqual=" + SMALLER_T_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByt200IsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t200 is less than DEFAULT_T_200
        defaultBlupShouldNotBeFound("t200.lessThan=" + DEFAULT_T_200);

        // Get all the blupList where t200 is less than UPDATED_T_200
        defaultBlupShouldBeFound("t200.lessThan=" + UPDATED_T_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByt200IsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t200 is greater than DEFAULT_T_200
        defaultBlupShouldNotBeFound("t200.greaterThan=" + DEFAULT_T_200);

        // Get all the blupList where t200 is greater than SMALLER_T_200
        defaultBlupShouldBeFound("t200.greaterThan=" + SMALLER_T_200);
    }


    @Test
    @Transactional
    public void getAllBlupsByd200IsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d200 equals to DEFAULT_D_200
        defaultBlupShouldBeFound("d200.equals=" + DEFAULT_D_200);

        // Get all the blupList where d200 equals to UPDATED_D_200
        defaultBlupShouldNotBeFound("d200.equals=" + UPDATED_D_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByd200IsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d200 not equals to DEFAULT_D_200
        defaultBlupShouldNotBeFound("d200.notEquals=" + DEFAULT_D_200);

        // Get all the blupList where d200 not equals to UPDATED_D_200
        defaultBlupShouldBeFound("d200.notEquals=" + UPDATED_D_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByd200IsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d200 in DEFAULT_D_200 or UPDATED_D_200
        defaultBlupShouldBeFound("d200.in=" + DEFAULT_D_200 + "," + UPDATED_D_200);

        // Get all the blupList where d200 equals to UPDATED_D_200
        defaultBlupShouldNotBeFound("d200.in=" + UPDATED_D_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByd200IsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d200 is not null
        defaultBlupShouldBeFound("d200.specified=true");

        // Get all the blupList where d200 is null
        defaultBlupShouldNotBeFound("d200.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsByd200IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d200 is greater than or equal to DEFAULT_D_200
        defaultBlupShouldBeFound("d200.greaterThanOrEqual=" + DEFAULT_D_200);

        // Get all the blupList where d200 is greater than or equal to UPDATED_D_200
        defaultBlupShouldNotBeFound("d200.greaterThanOrEqual=" + UPDATED_D_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByd200IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d200 is less than or equal to DEFAULT_D_200
        defaultBlupShouldBeFound("d200.lessThanOrEqual=" + DEFAULT_D_200);

        // Get all the blupList where d200 is less than or equal to SMALLER_D_200
        defaultBlupShouldNotBeFound("d200.lessThanOrEqual=" + SMALLER_D_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByd200IsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d200 is less than DEFAULT_D_200
        defaultBlupShouldNotBeFound("d200.lessThan=" + DEFAULT_D_200);

        // Get all the blupList where d200 is less than UPDATED_D_200
        defaultBlupShouldBeFound("d200.lessThan=" + UPDATED_D_200);
    }

    @Test
    @Transactional
    public void getAllBlupsByd200IsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d200 is greater than DEFAULT_D_200
        defaultBlupShouldNotBeFound("d200.greaterThan=" + DEFAULT_D_200);

        // Get all the blupList where d200 is greater than SMALLER_D_200
        defaultBlupShouldBeFound("d200.greaterThan=" + SMALLER_D_200);
    }


    @Test
    @Transactional
    public void getAllBlupsBym200IsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m200 equals to DEFAULT_M_200
        defaultBlupShouldBeFound("m200.equals=" + DEFAULT_M_200);

        // Get all the blupList where m200 equals to UPDATED_M_200
        defaultBlupShouldNotBeFound("m200.equals=" + UPDATED_M_200);
    }

    @Test
    @Transactional
    public void getAllBlupsBym200IsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m200 not equals to DEFAULT_M_200
        defaultBlupShouldNotBeFound("m200.notEquals=" + DEFAULT_M_200);

        // Get all the blupList where m200 not equals to UPDATED_M_200
        defaultBlupShouldBeFound("m200.notEquals=" + UPDATED_M_200);
    }

    @Test
    @Transactional
    public void getAllBlupsBym200IsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m200 in DEFAULT_M_200 or UPDATED_M_200
        defaultBlupShouldBeFound("m200.in=" + DEFAULT_M_200 + "," + UPDATED_M_200);

        // Get all the blupList where m200 equals to UPDATED_M_200
        defaultBlupShouldNotBeFound("m200.in=" + UPDATED_M_200);
    }

    @Test
    @Transactional
    public void getAllBlupsBym200IsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m200 is not null
        defaultBlupShouldBeFound("m200.specified=true");

        // Get all the blupList where m200 is null
        defaultBlupShouldNotBeFound("m200.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsBym200IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m200 is greater than or equal to DEFAULT_M_200
        defaultBlupShouldBeFound("m200.greaterThanOrEqual=" + DEFAULT_M_200);

        // Get all the blupList where m200 is greater than or equal to UPDATED_M_200
        defaultBlupShouldNotBeFound("m200.greaterThanOrEqual=" + UPDATED_M_200);
    }

    @Test
    @Transactional
    public void getAllBlupsBym200IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m200 is less than or equal to DEFAULT_M_200
        defaultBlupShouldBeFound("m200.lessThanOrEqual=" + DEFAULT_M_200);

        // Get all the blupList where m200 is less than or equal to SMALLER_M_200
        defaultBlupShouldNotBeFound("m200.lessThanOrEqual=" + SMALLER_M_200);
    }

    @Test
    @Transactional
    public void getAllBlupsBym200IsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m200 is less than DEFAULT_M_200
        defaultBlupShouldNotBeFound("m200.lessThan=" + DEFAULT_M_200);

        // Get all the blupList where m200 is less than UPDATED_M_200
        defaultBlupShouldBeFound("m200.lessThan=" + UPDATED_M_200);
    }

    @Test
    @Transactional
    public void getAllBlupsBym200IsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where m200 is greater than DEFAULT_M_200
        defaultBlupShouldNotBeFound("m200.greaterThan=" + DEFAULT_M_200);

        // Get all the blupList where m200 is greater than SMALLER_M_200
        defaultBlupShouldBeFound("m200.greaterThan=" + SMALLER_M_200);
    }


    @Test
    @Transactional
    public void getAllBlupsByt365IsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t365 equals to DEFAULT_T_365
        defaultBlupShouldBeFound("t365.equals=" + DEFAULT_T_365);

        // Get all the blupList where t365 equals to UPDATED_T_365
        defaultBlupShouldNotBeFound("t365.equals=" + UPDATED_T_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByt365IsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t365 not equals to DEFAULT_T_365
        defaultBlupShouldNotBeFound("t365.notEquals=" + DEFAULT_T_365);

        // Get all the blupList where t365 not equals to UPDATED_T_365
        defaultBlupShouldBeFound("t365.notEquals=" + UPDATED_T_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByt365IsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t365 in DEFAULT_T_365 or UPDATED_T_365
        defaultBlupShouldBeFound("t365.in=" + DEFAULT_T_365 + "," + UPDATED_T_365);

        // Get all the blupList where t365 equals to UPDATED_T_365
        defaultBlupShouldNotBeFound("t365.in=" + UPDATED_T_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByt365IsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t365 is not null
        defaultBlupShouldBeFound("t365.specified=true");

        // Get all the blupList where t365 is null
        defaultBlupShouldNotBeFound("t365.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsByt365IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t365 is greater than or equal to DEFAULT_T_365
        defaultBlupShouldBeFound("t365.greaterThanOrEqual=" + DEFAULT_T_365);

        // Get all the blupList where t365 is greater than or equal to UPDATED_T_365
        defaultBlupShouldNotBeFound("t365.greaterThanOrEqual=" + UPDATED_T_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByt365IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t365 is less than or equal to DEFAULT_T_365
        defaultBlupShouldBeFound("t365.lessThanOrEqual=" + DEFAULT_T_365);

        // Get all the blupList where t365 is less than or equal to SMALLER_T_365
        defaultBlupShouldNotBeFound("t365.lessThanOrEqual=" + SMALLER_T_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByt365IsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t365 is less than DEFAULT_T_365
        defaultBlupShouldNotBeFound("t365.lessThan=" + DEFAULT_T_365);

        // Get all the blupList where t365 is less than UPDATED_T_365
        defaultBlupShouldBeFound("t365.lessThan=" + UPDATED_T_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByt365IsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where t365 is greater than DEFAULT_T_365
        defaultBlupShouldNotBeFound("t365.greaterThan=" + DEFAULT_T_365);

        // Get all the blupList where t365 is greater than SMALLER_T_365
        defaultBlupShouldBeFound("t365.greaterThan=" + SMALLER_T_365);
    }


    @Test
    @Transactional
    public void getAllBlupsByd365IsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d365 equals to DEFAULT_D_365
        defaultBlupShouldBeFound("d365.equals=" + DEFAULT_D_365);

        // Get all the blupList where d365 equals to UPDATED_D_365
        defaultBlupShouldNotBeFound("d365.equals=" + UPDATED_D_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByd365IsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d365 not equals to DEFAULT_D_365
        defaultBlupShouldNotBeFound("d365.notEquals=" + DEFAULT_D_365);

        // Get all the blupList where d365 not equals to UPDATED_D_365
        defaultBlupShouldBeFound("d365.notEquals=" + UPDATED_D_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByd365IsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d365 in DEFAULT_D_365 or UPDATED_D_365
        defaultBlupShouldBeFound("d365.in=" + DEFAULT_D_365 + "," + UPDATED_D_365);

        // Get all the blupList where d365 equals to UPDATED_D_365
        defaultBlupShouldNotBeFound("d365.in=" + UPDATED_D_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByd365IsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d365 is not null
        defaultBlupShouldBeFound("d365.specified=true");

        // Get all the blupList where d365 is null
        defaultBlupShouldNotBeFound("d365.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsByd365IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d365 is greater than or equal to DEFAULT_D_365
        defaultBlupShouldBeFound("d365.greaterThanOrEqual=" + DEFAULT_D_365);

        // Get all the blupList where d365 is greater than or equal to UPDATED_D_365
        defaultBlupShouldNotBeFound("d365.greaterThanOrEqual=" + UPDATED_D_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByd365IsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d365 is less than or equal to DEFAULT_D_365
        defaultBlupShouldBeFound("d365.lessThanOrEqual=" + DEFAULT_D_365);

        // Get all the blupList where d365 is less than or equal to SMALLER_D_365
        defaultBlupShouldNotBeFound("d365.lessThanOrEqual=" + SMALLER_D_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByd365IsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d365 is less than DEFAULT_D_365
        defaultBlupShouldNotBeFound("d365.lessThan=" + DEFAULT_D_365);

        // Get all the blupList where d365 is less than UPDATED_D_365
        defaultBlupShouldBeFound("d365.lessThan=" + UPDATED_D_365);
    }

    @Test
    @Transactional
    public void getAllBlupsByd365IsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where d365 is greater than DEFAULT_D_365
        defaultBlupShouldNotBeFound("d365.greaterThan=" + DEFAULT_D_365);

        // Get all the blupList where d365 is greater than SMALLER_D_365
        defaultBlupShouldBeFound("d365.greaterThan=" + SMALLER_D_365);
    }


    @Test
    @Transactional
    public void getAllBlupsByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where total equals to DEFAULT_TOTAL
        defaultBlupShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the blupList where total equals to UPDATED_TOTAL
        defaultBlupShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllBlupsByTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where total not equals to DEFAULT_TOTAL
        defaultBlupShouldNotBeFound("total.notEquals=" + DEFAULT_TOTAL);

        // Get all the blupList where total not equals to UPDATED_TOTAL
        defaultBlupShouldBeFound("total.notEquals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllBlupsByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultBlupShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the blupList where total equals to UPDATED_TOTAL
        defaultBlupShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllBlupsByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where total is not null
        defaultBlupShouldBeFound("total.specified=true");

        // Get all the blupList where total is null
        defaultBlupShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlupsByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where total is greater than or equal to DEFAULT_TOTAL
        defaultBlupShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the blupList where total is greater than or equal to UPDATED_TOTAL
        defaultBlupShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllBlupsByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where total is less than or equal to DEFAULT_TOTAL
        defaultBlupShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the blupList where total is less than or equal to SMALLER_TOTAL
        defaultBlupShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    public void getAllBlupsByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where total is less than DEFAULT_TOTAL
        defaultBlupShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the blupList where total is less than UPDATED_TOTAL
        defaultBlupShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllBlupsByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where total is greater than DEFAULT_TOTAL
        defaultBlupShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the blupList where total is greater than SMALLER_TOTAL
        defaultBlupShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }


    @Test
    @Transactional
    public void getAllBlupsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where status equals to DEFAULT_STATUS
        defaultBlupShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the blupList where status equals to UPDATED_STATUS
        defaultBlupShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBlupsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where status not equals to DEFAULT_STATUS
        defaultBlupShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the blupList where status not equals to UPDATED_STATUS
        defaultBlupShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBlupsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultBlupShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the blupList where status equals to UPDATED_STATUS
        defaultBlupShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBlupsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where status is not null
        defaultBlupShouldBeFound("status.specified=true");

        // Get all the blupList where status is null
        defaultBlupShouldNotBeFound("status.specified=false");
    }
                @Test
    @Transactional
    public void getAllBlupsByStatusContainsSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where status contains DEFAULT_STATUS
        defaultBlupShouldBeFound("status.contains=" + DEFAULT_STATUS);

        // Get all the blupList where status contains UPDATED_STATUS
        defaultBlupShouldNotBeFound("status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBlupsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        blupRepository.saveAndFlush(blupEntity);

        // Get all the blupList where status does not contain DEFAULT_STATUS
        defaultBlupShouldNotBeFound("status.doesNotContain=" + DEFAULT_STATUS);

        // Get all the blupList where status does not contain UPDATED_STATUS
        defaultBlupShouldBeFound("status.doesNotContain=" + UPDATED_STATUS);
    }


    @Test
    @Transactional
    public void getAllBlupsByBovineIsEqualToSomething() throws Exception {
        // Get already existing entity
        BovineEntity bovine = blupEntity.getBovine();
        blupRepository.saveAndFlush(blupEntity);
        Long bovineId = bovine.getId();

        // Get all the blupList where bovine equals to bovineId
        defaultBlupShouldBeFound("bovineId.equals=" + bovineId);

        // Get all the blupList where bovine equals to bovineId + 1
        defaultBlupShouldNotBeFound("bovineId.equals=" + (bovineId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBlupShouldBeFound(String filter) throws Exception {
        restBlupMockMvc.perform(get("/api/blups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blupEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].t0").value(hasItem(DEFAULT_T_0)))
            .andExpect(jsonPath("$.[*].d0").value(hasItem(DEFAULT_D_0)))
            .andExpect(jsonPath("$.[*].m0").value(hasItem(DEFAULT_M_0)))
            .andExpect(jsonPath("$.[*].t200").value(hasItem(DEFAULT_T_200)))
            .andExpect(jsonPath("$.[*].d200").value(hasItem(DEFAULT_D_200)))
            .andExpect(jsonPath("$.[*].m200").value(hasItem(DEFAULT_M_200)))
            .andExpect(jsonPath("$.[*].t365").value(hasItem(DEFAULT_T_365)))
            .andExpect(jsonPath("$.[*].d365").value(hasItem(DEFAULT_D_365)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));

        // Check, that the count call also returns 1
        restBlupMockMvc.perform(get("/api/blups/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBlupShouldNotBeFound(String filter) throws Exception {
        restBlupMockMvc.perform(get("/api/blups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBlupMockMvc.perform(get("/api/blups/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingBlup() throws Exception {
        // Get the blup
        restBlupMockMvc.perform(get("/api/blups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }
}

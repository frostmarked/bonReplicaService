package com.bonlimousin.replica.web.rest;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.repository.SourceFileRepository;
import com.bonlimousin.replica.service.SourceFileService;
import com.bonlimousin.replica.service.dto.SourceFileCriteria;
import com.bonlimousin.replica.service.SourceFileQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link SourceFileResource} REST controller.
 */
@SpringBootTest(classes = BonReplicaServiceApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class SourceFileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_ZIP_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ZIP_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_ZIP_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ZIP_FILE_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_PROCESSED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PROCESSED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_OUTCOME = "AAAAAAAAAA";
    private static final String UPDATED_OUTCOME = "BBBBBBBBBB";

    @Autowired
    private SourceFileRepository sourceFileRepository;

    @Autowired
    private SourceFileService sourceFileService;

    @Autowired
    private SourceFileQueryService sourceFileQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSourceFileMockMvc;

    private SourceFileEntity sourceFileEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SourceFileEntity createEntity(EntityManager em) {
        SourceFileEntity sourceFileEntity = new SourceFileEntity()
            .name(DEFAULT_NAME)
            .zipFile(DEFAULT_ZIP_FILE)
            .zipFileContentType(DEFAULT_ZIP_FILE_CONTENT_TYPE)
            .processed(DEFAULT_PROCESSED)
            .outcome(DEFAULT_OUTCOME);
        return sourceFileEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SourceFileEntity createUpdatedEntity(EntityManager em) {
        SourceFileEntity sourceFileEntity = new SourceFileEntity()
            .name(UPDATED_NAME)
            .zipFile(UPDATED_ZIP_FILE)
            .zipFileContentType(UPDATED_ZIP_FILE_CONTENT_TYPE)
            .processed(UPDATED_PROCESSED)
            .outcome(UPDATED_OUTCOME);
        return sourceFileEntity;
    }

    @BeforeEach
    public void initTest() {
        sourceFileEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void createSourceFile() throws Exception {
        int databaseSizeBeforeCreate = sourceFileRepository.findAll().size();
        // Create the SourceFile
        restSourceFileMockMvc.perform(post("/api/source-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sourceFileEntity)))
            .andExpect(status().isCreated());

        // Validate the SourceFile in the database
        List<SourceFileEntity> sourceFileList = sourceFileRepository.findAll();
        assertThat(sourceFileList).hasSize(databaseSizeBeforeCreate + 1);
        SourceFileEntity testSourceFile = sourceFileList.get(sourceFileList.size() - 1);
        assertThat(testSourceFile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSourceFile.getZipFile()).isEqualTo(DEFAULT_ZIP_FILE);
        assertThat(testSourceFile.getZipFileContentType()).isEqualTo(DEFAULT_ZIP_FILE_CONTENT_TYPE);
        assertThat(testSourceFile.getProcessed()).isEqualTo(DEFAULT_PROCESSED);
        assertThat(testSourceFile.getOutcome()).isEqualTo(DEFAULT_OUTCOME);
    }

    @Test
    @Transactional
    public void createSourceFileWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sourceFileRepository.findAll().size();

        // Create the SourceFile with an existing ID
        sourceFileEntity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSourceFileMockMvc.perform(post("/api/source-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sourceFileEntity)))
            .andExpect(status().isBadRequest());

        // Validate the SourceFile in the database
        List<SourceFileEntity> sourceFileList = sourceFileRepository.findAll();
        assertThat(sourceFileList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sourceFileRepository.findAll().size();
        // set the field null
        sourceFileEntity.setName(null);

        // Create the SourceFile, which fails.


        restSourceFileMockMvc.perform(post("/api/source-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sourceFileEntity)))
            .andExpect(status().isBadRequest());

        List<SourceFileEntity> sourceFileList = sourceFileRepository.findAll();
        assertThat(sourceFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSourceFiles() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList
        restSourceFileMockMvc.perform(get("/api/source-files?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sourceFileEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].zipFileContentType").value(hasItem(DEFAULT_ZIP_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].zipFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_ZIP_FILE))))
            .andExpect(jsonPath("$.[*].processed").value(hasItem(DEFAULT_PROCESSED.toString())))
            .andExpect(jsonPath("$.[*].outcome").value(hasItem(DEFAULT_OUTCOME)));
    }
    
    @Test
    @Transactional
    public void getSourceFile() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get the sourceFile
        restSourceFileMockMvc.perform(get("/api/source-files/{id}", sourceFileEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sourceFileEntity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.zipFileContentType").value(DEFAULT_ZIP_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.zipFile").value(Base64Utils.encodeToString(DEFAULT_ZIP_FILE)))
            .andExpect(jsonPath("$.processed").value(DEFAULT_PROCESSED.toString()))
            .andExpect(jsonPath("$.outcome").value(DEFAULT_OUTCOME));
    }


    @Test
    @Transactional
    public void getSourceFilesByIdFiltering() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        Long id = sourceFileEntity.getId();

        defaultSourceFileShouldBeFound("id.equals=" + id);
        defaultSourceFileShouldNotBeFound("id.notEquals=" + id);

        defaultSourceFileShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSourceFileShouldNotBeFound("id.greaterThan=" + id);

        defaultSourceFileShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSourceFileShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllSourceFilesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where name equals to DEFAULT_NAME
        defaultSourceFileShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the sourceFileList where name equals to UPDATED_NAME
        defaultSourceFileShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where name not equals to DEFAULT_NAME
        defaultSourceFileShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the sourceFileList where name not equals to UPDATED_NAME
        defaultSourceFileShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSourceFileShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the sourceFileList where name equals to UPDATED_NAME
        defaultSourceFileShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where name is not null
        defaultSourceFileShouldBeFound("name.specified=true");

        // Get all the sourceFileList where name is null
        defaultSourceFileShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllSourceFilesByNameContainsSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where name contains DEFAULT_NAME
        defaultSourceFileShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the sourceFileList where name contains UPDATED_NAME
        defaultSourceFileShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where name does not contain DEFAULT_NAME
        defaultSourceFileShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the sourceFileList where name does not contain UPDATED_NAME
        defaultSourceFileShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllSourceFilesByProcessedIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where processed equals to DEFAULT_PROCESSED
        defaultSourceFileShouldBeFound("processed.equals=" + DEFAULT_PROCESSED);

        // Get all the sourceFileList where processed equals to UPDATED_PROCESSED
        defaultSourceFileShouldNotBeFound("processed.equals=" + UPDATED_PROCESSED);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByProcessedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where processed not equals to DEFAULT_PROCESSED
        defaultSourceFileShouldNotBeFound("processed.notEquals=" + DEFAULT_PROCESSED);

        // Get all the sourceFileList where processed not equals to UPDATED_PROCESSED
        defaultSourceFileShouldBeFound("processed.notEquals=" + UPDATED_PROCESSED);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByProcessedIsInShouldWork() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where processed in DEFAULT_PROCESSED or UPDATED_PROCESSED
        defaultSourceFileShouldBeFound("processed.in=" + DEFAULT_PROCESSED + "," + UPDATED_PROCESSED);

        // Get all the sourceFileList where processed equals to UPDATED_PROCESSED
        defaultSourceFileShouldNotBeFound("processed.in=" + UPDATED_PROCESSED);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByProcessedIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where processed is not null
        defaultSourceFileShouldBeFound("processed.specified=true");

        // Get all the sourceFileList where processed is null
        defaultSourceFileShouldNotBeFound("processed.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourceFilesByOutcomeIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where outcome equals to DEFAULT_OUTCOME
        defaultSourceFileShouldBeFound("outcome.equals=" + DEFAULT_OUTCOME);

        // Get all the sourceFileList where outcome equals to UPDATED_OUTCOME
        defaultSourceFileShouldNotBeFound("outcome.equals=" + UPDATED_OUTCOME);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByOutcomeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where outcome not equals to DEFAULT_OUTCOME
        defaultSourceFileShouldNotBeFound("outcome.notEquals=" + DEFAULT_OUTCOME);

        // Get all the sourceFileList where outcome not equals to UPDATED_OUTCOME
        defaultSourceFileShouldBeFound("outcome.notEquals=" + UPDATED_OUTCOME);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByOutcomeIsInShouldWork() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where outcome in DEFAULT_OUTCOME or UPDATED_OUTCOME
        defaultSourceFileShouldBeFound("outcome.in=" + DEFAULT_OUTCOME + "," + UPDATED_OUTCOME);

        // Get all the sourceFileList where outcome equals to UPDATED_OUTCOME
        defaultSourceFileShouldNotBeFound("outcome.in=" + UPDATED_OUTCOME);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByOutcomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where outcome is not null
        defaultSourceFileShouldBeFound("outcome.specified=true");

        // Get all the sourceFileList where outcome is null
        defaultSourceFileShouldNotBeFound("outcome.specified=false");
    }
                @Test
    @Transactional
    public void getAllSourceFilesByOutcomeContainsSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where outcome contains DEFAULT_OUTCOME
        defaultSourceFileShouldBeFound("outcome.contains=" + DEFAULT_OUTCOME);

        // Get all the sourceFileList where outcome contains UPDATED_OUTCOME
        defaultSourceFileShouldNotBeFound("outcome.contains=" + UPDATED_OUTCOME);
    }

    @Test
    @Transactional
    public void getAllSourceFilesByOutcomeNotContainsSomething() throws Exception {
        // Initialize the database
        sourceFileRepository.saveAndFlush(sourceFileEntity);

        // Get all the sourceFileList where outcome does not contain DEFAULT_OUTCOME
        defaultSourceFileShouldNotBeFound("outcome.doesNotContain=" + DEFAULT_OUTCOME);

        // Get all the sourceFileList where outcome does not contain UPDATED_OUTCOME
        defaultSourceFileShouldBeFound("outcome.doesNotContain=" + UPDATED_OUTCOME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSourceFileShouldBeFound(String filter) throws Exception {
        restSourceFileMockMvc.perform(get("/api/source-files?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sourceFileEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].zipFileContentType").value(hasItem(DEFAULT_ZIP_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].zipFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_ZIP_FILE))))
            .andExpect(jsonPath("$.[*].processed").value(hasItem(DEFAULT_PROCESSED.toString())))
            .andExpect(jsonPath("$.[*].outcome").value(hasItem(DEFAULT_OUTCOME)));

        // Check, that the count call also returns 1
        restSourceFileMockMvc.perform(get("/api/source-files/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSourceFileShouldNotBeFound(String filter) throws Exception {
        restSourceFileMockMvc.perform(get("/api/source-files?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSourceFileMockMvc.perform(get("/api/source-files/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingSourceFile() throws Exception {
        // Get the sourceFile
        restSourceFileMockMvc.perform(get("/api/source-files/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSourceFile() throws Exception {
        // Initialize the database
        sourceFileService.save(sourceFileEntity);

        int databaseSizeBeforeUpdate = sourceFileRepository.findAll().size();

        // Update the sourceFile
        SourceFileEntity updatedSourceFileEntity = sourceFileRepository.findById(sourceFileEntity.getId()).get();
        // Disconnect from session so that the updates on updatedSourceFileEntity are not directly saved in db
        em.detach(updatedSourceFileEntity);
        updatedSourceFileEntity
            .name(UPDATED_NAME)
            .zipFile(UPDATED_ZIP_FILE)
            .zipFileContentType(UPDATED_ZIP_FILE_CONTENT_TYPE)
            .processed(UPDATED_PROCESSED)
            .outcome(UPDATED_OUTCOME);

        restSourceFileMockMvc.perform(put("/api/source-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSourceFileEntity)))
            .andExpect(status().isOk());

        // Validate the SourceFile in the database
        List<SourceFileEntity> sourceFileList = sourceFileRepository.findAll();
        assertThat(sourceFileList).hasSize(databaseSizeBeforeUpdate);
        SourceFileEntity testSourceFile = sourceFileList.get(sourceFileList.size() - 1);
        assertThat(testSourceFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSourceFile.getZipFile()).isEqualTo(UPDATED_ZIP_FILE);
        assertThat(testSourceFile.getZipFileContentType()).isEqualTo(UPDATED_ZIP_FILE_CONTENT_TYPE);
        assertThat(testSourceFile.getProcessed()).isEqualTo(UPDATED_PROCESSED);
        assertThat(testSourceFile.getOutcome()).isEqualTo(UPDATED_OUTCOME);
    }

    @Test
    @Transactional
    public void updateNonExistingSourceFile() throws Exception {
        int databaseSizeBeforeUpdate = sourceFileRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSourceFileMockMvc.perform(put("/api/source-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sourceFileEntity)))
            .andExpect(status().isBadRequest());

        // Validate the SourceFile in the database
        List<SourceFileEntity> sourceFileList = sourceFileRepository.findAll();
        assertThat(sourceFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSourceFile() throws Exception {
        // Initialize the database
        sourceFileService.save(sourceFileEntity);

        int databaseSizeBeforeDelete = sourceFileRepository.findAll().size();

        // Delete the sourceFile
        restSourceFileMockMvc.perform(delete("/api/source-files/{id}", sourceFileEntity.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SourceFileEntity> sourceFileList = sourceFileRepository.findAll();
        assertThat(sourceFileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

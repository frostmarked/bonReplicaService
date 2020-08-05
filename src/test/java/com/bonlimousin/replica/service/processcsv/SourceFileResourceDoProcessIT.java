package com.bonlimousin.replica.service.processcsv;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.SourceFileService;
import com.bonlimousin.replica.web.rest.SourceFileResource;

/**
 * Integration tests for the {@link SourceFileResource} REST controller.
 */
@SpringBootTest(classes = BonReplicaServiceApp.class)
@AutoConfigureMockMvc
@WithMockUser
class SourceFileResourceDoProcessIT {

	/* 
	 * includes necessary csv files 
	 * * ancestry -> harst
	 * * weight -> vagning
	 * * journal entries -> journal
	 * 
	 * content is reduced to 2916 and her mother 2713 and father 2688
	 */
	private static final String TEST_ZIP_FILE = "src/test/resources/fixtures/csv_se015112_truncated.zip";

	@Autowired
	private SourceFileService sourceFileService;
	
	@Autowired
	private BovineRepository bovineRepository;

	@Autowired
	private MockMvc restSourceFileMockMvc;

	private SourceFileEntity sourceFileEntity;

	public static SourceFileEntity createEntity() throws FileNotFoundException, IOException {
		byte[] zipBytes = IOUtils.toByteArray(new FileInputStream(TEST_ZIP_FILE));
		SourceFileEntity sourceFileEntity = new SourceFileEntity()
				.name("test.zip")
				.zipFile(zipBytes)
				.zipFileContentType("application/zip")
				.processed(null)
				.outcome(null);		
		return sourceFileEntity;
	}

	@BeforeEach
	public void initTest() throws FileNotFoundException, IOException {
		sourceFileEntity = createEntity();
	}

	@Test
	@Transactional
	void dryRunSyncSourceFileProcess() throws Exception {
		// Initialize the database
		sourceFileEntity = sourceFileService.save(sourceFileEntity);

		// Process the sourceFile
		restSourceFileMockMvc.perform(
				post("/api/source-files/{id}/process", sourceFileEntity.getId())
				.param("isRunAsync", "false")
				.param("isDryRun", "true")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
		
		Optional<SourceFileEntity> sfeOpt = sourceFileService.findOne(sourceFileEntity.getId());
		Assert.assertTrue(sfeOpt.isPresent());
		Assert.assertNull(sfeOpt.get().getOutcome());
		Assert.assertNull(sfeOpt.get().getProcessed());
		
		Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(2916);
		Assert.assertFalse(opt.isPresent());
	}
		
	@Test
	@Transactional
	void syncSourceFileProcess() throws Exception {
		// Initialize the database
		sourceFileEntity = sourceFileService.save(sourceFileEntity);
		Assert.assertNull(sourceFileEntity.getOutcome());
		Assert.assertNull(sourceFileEntity.getProcessed());
		
		// Process the sourceFile
		restSourceFileMockMvc.perform(
				post("/api/source-files/{id}/process", sourceFileEntity.getId())
				.param("isRunAsync", "false")
				.param("isDryRun", "false")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
		
		Optional<SourceFileEntity> sfeOpt = sourceFileService.findOne(sourceFileEntity.getId());
		Assert.assertTrue(sfeOpt.isPresent());
		Assert.assertNotNull(sfeOpt.get().getOutcome());
		Assert.assertNotNull(sfeOpt.get().getProcessed());
		
		Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(2916);
		Assert.assertTrue(opt.isPresent());
		
		BovineEntity bovine2916 = opt.get();
		Assert.assertEquals(2916, bovine2916.getEarTagId().intValue());
		Assert.assertEquals("2916", bovine2916.getMasterIdentifier());
		Assert.assertEquals("Polled Europe av Bon", bovine2916.getName());		
		Assert.assertEquals(2713, bovine2916.getMatriId().intValue());
		Assert.assertEquals(2688, bovine2916.getPatriId().intValue());
		Assert.assertEquals(35, bovine2916.getWeight0().intValue());
		Assert.assertNull(bovine2916.getWeight200());
		Assert.assertNull(bovine2916.getWeight365());
		Assert.assertEquals(BovineStatus.ON_FARM, bovine2916.getBovineStatus());
		Assert.assertEquals("se", bovine2916.getCountry());
		Assert.assertEquals(Gender.HEIFER, bovine2916.getGender());
		Assert.assertEquals(15112, bovine2916.getHerdId().intValue());
		Assert.assertEquals("test.zip", bovine2916.getSourceFile().getName());
		// not impl, yet
		Assert.assertEquals(0, bovine2916.getJournalEntries().size());
	}
}

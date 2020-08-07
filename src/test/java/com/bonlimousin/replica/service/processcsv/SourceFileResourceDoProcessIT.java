package com.bonlimousin.replica.service.processcsv;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
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
	
	private static final String TEST_ZIP_FILE_UPDATED_NAME = "src/test/resources/fixtures/csv_se015112_upd_harst_name.zip";
	
	@Autowired
	private SourceFileService sourceFileService;
	
	@Autowired
	private BovineRepository bovineRepository;

	@Autowired
	private MockMvc restSourceFileMockMvc;

	private SourceFileEntity sourceFileEntity;

	public static SourceFileEntity createEntity(String fileName) throws FileNotFoundException, IOException {
		byte[] zipBytes = IOUtils.toByteArray(new FileInputStream(fileName));
		SourceFileEntity sourceFileEntity = new SourceFileEntity()
				.name("test.zip")
				.zipFile(zipBytes)
				.zipFileContentType("application/zip")
				.processed(null)
				.outcome(null);		
		return sourceFileEntity;
	}
	
	@Test
	void missingSourceFile() throws Exception {		
		restSourceFileMockMvc.perform(
				post("/api/source-files/{id}/process", Integer.MAX_VALUE)
				.param("isRunAsync", "false")
				.param("isDryRun", "true")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());		
	}

	@Test
	@Transactional
	void dryRunSyncSourceFileProcess() throws Exception {
		sourceFileEntity = createEntity(TEST_ZIP_FILE);
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
		Assert.assertNotNull(sfeOpt.get().getOutcome());
		Assert.assertNotNull(sfeOpt.get().getProcessed());
		
		Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(2916);
		Assert.assertFalse(opt.isPresent());
	}
		
	@Test
	@Transactional
	void syncSourceFileProcess() throws Exception {
		sourceFileEntity = createEntity(TEST_ZIP_FILE);
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
	
	@Test	
	void sourceFileProcess() throws Exception {
		SourceFileEntity sfe = createEntity(TEST_ZIP_FILE);
		sfe = sourceFileService.save(sfe);
		Assert.assertNull(sfe.getOutcome());
		Assert.assertNull(sfe.getProcessed());
		
		// Process the sourceFile
		restSourceFileMockMvc.perform(
				post("/api/source-files/{id}/process", sfe.getId())
				.param("isRunAsync", "false")
				.param("isDryRun", "false")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
		Optional<SourceFileEntity> sfeOpt = sourceFileService.findOne(sfe.getId());
		Assert.assertTrue(sfeOpt.isPresent());
		Assert.assertNotNull(sfeOpt.get().getOutcome());
		Assert.assertNotNull(sfeOpt.get().getProcessed());
		
		sourceFileEntity = createEntity(TEST_ZIP_FILE_UPDATED_NAME);
		sourceFileEntity = sourceFileService.save(sourceFileEntity);
		Assert.assertNull(sourceFileEntity.getOutcome());
		Assert.assertNull(sourceFileEntity.getProcessed());
		
		Optional<BovineEntity> optOrg = bovineRepository.findOneByEarTagId(2916);
		Assert.assertTrue(optOrg.isPresent());		
		Assert.assertThat(optOrg.get().getName(), not(containsString("TEST")));
		
		restSourceFileMockMvc.perform(
				post("/api/source-files/{id}/process", sourceFileEntity.getId())
				.param("isRunAsync", "true")
				.param("isDryRun", "false")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			
		Optional<SourceFileEntity> sfeUpdOpt = null;
		for(int i=1; i<10; i++) {
			TimeUnit.SECONDS.sleep(i);
			sfeUpdOpt = sourceFileService.findOne(sourceFileEntity.getId());
			if(sfeUpdOpt.isPresent() && sfeUpdOpt.get().getProcessed() != null) {
				break;
			}
		}
		if(sfeUpdOpt.isEmpty() || sfeUpdOpt.get().getProcessed() == null) {
			Assert.fail("Could not find update of bovine name");
		}
		
		Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(2916);
		Assert.assertTrue(opt.isPresent());
		
		BovineEntity be = opt.get();		
		Assert.assertThat(be.getName(), containsString("TEST"));
		
		bovineRepository.deleteAll();
		sourceFileService.delete(sfe.getId());
		sourceFileService.delete(sourceFileEntity.getId());		
	}
}

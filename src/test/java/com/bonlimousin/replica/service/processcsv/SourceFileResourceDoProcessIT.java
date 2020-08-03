package com.bonlimousin.replica.service.processcsv;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;

import org.apache.commons.io.IOUtils;
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
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.service.SourceFileService;
import com.bonlimousin.replica.web.rest.SourceFileResource;

/**
 * Integration tests for the {@link SourceFileResource} REST controller.
 */
@SpringBootTest(classes = BonReplicaServiceApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class SourceFileResourceDoProcessIT {

	private static final String TEST_ZIP_FILE = "src/test/resources/fixtures/csv_se015112.zip";

	@Autowired
	private SourceFileService sourceFileService;

	@Autowired
	private MockMvc restSourceFileMockMvc;

	private SourceFileEntity sourceFileEntity;

	/**
	 * Create an entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static SourceFileEntity createEntity() throws FileNotFoundException, IOException {
		byte[] zipBytes = IOUtils.toByteArray(new FileInputStream(TEST_ZIP_FILE));
		SourceFileEntity sourceFileEntity = new SourceFileEntity().name("test.zip").zipFile(zipBytes)
				.zipFileContentType("application/zip").processed(Instant.ofEpochMilli(0L)).outcome("hoho");
		return sourceFileEntity;
	}

	@BeforeEach
	public void initTest() throws FileNotFoundException, IOException {
		sourceFileEntity = createEntity();
	}

	@Test
	@Transactional
	public void dryRunSyncSourceFileProcess() throws Exception {
		// Initialize the database
		sourceFileService.save(sourceFileEntity);

		// Process the sourceFile
		restSourceFileMockMvc.perform(
				post("/api/source-files/{id}/process", sourceFileEntity.getId())
				.param("isRunAsync", "false")
				.param("isDryRun", "true")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}
		
	@Test
	@Transactional
	public void syncSourceFileProcess() throws Exception {
		// Initialize the database
		sourceFileService.save(sourceFileEntity);

		// Process the sourceFile
		restSourceFileMockMvc.perform(
				post("/api/source-files/{id}/process", sourceFileEntity.getId())
				.param("isRunAsync", "false")
				.param("isDryRun", "false")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}
}

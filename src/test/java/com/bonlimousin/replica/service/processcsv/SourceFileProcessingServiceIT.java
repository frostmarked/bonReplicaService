package com.bonlimousin.replica.service.processcsv;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.validation.ValidationException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.service.SourceFileService;

@SpringBootTest(classes = BonReplicaServiceApp.class)
public class SourceFileProcessingServiceIT {

	private static final String TEST_ZIP_FILE_BROKEN_HEADER = "src/test/resources/fixtures/csv_se015112_broken_header.zip";
	private static final String TEST_ZIP_FILE_EMPTY_LINES = "src/test/resources/fixtures/csv_se015112_empty_lines.zip";
	private static final String TEST_ZIP_FILE_MISSING_LINES = "src/test/resources/fixtures/csv_se015112_missing_lines.zip";
	private static final String TEST_ZIP_FILE_MISSING_ID_FIRST = "src/test/resources/fixtures/csv_se015112_missing_id_first.zip";
	private static final String TEST_ZIP_FILE_MISSING_ID_SECOND = "src/test/resources/fixtures/csv_se015112_missing_id_second.zip";

	@Autowired
	private SourceFileService sourceFileService;
	
	@Autowired
	private SourceFileProcessingService sourceFileProcessingService;
	
	public static SourceFileEntity createEntity(String zipFile) throws FileNotFoundException, IOException {
		byte[] zipBytes = IOUtils.toByteArray(new FileInputStream(zipFile));
		SourceFileEntity sourceFileEntity = new SourceFileEntity()
				.name("test.zip")
				.zipFile(zipBytes)
				.zipFileContentType("application/zip")
				.processed(null)
				.outcome(null);		
		return sourceFileEntity;
	}

	@Test
	@Transactional
	void processBrokenHeader() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(createEntity(TEST_ZIP_FILE_BROKEN_HEADER));
		Long id = sfe.getId();
		assertThrows(ValidationException.class, () ->
			sourceFileProcessingService.process(id, false, true)
		);
	}
	
	@Test
	@Transactional
	void processEmptyLines() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(createEntity(TEST_ZIP_FILE_EMPTY_LINES));
		Long id = sfe.getId();
		assertThrows(ValidationException.class, () ->
			sourceFileProcessingService.process(id, false, true)
		);
	}
	
	@Test
	@Transactional
	void processMissingLines() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(createEntity(TEST_ZIP_FILE_MISSING_LINES));
		Long id = sfe.getId();
		assertThrows(ValidationException.class, () ->
			sourceFileProcessingService.process(id, false, true)
		);
	}
	
	@Test
	@Transactional
	void processMissingIdFirst() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(createEntity(TEST_ZIP_FILE_MISSING_ID_FIRST));
		Long id = sfe.getId();
		assertThrows(ValidationException.class, () ->
			sourceFileProcessingService.process(id, false, true)
		);
	}
	
	@Test
	@Transactional
	void processMissingIdSecond() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(createEntity(TEST_ZIP_FILE_MISSING_ID_SECOND));
		Long id = sfe.getId();
		assertThrows(ValidationException.class, () ->
			sourceFileProcessingService.process(id, false, true)
		);
	}
}

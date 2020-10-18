package com.bonlimousin.replica.service.processcsv.pckap;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.service.SourceFileService;
import com.bonlimousin.replica.service.processcsv.ProcessCSVTestUtils;
import com.bonlimousin.replica.service.processcsv.SourceFileProcessingService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = BonReplicaServiceApp.class)
class FaultyPcKapSourceFileIT {

	private static final String TEST_ZIP_FILE_BROKEN_HEADER = "src/test/resources/fixtures/pckap/csv_se015112_broken_header.zip";
	private static final String TEST_ZIP_FILE_EMPTY_LINES = "src/test/resources/fixtures/pckap/csv_se015112_empty_lines.zip";
	private static final String TEST_ZIP_FILE_MISSING_LINES = "src/test/resources/fixtures/pckap/csv_se015112_missing_lines.zip";
	private static final String TEST_ZIP_FILE_MISSING_ID_FIRST = "src/test/resources/fixtures/pckap/csv_se015112_missing_id_first.zip";
	private static final String TEST_ZIP_FILE_MISSING_ID_SECOND = "src/test/resources/fixtures/pckap/csv_se015112_missing_id_second.zip";

	@Autowired
	private SourceFileService sourceFileService;

	@Autowired
	private SourceFileProcessingService sourceFileProcessingService;

	@Test
	@Transactional
	void processBrokenHeader() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(ProcessCSVTestUtils.createSourceFileEntity(TEST_ZIP_FILE_BROKEN_HEADER));
		Long id = sfe.getId();
        int processedRows = sourceFileProcessingService.process(id, false, true);
		assertEquals(0, processedRows);
	}

	@Test
	@Transactional
	void processEmptyLines() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(ProcessCSVTestUtils.createSourceFileEntity(TEST_ZIP_FILE_EMPTY_LINES));
		Long id = sfe.getId();
        int processedRows = sourceFileProcessingService.process(id, false, true);
        assertEquals(3, processedRows); // weight records are missing bovine
	}

	@Test
	@Transactional
	void processMissingLines() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(ProcessCSVTestUtils.createSourceFileEntity(TEST_ZIP_FILE_MISSING_LINES));
		Long id = sfe.getId();
        int processedRows = sourceFileProcessingService.process(id, false, true);
        assertEquals(0, processedRows);
	}

	@Test
	@Transactional
	void processMissingIdFirst() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(ProcessCSVTestUtils.createSourceFileEntity(TEST_ZIP_FILE_MISSING_ID_FIRST));
		int processed = sourceFileProcessingService.process(sfe.getId(), false, true);
		Assertions.assertThat(processed).isZero();
	}

	@Test
	@Transactional
	void processMissingIdSecond() throws Exception {
		SourceFileEntity sfe = sourceFileService.save(ProcessCSVTestUtils.createSourceFileEntity(TEST_ZIP_FILE_MISSING_ID_SECOND));
        int processed = sourceFileProcessingService.process(sfe.getId(), false, true);
        Assertions.assertThat(processed).isEqualTo(2);
	}
}

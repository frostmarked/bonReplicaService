package com.bonlimousin.replica.service.processcsv;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.validation.ValidationException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class CsvValidatorTest {

	private static final String TEST_ZIP_FILE = "src/test/resources/fixtures/csv_se015112_truncated.zip";
	private static final String TEST_CSV_DIR = "src/test/resources/fixtures/csv";

	@Test
	void validateAncestryZipFile() throws IOException {
		byte[] zipBytes = IOUtils.toByteArray(new FileInputStream(TEST_ZIP_FILE));
		CsvValidator.validateZipFile(CsvFile.ANCESTRY.fileName(), CsvAncestryColumns.values(), zipBytes);
	}

	@Test
	void validateWeightZipFile() throws IOException {
		byte[] zipBytes = IOUtils.toByteArray(new FileInputStream(TEST_ZIP_FILE));
		CsvValidator.validateZipFile(CsvFile.WEIGHT.fileName(), CsvWeightColumns.values(), zipBytes);
	}

	@Test
	void validateJournalZipFile() throws IOException {
		byte[] zipBytes = IOUtils.toByteArray(new FileInputStream(TEST_ZIP_FILE));
		CsvValidator.validateZipFile(CsvFile.JOURNAL.fileName(), CsvJournalColumns.values(), zipBytes);
	}

	@Test
	void validateAncestryFile() throws IOException {
		CsvFile csvFile = CsvFile.ANCESTRY;
		FileInputStream fis = new FileInputStream(TEST_CSV_DIR + File.separator + csvFile.fileName());
		CsvValidator.validateCsvFile(csvFile.fileName(), CsvAncestryColumns.values(), fis);
	}

	@Test
	void validateWeightFile() throws IOException {
		CsvFile csvFile = CsvFile.WEIGHT;
		FileInputStream fis = new FileInputStream(TEST_CSV_DIR + File.separator + csvFile.fileName());
		CsvValidator.validateCsvFile(csvFile.fileName(), CsvWeightColumns.values(), fis);
	}

	@Test
	void validateJournalFile() throws IOException {
		CsvFile csvFile = CsvFile.JOURNAL;
		FileInputStream fis = new FileInputStream(TEST_CSV_DIR + File.separator + csvFile.fileName());
		CsvValidator.validateCsvFile(csvFile.fileName(), CsvJournalColumns.values(), fis);
	}

	@Test
	void faultyConfig() throws IOException {
		FileInputStream fis = new FileInputStream(TEST_CSV_DIR + File.separator + CsvFile.JOURNAL.fileName());
		ValidationException ve = assertThrows(ValidationException.class, () -> 			
			CsvValidator.validateCsvFile(CsvFile.ANCESTRY.fileName(), CsvAncestryColumns.values(), fis)
		);
		assertTrue(ve.getMessage().contains("missing"));
	}
}

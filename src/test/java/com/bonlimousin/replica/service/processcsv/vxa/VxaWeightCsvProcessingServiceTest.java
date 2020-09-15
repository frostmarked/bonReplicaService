package com.bonlimousin.replica.service.processcsv.vxa;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.service.processcsv.CsvFileConfig;

import liquibase.util.csv.opencsv.CSVReader;

@SpringBootTest(classes = BonReplicaServiceApp.class)
class VxaWeightCsvProcessingServiceTest {

	private static final String TEST_CSV_2916 = "src/test/resources/fixtures/vxa/csv/vxa_weight_2916.csv";

	@Autowired
	private VxaWeightCsvProcessingService service;

	@Test
	void populate2916() throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(TEST_CSV_2916), CsvFileConfig.VXA_ANCESTRY.separator(),
				CsvFileConfig.VXA_ANCESTRY.quoteChar());
		csvReader.readNext(); // ignore header
		BovineEntity be = service.populateBovineEntity(csvReader.readNext(), new BovineEntity());
		assertEquals(2916, be.getEarTagId());
		assertEquals(35, be.getWeight0());
		assertNull(be.getWeight200());
		assertNull(be.getWeight365());

	}
}

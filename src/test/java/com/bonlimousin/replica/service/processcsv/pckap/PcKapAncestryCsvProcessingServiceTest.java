package com.bonlimousin.replica.service.processcsv.pckap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;

import liquibase.util.csv.opencsv.CSVReader;

@SpringBootTest(classes = BonReplicaServiceApp.class)
class PcKapAncestryCsvProcessingServiceTest {

	private static final String TEST_CSV_2916 = "src/test/resources/fixtures/pckap/csv/sj_harst_2916.csv";
	
	@Autowired
	private PcKapAncestryCsvProcessingService service;
	
	@Test
	void populate2916() throws IOException {		
		CSVReader csvReader = new CSVReader(new FileReader(TEST_CSV_2916), ',', '"');		
		csvReader.readNext(); // ignore header
		BovineEntity be = service.populateBovineEntity(csvReader.readNext(), new BovineEntity());
		assertEquals(2916, be.getEarTagId());
		assertEquals(15112, be.getHerdId());
		assertEquals(2713, be.getMatriId());
		assertEquals(2688, be.getPatriId());
		assertEquals(Gender.HEIFER, be.getGender());
		assertEquals(BovineStatus.ON_FARM, be.getBovineStatus());			
		assertEquals(2019, LocalDateTime.ofInstant(be.getBirthDate(), ZoneOffset.UTC).get(ChronoField.YEAR));
	}
}

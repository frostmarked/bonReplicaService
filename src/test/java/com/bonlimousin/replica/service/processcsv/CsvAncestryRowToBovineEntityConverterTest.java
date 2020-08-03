package com.bonlimousin.replica.service.processcsv;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Test;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;

import liquibase.util.csv.opencsv.CSVReader;

public class CsvAncestryRowToBovineEntityConverterTest {

	private static final String TEST_CSV_2916 = "src/test/resources/fixtures/csv/sj_harst_2916.csv";
	
	@Test
	public void convert2916() throws IOException {		
		CSVReader csvReader = new CSVReader(new FileReader(TEST_CSV_2916), ',', '"');
		BovineEntity be = new BovineEntity();
		csvReader.readNext(); // ignore header
		CsvAncestryRowToBovineEntityConverter.convert(csvReader.readNext(), be);
		assertEquals(2916, be.getEarTagId());
		assertEquals(15112, be.getHerdId());
		assertEquals(2713, be.getMatriId());
		assertEquals(2688, be.getPatriId());
		assertEquals(Gender.HEIFER, be.getGender());
		assertEquals(BovineStatus.ON_FARM, be.getBovineStatus());			
		assertTrue(LocalDateTime.ofInstant(be.getBirthDate(), ZoneOffset.UTC).get(ChronoField.YEAR) == 2019);
	}
}

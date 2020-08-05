package com.bonlimousin.replica.service.processcsv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.bonlimousin.replica.domain.BovineEntity;

import liquibase.util.csv.opencsv.CSVReader;

class CsvWeightRowToBovineEntityConverterTest {

	private static final String TEST_CSV_2916 = "src/test/resources/fixtures/csv/sj_vagning_2916.csv";
	
	@Test
	void convert2916() throws IOException {		
		CSVReader csvReader = new CSVReader(new FileReader(TEST_CSV_2916), ',', '"');
		BovineEntity be = new BovineEntity();
		csvReader.readNext(); // ignore header
		CsvWeightRowToBovineEntityConverter.convert(csvReader.readNext(), be);
		assertEquals(2916, be.getEarTagId());
		assertEquals(35, be.getWeight0());
	}
}

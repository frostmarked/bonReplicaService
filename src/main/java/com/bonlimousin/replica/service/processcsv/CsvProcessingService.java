package com.bonlimousin.replica.service.processcsv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.bonlimousin.replica.domain.SourceFileEntity;

import liquibase.util.csv.CSVReader;

public interface CsvProcessingService {
	
	CsvFileConfig getCsvFileConfig();
	
	void processCsvFile(SourceFileEntity sfe, boolean isDryRun, CSVReader csvReader) throws IOException;

	default void processCsvFile(SourceFileEntity sfe, boolean isDryRun)
			throws IOException {
		CsvFileConfig csvFile = getCsvFileConfig();
		try (ByteArrayInputStream bais = new ByteArrayInputStream(sfe.getZipFile());
				ZipInputStream zis = new ZipInputStream(bais)) {
			for (ZipEntry ze; (ze = zis.getNextEntry()) != null;) {
				if (csvFile.fileName().equals(ze.getName())) {
					try (CSVReader csvReader = new CSVReader(new InputStreamReader(zis, StandardCharsets.UTF_8),
							csvFile.separator(), csvFile.quoteChar())) {
						processCsvFile(sfe, isDryRun, csvReader);
						return;
					}
				}
			}
		}
	}
}

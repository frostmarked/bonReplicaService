package com.bonlimousin.replica.service.processcsv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.util.csv.CSVReader;

public class CsvValidator {

	private static final Logger log = LoggerFactory.getLogger(CsvValidator.class);

	private CsvValidator() {
		
	}
	
	public static void validateZipFile(String csvFileName, CsvColumns[] columns, byte[] zipBytes) throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
				ZipInputStream zis = new ZipInputStream(bais)) {
			for (ZipEntry ze; (ze = zis.getNextEntry()) != null;) {
				log.debug("File: {} Size: {} Date: {}", ze.getName(), ze.getSize(), ze.getLastModifiedTime());
				if (csvFileName.equals(ze.getName())) {
					validateCsvFile(ze.getName(), columns, zis);
					return;
				}
			}
		}
		throw new ValidationException("Zip is missing file " + csvFileName);
	}

	public static void validateCsvFile(String fileName, CsvColumns[] columns, InputStream csvis) throws IOException {
		try (CSVReader csvReader = new CSVReader(new InputStreamReader(csvis, StandardCharsets.UTF_8))) {
			String[] header = csvReader.readNext();
			if (header == null) {
				throw new ValidationException("No headers in csv " + fileName);
			}
			for (CsvColumns col : columns) {
				if (header.length <= col.columnIndex()) {
					String msg = MessageFormat.format("{0} has fewer columns ({1}) then given index ({2}) for column {3}", fileName, header.length, col.columnIndex(), col.name());
					throw new ValidationException(msg);
				}
				if (!header[col.columnIndex()].equals(col.columnName())) {
					String msg = MessageFormat.format("Column {0} is missing or misplaced in file {1}. Value on index ({2}) is {3}", col.name(), fileName, col.columnIndex(), header[col.columnIndex()]);
					throw new ValidationException(msg);
				}
			}

			String[] row = csvReader.readNext();
			if (row == null || row.length == 0) {
				throw new ValidationException("No records in csv " + fileName);
			}
			for (CsvColumns col : columns) {
				if (row.length <= col.columnIndex()) {
					String msg = MessageFormat.format("First row for {0} has fewer columns ({1}) then given index ({2}) for column {3}", fileName, row.length, col.columnIndex(), col.name());
					throw new ValidationException(msg);
				}
				if (!col.nullableValue() && StringUtils.trimToNull(row[col.columnIndex()]) == null) {
					String msg = MessageFormat.format("First row for {0} is missing column {1}", fileName, col.name());
					throw new ValidationException(msg);					
				}
			}
		}
	}
}

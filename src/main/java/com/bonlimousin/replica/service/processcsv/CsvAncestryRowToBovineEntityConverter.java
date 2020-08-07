package com.bonlimousin.replica.service.processcsv;

import java.time.LocalDate;
import java.time.ZoneId;

import org.apache.commons.lang3.StringUtils;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.HornStatus;

public class CsvAncestryRowToBovineEntityConverter {
	
	private CsvAncestryRowToBovineEntityConverter() {
		
	}

	public static BovineEntity convert(String[] cells, BovineEntity be) {				
		be.setEarTagId(CsvRowToEntityConverterUtils.createId(cells, CsvAncestryColumns.EAR_TAG_ID));
		
		String csvMasterIdentifier = cells[CsvAncestryColumns.MASTER_IDENTIFIER.columnIndex()];
		be.setMasterIdentifier(csvMasterIdentifier);
		
		String csvName = cells[CsvAncestryColumns.NAME.columnIndex()];
		be.setName(csvName);
		
		String csvCountry = cells[CsvAncestryColumns.COUNTRY_ID.columnIndex()];
		be.setCountry(StringUtils.lowerCase(csvCountry));
				
		be.setHerdId(CsvRowToEntityConverterUtils.createId(cells, CsvAncestryColumns.HERD_ID));
		
		String csvBirthDate = cells[CsvAncestryColumns.BIRTH_DATE.columnIndex()];
		be.setBirthDate(LocalDate.parse(csvBirthDate).atStartOfDay(ZoneId.of("Europe/Stockholm")).toInstant());
				
		String csvGender = cells[CsvAncestryColumns.GENDER.columnIndex()];
		be.setGender("2".equals(csvGender) ? Gender.HEIFER : Gender.BULL);
				
		be.setMatriId(CsvRowToEntityConverterUtils.createId(cells, CsvAncestryColumns.MATRI_ID));
		
		be.setPatriId(CsvRowToEntityConverterUtils.createId(cells, CsvAncestryColumns.PATRI_ID));
		
		be.setHornStatus(HornStatus.UNKNOWN); // TODO if exists
		
		String csvUnregistered = cells[CsvAncestryColumns.UNREGISTERED.columnIndex()];
		if(StringUtils.trimToNull(csvUnregistered) != null) {
			be.setBovineStatus(BovineStatus.UNKNOWN);
			// TODO unregistered date only tells us that the cow 
			// has left the farm. NOT why it left the farm. 
			// can be several options... dig deeper
		} else {
			be.setBovineStatus(BovineStatus.ON_FARM);
		}
		
		return be;
	}
}

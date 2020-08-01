package com.bonlimousin.replica.service.processcsv;

import java.time.LocalDate;
import java.time.ZoneId;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.HornStatus;

public class CsvRowToBovineEntityConverter {

	public static BovineEntity convert(String[] cells, BovineEntity be) {		
		String csvEarTagId = cells[CsvAncestryColumns.EAR_TAG_ID.columnIndex()];
		be.setEarTagId(NumberUtils.createInteger(StringUtils.replace(csvEarTagId, ".0", "")));
		
		String csvName = cells[CsvAncestryColumns.NAME.columnIndex()];
		be.setName(csvName);
		
		String csvCountry = cells[CsvAncestryColumns.COUNTRY_ID.columnIndex()];
		be.setCountry(StringUtils.lowerCase(csvCountry));
		
		String csvHerdId = cells[CsvAncestryColumns.HERD_ID.columnIndex()];
		be.setHerdId(NumberUtils.createInteger(StringUtils.replace(csvHerdId, ".0", "")));
		
		String csvBirthDate = cells[CsvAncestryColumns.BIRTH_DATE.columnIndex()];
		be.setBirthDate(LocalDate.parse(csvBirthDate).atStartOfDay(ZoneId.of("Europe/Stockholm")).toInstant());
				
		String csvGender = cells[CsvAncestryColumns.GENDER.columnIndex()];
		be.setGender("2".equals(csvGender) ? Gender.HEIFER : Gender.BULL);
				
		String csvMatriId = cells[CsvAncestryColumns.MATRI_ID.columnIndex()];
		be.setMatriId(NumberUtils.createInteger(StringUtils.replace(csvMatriId, ".0", "")));
		
		String csvPatriId = cells[CsvAncestryColumns.PATRI_ID.columnIndex()];
		be.setPatriId(NumberUtils.createInteger(StringUtils.replace(csvPatriId, ".0", "")));
		
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

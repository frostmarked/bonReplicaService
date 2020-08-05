package com.bonlimousin.replica.service.processcsv;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.bonlimousin.replica.domain.BovineEntity;

public class CsvWeightRowToBovineEntityConverter {

	private CsvWeightRowToBovineEntityConverter() {
		
	}
	
	public static BovineEntity convert(String[] cells, BovineEntity be) {				
		String csvEarTagId = cells[CsvWeightColumns.EAR_TAG_ID.columnIndex()];
		be.setEarTagId(NumberUtils.createInteger(StringUtils.replace(csvEarTagId, ".0", "")));
		
		String csvWeight = cells[CsvWeightColumns.WEIGHT.columnIndex()];
		Integer weight = NumberUtils.createInteger(StringUtils.replace(csvWeight, ".0", ""));
				
		String csvType = cells[CsvWeightColumns.TYPE.columnIndex()];
		switch (csvType) {
		case "F":
			be.setWeight0(weight);
			break;
		case "W200": // TODO where are the 200 days weight?!
			be.setWeight200(weight);
			break;
		case "W365": // TODO where are the 365 days weight?!
			be.setWeight365(weight);
			break;
		default:
			break;
		}
		
		return be;
	}
}

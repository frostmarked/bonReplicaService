package com.bonlimousin.replica.service.processcsv;

import java.text.MessageFormat;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class CsvRowToEntityConverterUtils {
	
	private CsvRowToEntityConverterUtils() {
		
	}
	
	public static Integer createId(String[] cells, CsvColumns col) {
		String val = cells[col.columnIndex()];
		String stripped = StringUtils.trimToNull(StringUtils.replace(val, ".0", ""));
		if(!col.nullableValue() && stripped == null) {
			String msg = MessageFormat.format("The value {0} for column {1} is required but not and id", val, col.name());
			throw new ValidationException(msg);
		} else if(stripped == null) {
			return 0; // unknown parent seems to have zero anyway
		}
		return NumberUtils.createInteger(stripped);
	}
}

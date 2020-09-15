package com.bonlimousin.replica.service.processcsv;

import java.text.MessageFormat;
import java.util.Optional;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class CsvProcessingUtils {
	
	private CsvProcessingUtils() {
		
	}
	
	public static Optional<Integer> createId(String[] cells, CsvColumns col) {
		String val = cells[col.columnIndex()];
		String stripped = StringUtils.trimToNull(StringUtils.replace(val, ".0", ""));
		if(!col.nullableValue() && stripped == null) {
			String msg = MessageFormat.format("The value {0} for column {1} is required but not and id", val, col.name());
			throw new ValidationException(msg);
		}
		return Optional.ofNullable(NumberUtils.createInteger(stripped));
	}
	
	public static Optional<Integer> extractVxaInternalId(String csvInternalId) {
		String internalId = StringUtils.trimToEmpty(csvInternalId);		
		if(!internalId.isEmpty()) {
			String piid = internalId.split(" ")[1];
			piid = internalId.startsWith("LIM") ? piid : piid.substring(2);			
			return Optional.of(NumberUtils.createInteger(piid));
		}
		return Optional.empty();
	}
}

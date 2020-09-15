package com.bonlimousin.replica.service.processcsv.vxa;

import com.bonlimousin.replica.service.processcsv.CsvColumns;

public enum VxaWeightCsvColumns implements CsvColumns {
	EAR_TAG_ID(null, 0),
	HERD_ID(null, 1),
	GENDER(null, 4),	
	PATRI_MASTER_ID(null, 7),	
	MATRI_MASTER_ID(null, 9),
	
	WEIGHT_0_ACTUAL(null, 12),
	WEIGHT_0(null, 13),
	
	WEIGHT_200_ACTUAL(null, 18),
	WEIGHT_200(null, 16),
	
	WEIGHT_365_ACTUAL(null, 24),
	WEIGHT_365(null, 22),
	;

	private final String columnName;
	private final int columnIndex;
	private final boolean nullableValue;

	private VxaWeightCsvColumns(final String columnName, final int columnIndex) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.nullableValue = false;
	}
	
	private VxaWeightCsvColumns(final String columnName, final int columnIndex, boolean nullableValue) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.nullableValue = nullableValue;
	}

	public String columnName() {
		return columnName;
	}

	public int columnIndex() {
		return columnIndex;
	}
	
	public boolean nullableValue() {
		return nullableValue;
	}
}
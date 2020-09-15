package com.bonlimousin.replica.service.processcsv.pckap;

import com.bonlimousin.replica.service.processcsv.CsvColumns;

public enum CsvWeightColumns implements CsvColumns {
	EAR_TAG_ID("idonr", 2),
	MASTER_IDENTIFIER("idbonr", 5),
	HERD_ID("idbes", 1),
	COUNTRY_ID("idfor", 0, true),
	GENDER("kdkoen", 3, true),
	WEIGHT("vivagn", 12),
	TYPE("kdtillf", 13),
	;

	private final String columnName;
	private final int columnIndex;
	private final boolean nullableValue;

	private CsvWeightColumns(final String columnName, final int columnIndex) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.nullableValue = false;
	}
	
	private CsvWeightColumns(final String columnName, final int columnIndex, boolean nullableValue) {
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
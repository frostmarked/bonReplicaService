package com.bonlimousin.replica.service.processcsv;

public enum CsvWeightColumns implements CsvColumns {
	EAR_TAG_ID("idonr", 2);

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
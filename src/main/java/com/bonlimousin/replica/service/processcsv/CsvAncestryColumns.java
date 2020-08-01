package com.bonlimousin.replica.service.processcsv;

public enum CsvAncestryColumns implements CsvColumns {
	EAR_TAG_ID("idonr", 2),
	NAME("bedjurl", 58, true),
	COUNTRY_ID("idfor", 0),
	HERD_ID("idbes", 1),
	BIRTH_DATE("dafoed", 25),
	GENDER("kdkoen", 3),
	MATRI_ID("idonrm", 29, true),
	PATRI_ID("idonrf", 39, true),
	REGISTERED("daingang", 18),
	UNREGISTERED("dautgang", 17),
	;

	private final String columnName;
	private final int columnIndex;
	private final boolean nullableValue;

	private CsvAncestryColumns(final String columnName, final int columnIndex) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.nullableValue = false;
	}
	
	private CsvAncestryColumns(final String columnName, final int columnIndex, boolean nullableValue) {
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
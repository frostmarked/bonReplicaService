package com.bonlimousin.replica.service.processcsv.vxa;

import com.bonlimousin.replica.service.processcsv.CsvColumns;

public enum VxaAncestryCsvColumns implements CsvColumns {
	EAR_TAG_ID(null, 0),
	MASTER_IDENTIFIER(null, 1),
	GENDER(null, 2),
	BIRTH_DATE(null, 3),
	NAME(null, 7, true),
	
	STATUS_DATE(null, 16, true),
	STATUS(null, 17, true),
	SUBSTATUS(null, 18, true),
	
	INTERNAL_ID(null, 21, true),
	
	HORN_STATUS(null, 23, true),
	
	PATRI_INTERNAL_ID(null, 24),
	PATRI_NAME(null, 26),
	
	MATRI_MASTER_ID(null, 29),	
	MATRI_FATHER_INTERNAL_ID(null, 33),
	;

	private final String columnName;
	private final int columnIndex;
	private final boolean nullableValue;

	private VxaAncestryCsvColumns(final String columnName, final int columnIndex) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.nullableValue = false;
	}
	
	private VxaAncestryCsvColumns(final String columnName, final int columnIndex, boolean nullableValue) {
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
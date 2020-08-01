package com.bonlimousin.replica.service.processcsv;

public interface CsvColumns {
	public String name();
	
	public String columnName();

	public int columnIndex();
	
	public boolean nullableValue();
}

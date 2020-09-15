package com.bonlimousin.replica.service.processcsv;

public enum CsvFileConfig {
	PCKAP_ANCESTRY("dbo.sj_harst.csv", ',', '"'),
	PCKAP_WEIGHT("dbo.sj_vagning.csv", ',', '"'),
	PCKAP_JOURNAL("dbo.sj_journal.csv", ',', '"'),
	
	VXA_ANCESTRY("vxa_ancestry.csv", ';', '"'),
	VXA_WEIGHT("vxa_weight.csv", ';', '"')
	;
	
	private final String fileName;
	private final char separator;
	private final char quoteChar;
	private CsvFileConfig(String fileName, char separator, char quoteChar) {
		this.fileName = fileName;
		this.separator = separator;
		this.quoteChar = quoteChar;
	}
	
	public String fileName() {
		return fileName;
	}
	
	public char separator() {
		return separator;
	}
	
	public char quoteChar() {
		return quoteChar;
	}
}

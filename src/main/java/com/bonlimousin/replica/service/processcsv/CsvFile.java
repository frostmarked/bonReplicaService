package com.bonlimousin.replica.service.processcsv;

public enum CsvFile {
	ANCESTRY("dbo.sj_harst.csv"),
	WEIGHT("dbo.sj_vagning.csv"),
	JOURNAL("dbo.sj_journal.csv")
	;
	
	private final String fileName;
	private CsvFile(String fileName) {
		this.fileName = fileName;
	}
	
	public String fileName() {
		return fileName;
	}
}

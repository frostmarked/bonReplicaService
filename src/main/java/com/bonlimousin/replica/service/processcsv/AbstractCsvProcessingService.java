package com.bonlimousin.replica.service.processcsv;

import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import liquibase.util.csv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class AbstractCsvProcessingService {

    private static final Logger log = LoggerFactory.getLogger(AbstractCsvProcessingService.class);

    protected final BovineRepository bovineRepository;
    protected final BovineService bovineService;
    private final CsvFileConfig csvFileConfig;
    private final CsvColumns[] columns;

    public AbstractCsvProcessingService(BovineRepository bovineRepository, BovineService bovineService,
                                        CsvFileConfig csvFileConfig, CsvColumns[] columns) {
        this.bovineRepository = bovineRepository;
        this.bovineService = bovineService;
        this.csvFileConfig = csvFileConfig;
        this.columns = columns;
    }

    public CsvFileConfig getCsvFileConfig() {
        return this.csvFileConfig;
    }

    public int processCsvFile(SourceFileEntity sfe, boolean isDryRun)
        throws IOException {
        CsvFileConfig csvFile = getCsvFileConfig();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(sfe.getZipFile());
             ZipInputStream zis = new ZipInputStream(bais)) {
            for (ZipEntry ze; (ze = zis.getNextEntry()) != null; ) {
                if (csvFile.fileName().equals(ze.getName())) {
                    try (CSVReader csvReader = new CSVReader(new InputStreamReader(zis, StandardCharsets.UTF_8),
                        csvFile.separator(), csvFile.quoteChar())) {
                        return processCsvFile(sfe, isDryRun, csvReader);
                    }
                }
            }
        }
        return 0;
    }

    public Optional<CsvColumns> columnsExist(String[] cells) {
        return columnsExist(this.columns, cells);
    }

    public static Optional<CsvColumns> columnsExist(CsvColumns[] cols, String[] cells) {
        for (CsvColumns col : cols) {
            if (col.columnIndex() >= cells.length || (!col.nullableValue() && StringUtils.trimToEmpty(cells[col.columnIndex()]).isEmpty())) {
                return Optional.of(col);
            }
        }
        return Optional.empty();
    }

    public int processCsvFile(SourceFileEntity sfe, boolean isDryRun, CSVReader csvReader) throws IOException {
        csvReader.readNext(); // ignore header
        int rowCount = 0;
        int processedRowsCount = 0;
        for (String[] cells; (cells = csvReader.readNext()) != null; rowCount++) {
            log.debug("Process row {} of file {}", rowCount, sfe.getName());
            Optional<CsvColumns> ccOpt = columnsExist(cells);
            if (ccOpt.isPresent()) {
                if(log.isWarnEnabled()) {
                    log.warn("Row {} of file {} is column {}", rowCount, sfe.getName(), ccOpt.get().name());
                }
                continue;
            }
            if (processCsvRow(sfe, isDryRun, cells)) {
                processedRowsCount++;
            }
        }
        return processedRowsCount;
    }

    public abstract boolean processCsvRow(SourceFileEntity sfe, boolean isDryRun, String[] cells);
}

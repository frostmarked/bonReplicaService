package com.bonlimousin.replica.service.processcsv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.repository.SourceFileRepository;
import com.bonlimousin.replica.service.BovineService;

import liquibase.util.csv.CSVReader;

/**
 * Service Implementation for managing {@link SourceFileEntity}.
 */
@Service
@Transactional
public class SourceFileProcessingService {

	private final Logger log = LoggerFactory.getLogger(SourceFileProcessingService.class);

	private final SourceFileRepository sourceFileRepository;
	private final BovineRepository bovineRepository;
	private final BovineService bovineService;

	private final Executor executor;
	
	public SourceFileProcessingService(SourceFileRepository sourceFileRepository,
			@Qualifier("taskExecutor") Executor executor,
			BovineRepository bovineRepository,
			BovineService bovineService) {
		this.sourceFileRepository = sourceFileRepository;
		this.executor = executor;		
		this.bovineRepository = bovineRepository;
		this.bovineService = bovineService;
	}

	/**
	 * Check existence of sourceFile by id.
	 *
	 * @param id the id of the entity.
	 * @return answer.
	 */
	@Transactional(readOnly = true)
	public boolean exists(Long id) {
		return sourceFileRepository.existsById(id);
	}

	/**
	 * Process one sourceFile by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 * @throws IOException
	 */
	@Transactional
	public void process(Long id, boolean isRunAsync, boolean isDryRun) throws IOException {
		SourceFileEntity sfe = sourceFileRepository.getOne(id);

		CsvValidator.validateZipFile(CsvFile.ANCESTRY.fileName(), CsvAncestryColumns.values(), sfe.getZipFile());
		CsvValidator.validateZipFile(CsvFile.WEIGHT.fileName(), CsvWeightColumns.values(), sfe.getZipFile());
// TODO impl CsvValidator.validateZipFile(CsvFile.JOURNAL.fileName(), CsvJournalColumns.values(), sfe.getZipFile());

		if(!isRunAsync) {
			processCsvFiles(sfe, isDryRun);			
		} else {
			executor.execute(() -> {
				StopWatch watch = new StopWatch();
				watch.start();
				try {				
					processCsvFiles(sfe, isDryRun);					
				} catch (Exception e) {
					log.error("Processing of zip-file failed", e.getMessage(), e);
				} finally {
					watch.stop();
					log.info("Processing time of zip-file was {}", watch.getTotalTimeMillis());
				}
			});
		}
	}
	
	public void processCsvFiles(SourceFileEntity sfe, boolean isDryRun) throws IOException {
		processCsvFile(sfe, CsvFile.ANCESTRY, (reader) -> { 
			processAncestryCsvFile(sfe, reader, isDryRun);
        });
		processCsvFile(sfe, CsvFile.WEIGHT, (reader) -> { 
			processWeightCsvFile(reader, isDryRun);
        });
		// TODO impl
		/*
		processCsvFile(sfe, CsvFile.JOURNAL, (reader) -> { 
			processJournalCsvFile(reader);
        });
        */
		if(!isDryRun) {
			sfe.setProcessed(Instant.now());
			sfe.setOutcome("success"); // XXX catch and save failures as well?
			sourceFileRepository.save(sfe);
		}
	}

	public void processCsvFile(SourceFileEntity sfe, CsvFile csvFile, Consumer<CSVReader> processor)
			throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(sfe.getZipFile());
				ZipInputStream zis = new ZipInputStream(bais)) {
			for (ZipEntry ze; (ze = zis.getNextEntry()) != null;) {
				if (csvFile.fileName().equals(ze.getName())) {
					processor.accept(new CSVReader(new InputStreamReader(zis, StandardCharsets.UTF_8)));											
				}
			}
		}
	}

	public void processAncestryCsvFile(SourceFileEntity sfe, CSVReader csvReader, boolean isDryRun) {
		BovineEntity be, currbe;
		try {
			csvReader.readNext(); // ignore header	
			int rowCount = 0;
			for(String[] cells; (cells = csvReader.readNext()) != null; rowCount++) {
				if(cells.length <= CsvAncestryColumns.NAME.columnIndex()) {
					log.warn("Row {} seems empty. Only {} cells", rowCount, cells.length);
					continue;
				}
				be = new BovineEntity();
				be = CsvAncestryRowToBovineEntityConverter.convert(cells, be);
				if(be.getEarTagId() == null) {
					log.warn("(row: {}) Abort! Eartagid is missing for row", rowCount);
					continue;
				}
				if(be.getHerdId() == null) {
					log.warn("(row: {}) Abort! HerdId is missing for row", rowCount);
					continue;
				}				
				Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(be.getEarTagId());
				if(opt.isPresent()) {																								
					currbe = CsvAncestryRowToBovineEntityConverter.convert(cells, opt.get());					
					currbe.setSourceFile(sfe);
					log.debug("(row: {})(dryrun: {}) Update of existing bovine with eartagid {} and herdid {}", rowCount, isDryRun, be.getEarTagId(), be.getHerdId());
					if(!isDryRun) {
						bovineService.save(currbe);
					}														
				} else {
					be.setSourceFile(sfe);
					log.info("(row: {})(dryrun: {}) Create new bovine with eartagid {} and herdid {}", rowCount, isDryRun, be.getEarTagId(), be.getHerdId());
					if(!isDryRun) {
						bovineService.save(be);
					}
				}
			}
		} catch (IOException e) {
			log.error("Processing of ancestry-csv failed", e.getMessage(), e);
		}		
	}	
	
	public void processWeightCsvFile(CSVReader csvReader, boolean isDryRun) {
		BovineEntity be, currbe;
		try {
			csvReader.readNext(); // ignore header
			int rowCount = 0;			
			for(String[] cells; (cells = csvReader.readNext()) != null; rowCount++) {
				if(cells.length <= CsvWeightColumns.TYPE.columnIndex()) {
					log.warn("Row {} seems empty. Only {} cells", rowCount, cells.length);
					continue;
				}
				be = new BovineEntity();
				be = CsvWeightRowToBovineEntityConverter.convert(cells, be);
				if(be.getEarTagId() == null) {
					log.warn("(row: {}) Abort! Eartagid is missing for row", rowCount);
					continue;
				}
				if(be.getHerdId() == null) {
					log.warn("(row: {}) Abort! HerdId is missing for row", rowCount);
					continue;
				}				
				Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(be.getEarTagId());
				if(opt.isPresent()) {															
					currbe = CsvWeightRowToBovineEntityConverter.convert(cells, opt.get());					
					log.debug("(row: {})(dryrun: {}) Update of existing bovine with eartagid {} and herdid {}", rowCount, isDryRun, be.getEarTagId(), be.getHerdId());
					if(!isDryRun) {
						bovineService.save(currbe);
					}					
				} else {
					log.info("(row: {})(dryrun: {}) No bovine with eartagid {} and herdid {} exists. Ignore weight until present.", rowCount, isDryRun, be.getEarTagId(), be.getHerdId());
				}
			}
		} catch (IOException e) {
			log.error("Processing of weight-csv failed", e.getMessage(), e);
		}
	}
	
	public void processJournalCsvFile(CSVReader csvReader, boolean isDryRun) {
		// TODO
		throw new NotImplementedException();
	}
}

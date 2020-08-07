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

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import com.bonlimousin.replica.service.SourceFileService;

import liquibase.util.csv.CSVReader;

/**
 * Service Implementation for managing import of CVS files with cow data
 */
@Service
public class SourceFileProcessingService {

	private final Logger log = LoggerFactory.getLogger(SourceFileProcessingService.class);

	private final SourceFileService sourceFileService;
	private final BovineRepository bovineRepository;
	private final BovineService bovineService;

	private final Executor executor;
	
	public SourceFileProcessingService(SourceFileService sourceFileService,
			@Qualifier("taskExecutor") Executor executor,
			BovineRepository bovineRepository,
			BovineService bovineService) {
		this.sourceFileService = sourceFileService;
		this.executor = executor;		
		this.bovineRepository = bovineRepository;
		this.bovineService = bovineService;
	}

	/**
	 * Process one sourceFile by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 * @throws IOException
	 */
	public void process(Long id, boolean isRunAsync, boolean isDryRun) throws IOException {
		Optional<SourceFileEntity> opt = sourceFileService.findOne(id);
		if(opt.isEmpty()) {
			throw new ValidationException("Entity not found");
		}
		SourceFileEntity sfe = opt.get();
		
		CsvValidator.validateZipFile(CsvFile.ANCESTRY.fileName(), CsvAncestryColumns.values(), sfe.getZipFile());
		CsvValidator.validateZipFile(CsvFile.WEIGHT.fileName(), CsvWeightColumns.values(), sfe.getZipFile());
		CsvValidator.validateZipFile(CsvFile.JOURNAL.fileName(), CsvJournalColumns.values(), sfe.getZipFile());

		if(!isRunAsync) {
			processCsvFiles(sfe, isDryRun);			
		} else {
			executor.execute(() -> {				
				try {				
					processCsvFiles(sfe, isDryRun);					
				} catch (Exception e) {
					log.error("Processing of zip-file failed", e);
				}
			});
		}
	}
	
	public void processCsvFiles(SourceFileEntity sfe, boolean isDryRun) throws IOException {
		StopWatch watch = new StopWatch();
		watch.start();
		try {
			processCsvFile(sfe, CsvFile.ANCESTRY, cells -> processAncestryCsvLine(sfe, isDryRun, cells));
			processCsvFile(sfe, CsvFile.WEIGHT, cells -> processWeightCsvLine(isDryRun, cells));			
			processCsvFile(sfe, CsvFile.JOURNAL, cells -> processJournalCsvLine(isDryRun, cells));
			String outcome = isDryRun ? "dryrun_success" : "success";
			storeOutcome(sfe, outcome);			
		} catch(Exception e) {
			String outcome = isDryRun ? "dryrun_failure" : "failure";
			storeOutcome(sfe, outcome);
			throw e;
		} finally {
			watch.stop();
			log.info("Processing time of zip-file was {}", watch.getTotalTimeMillis());
		}
	}
	
	private void storeOutcome(SourceFileEntity sfe, String outcome) {
		sfe.setProcessed(Instant.now());
		sfe.setOutcome(outcome);
		sourceFileService.save(sfe);
	}

	private void processCsvFile(SourceFileEntity sfe, CsvFile csvFile, Consumer<String[]> processor)
			throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(sfe.getZipFile());
				ZipInputStream zis = new ZipInputStream(bais)) {
			for (ZipEntry ze; (ze = zis.getNextEntry()) != null;) {
				if (csvFile.fileName().equals(ze.getName())) {					
					try (CSVReader csvReader = new CSVReader(new InputStreamReader(zis, StandardCharsets.UTF_8))) {					
						csvReader.readNext(); // ignore header	
						int rowCount = 0;
						for(String[] cells; (cells = csvReader.readNext()) != null; rowCount++) {
							log.debug("Process row {} of file {}", rowCount, csvFile);
							processor.accept(cells);
						}
						return;
					} catch (IOException e) {
						log.error("Processing of csv-file failed", e);
					}
				}
			}
		}
	}

	public void processAncestryCsvLine(SourceFileEntity sfe, boolean isDryRun, String[] cells) {		
		BovineEntity be = CsvAncestryRowToBovineEntityConverter.convert(cells, new BovineEntity());		
		Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(be.getEarTagId());
		if(opt.isPresent()) {																								
			BovineEntity currbe = CsvAncestryRowToBovineEntityConverter.convert(cells, opt.get());					
			currbe.setSourceFile(sfe);
			log.info("Update of existing bovine with eartagid {} and herdid {}", be.getEarTagId(), be.getHerdId());
			if(!isDryRun) {
				bovineService.save(currbe);
			}														
		} else {
			be.setSourceFile(sfe);
			log.info("Create new bovine with eartagid {} and herdid {}", be.getEarTagId(), be.getHerdId());
			if(!isDryRun) {
				bovineService.save(be);
			}
		}
	}
	
	public void processWeightCsvLine(boolean isDryRun, String[] cells) {
		BovineEntity be = CsvWeightRowToBovineEntityConverter.convert(cells, new BovineEntity());						
		Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(be.getEarTagId());
		if(opt.isPresent()) {															
			BovineEntity currbe = CsvWeightRowToBovineEntityConverter.convert(cells, opt.get());					
			log.info("Update of existing bovine with eartagid {} and herdid {}", be.getEarTagId(), be.getHerdId());
			if(!isDryRun) {
				bovineService.save(currbe);
			}					
		} else {
			log.info("No bovine with eartagid {} and herdid {} exists. Ignore weight until present.", be.getEarTagId(), be.getHerdId());
		}
	}
	
	public void processJournalCsvLine(boolean isDryRun, String[] cells) {
		// TODO impl
	}
}

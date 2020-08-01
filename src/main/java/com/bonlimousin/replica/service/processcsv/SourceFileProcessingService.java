package com.bonlimousin.replica.service.processcsv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
	
	private final BovineService bovineService;

	private final Executor executor;

	public SourceFileProcessingService(SourceFileRepository sourceFileRepository,
			@Qualifier("taskExecutor") Executor executor, BovineService bovineService) {
		this.sourceFileRepository = sourceFileRepository;
		this.executor = executor;
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
	public void process(Long id) throws IOException {
		SourceFileEntity sfe = sourceFileRepository.getOne(id);

		CsvValidator.validateZipFile(CsvFile.ANCESTRY.fileName(), CsvAncestryColumns.values(), sfe.getZipFile());
		CsvValidator.validateZipFile(CsvFile.WEIGHT.fileName(), CsvWeightColumns.values(), sfe.getZipFile());
		CsvValidator.validateZipFile(CsvFile.JOURNAL.fileName(), CsvJournalColumns.values(), sfe.getZipFile());

		executor.execute(() -> {
			StopWatch watch = new StopWatch();
			watch.start();
			try {				
				processCsvFile(sfe, CsvFile.ANCESTRY, (reader) -> { 
					processAncestryCsvFile(reader);
		        });
				processCsvFile(sfe, CsvFile.WEIGHT, (reader) -> { 
					processWeightCsvFile(reader);
		        });
				processCsvFile(sfe, CsvFile.JOURNAL, (reader) -> { 
					processJournalCsvFile(reader);
		        });
			} catch (Exception e) {
				log.error("Processing of zip-file failed", e.getMessage(), e);
			} finally {
				watch.stop();
				log.info("Processing time of zip-file was {}", watch.getTotalTimeMillis());
			}
		});
	}

	public void processCsvFile(SourceFileEntity sfe, CsvFile csvFile, Consumer<CSVReader> processor)
			throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(sfe.getZipFile());
				ZipInputStream zis = new ZipInputStream(bais)) {
			for (ZipEntry ze; (ze = zis.getNextEntry()) != null;) {
				if (csvFile.fileName().equals(ze.getName())) {
					try (CSVReader csvReader = new CSVReader(new InputStreamReader(zis, StandardCharsets.UTF_8))) {						
						processor.accept(csvReader);						
					}
				}
			}
		}
	}

	public void processAncestryCsvFile(CSVReader csvReader) {
		BovineEntity be = new BovineEntity();
		try {
			csvReader.readNext(); // ignore header
			String[] cells;			
			while((cells = csvReader.readNext()) != null) {				
				be = CsvRowToBovineEntityConverter.convert(cells, be);
				// TODO maybe use some auditing lib?
				Optional<BovineEntity> opt = bovineService.findOne(be.getId());
				if(opt.isPresent()) {
					log.debug("Update: Bovine {} already exists", be.getId());
					
				} else {
					log.debug("Create: Bovine {} does not exist", be.getId());
				}
				throw new NotImplementedException();
			}
		} catch (IOException e) {
			log.error("Processing of ancestry-csv failed", e.getMessage(), e);
		}		
	}	
	
	public void processWeightCsvFile(CSVReader csvReader) {
		// TODO
		throw new NotImplementedException();
	}
	
	public void processJournalCsvFile(CSVReader csvReader) {
		// TODO
		throw new NotImplementedException();
	}
}

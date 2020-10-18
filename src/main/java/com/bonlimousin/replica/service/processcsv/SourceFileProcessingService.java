package com.bonlimousin.replica.service.processcsv;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executor;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.service.SourceFileService;
import com.bonlimousin.replica.service.processcsv.pckap.PcKapAncestryCsvProcessingService;
import com.bonlimousin.replica.service.processcsv.pckap.PcKapWeightCsvProcessingService;
import com.bonlimousin.replica.service.processcsv.vxa.VxaAncestryCsvProcessingService;
import com.bonlimousin.replica.service.processcsv.vxa.VxaWeightCsvProcessingService;

/**
 * Service Implementation for managing import of CVS files with cow data
 */
@Service
public class SourceFileProcessingService {

	private final Logger log = LoggerFactory.getLogger(SourceFileProcessingService.class);

	private final SourceFileService sourceFileService;

	private final PcKapAncestryCsvProcessingService pcKapAncestryCsvProcessingService;
	private final PcKapWeightCsvProcessingService pcKapWeightCsvProcessingService;

	private final VxaAncestryCsvProcessingService vxaAncestryCsvProcessingService;
	private final VxaWeightCsvProcessingService vxaWeightCsvProcessingService;

	private final Executor executor;

	public SourceFileProcessingService(SourceFileService sourceFileService,
			@Qualifier("taskExecutor") Executor executor,
			PcKapAncestryCsvProcessingService pcKapAncestryCsvProcessingService,
			PcKapWeightCsvProcessingService pcKapWeightCsvProcessingService,
			VxaAncestryCsvProcessingService vxaAncestryCsvProcessingService,
			VxaWeightCsvProcessingService vxaWeightCsvProcessingService) {
		this.sourceFileService = sourceFileService;
		this.executor = executor;

		this.pcKapAncestryCsvProcessingService = pcKapAncestryCsvProcessingService;
		this.pcKapWeightCsvProcessingService = pcKapWeightCsvProcessingService;

		this.vxaAncestryCsvProcessingService = vxaAncestryCsvProcessingService;
		this.vxaWeightCsvProcessingService = vxaWeightCsvProcessingService;
	}

	public int process(Long id, boolean isRunAsync, boolean isDryRun) throws IOException {
		Optional<SourceFileEntity> opt = sourceFileService.findOne(id);
		if(!opt.isPresent()) {
			throw new ValidationException("Entity not found");
		}
		SourceFileEntity sfe = opt.get();
		if(!isRunAsync) {
			return processCsvFiles(sfe, isDryRun);
		} else {
			executor.execute(() -> {
				try {
					processCsvFiles(sfe, isDryRun);
				} catch (Exception e) {
					log.error("Processing of zip-file failed", e);
				}
			});
			return -1;
		}
	}

	public int processCsvFiles(SourceFileEntity sfe, boolean isDryRun) throws IOException {
		StopWatch watch = new StopWatch();
		watch.start();
		int processedRows = 0;
		try {
            processedRows += pcKapAncestryCsvProcessingService.processCsvFile(sfe, isDryRun);
            processedRows += pcKapWeightCsvProcessingService.processCsvFile(sfe, isDryRun);
			// impl journal processing??

            processedRows += vxaAncestryCsvProcessingService.processCsvFile(sfe, isDryRun);
            processedRows += vxaWeightCsvProcessingService.processCsvFile(sfe, isDryRun);
			// impl blup processing??

			storeOutcome(sfe, isDryRun ? "dryrun_success" : "success");
		} catch(Exception e) {
			storeOutcome(sfe, isDryRun ? "dryrun_failure" : "failure");
			throw e;
		} finally {
			watch.stop();
			log.info("Processing time of zip-file was {}", watch.getTotalTimeMillis());
		}
		return processedRows;
	}

	private void storeOutcome(SourceFileEntity sfe, String outcome) {
		sfe.setProcessed(Instant.now());
		sfe.setOutcome(outcome);
		sourceFileService.save(sfe);
	}
}

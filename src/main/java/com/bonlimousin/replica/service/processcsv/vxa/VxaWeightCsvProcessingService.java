package com.bonlimousin.replica.service.processcsv.vxa;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import com.bonlimousin.replica.service.processcsv.CsvFileConfig;
import com.bonlimousin.replica.service.processcsv.CsvProcessingService;
import com.bonlimousin.replica.service.processcsv.CsvProcessingUtils;

import liquibase.util.csv.CSVReader;

@Service
public class VxaWeightCsvProcessingService implements CsvProcessingService {

	private static final Logger log = LoggerFactory.getLogger(VxaWeightCsvProcessingService.class);	
	
	private final BovineRepository bovineRepository;
	private final BovineService bovineService;

	public VxaWeightCsvProcessingService(BovineRepository bovineRepository,
			BovineService bovineService) {		
		this.bovineRepository = bovineRepository;
		this.bovineService = bovineService;		
	}
	
	@Override
	public CsvFileConfig getCsvFileConfig() {
		return CsvFileConfig.VXA_WEIGHT;
	}
	
	@Override
	public void processCsvFile(SourceFileEntity sfe, boolean isDryRun, CSVReader csvReader) throws IOException {
		csvReader.readNext(); // ignore header	
		int rowCount = 0;
		for(String[] cells; (cells = csvReader.readNext()) != null; rowCount++) {
			log.debug("Process row {} of file {}", rowCount, sfe.getName());
				
			BovineEntity be = populateBovineEntity(cells, new BovineEntity());						
			Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(be.getEarTagId());
			if(opt.isPresent()) {															
				BovineEntity currbe = populateBovineEntity(cells, opt.get());					
				log.info("Update of existing bovine with eartagid {} and herdid {}", be.getEarTagId(), be.getHerdId());
				if(!isDryRun) {
					bovineService.save(currbe);
				}					
			} else {
				log.info("No bovine with eartagid {} and herdid {} exists. Ignore weight until present.", be.getEarTagId(), be.getHerdId());
			}
		}
	}
	
	public BovineEntity populateBovineEntity(String[] cells, BovineEntity be) {
		CsvProcessingUtils.createId(cells, VxaWeightCsvColumns.EAR_TAG_ID).ifPresent(be::setEarTagId);
		
		String csvWeight0 = StringUtils.trimToNull(cells[VxaWeightCsvColumns.WEIGHT_0.columnIndex()]);
		be.setWeight0(NumberUtils.createInteger(csvWeight0));
		
		String csvWeight200 = StringUtils.trimToNull(cells[VxaWeightCsvColumns.WEIGHT_200.columnIndex()]);
		be.setWeight200(NumberUtils.createInteger(csvWeight200));
		
		String csvWeight365 = StringUtils.trimToNull(cells[VxaWeightCsvColumns.WEIGHT_365.columnIndex()]);
		be.setWeight365(NumberUtils.createInteger(csvWeight365));
		
		return be;
	}
}

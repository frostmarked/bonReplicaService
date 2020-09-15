package com.bonlimousin.replica.service.processcsv.vxa;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.HornStatus;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import com.bonlimousin.replica.service.processcsv.CsvFileConfig;
import com.bonlimousin.replica.service.processcsv.CsvProcessingService;
import com.bonlimousin.replica.service.processcsv.CsvProcessingUtils;

import liquibase.util.csv.CSVReader;

@Service
public class VxaAncestryCsvProcessingService implements CsvProcessingService {

	private static final Logger log = LoggerFactory.getLogger(VxaAncestryCsvProcessingService.class);	
	
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	private final BovineRepository bovineRepository;
	private final BovineService bovineService;
	
	private final Map<Integer, Integer> internalIdEarTagIdMap;

	public VxaAncestryCsvProcessingService(BovineRepository bovineRepository,
			BovineService bovineService) {		
		this.bovineRepository = bovineRepository;
		this.bovineService = bovineService;
		
		this.internalIdEarTagIdMap = new HashMap<>();
	}
	
	@Override
	public CsvFileConfig getCsvFileConfig() {
		return CsvFileConfig.VXA_ANCESTRY;
	}
	
	@Override
	public void processCsvFile(SourceFileEntity sfe, boolean isDryRun, CSVReader csvReader) throws IOException {
		csvReader.readNext(); // ignore header	
		int rowCount = 0;
		for(String[] cells; (cells = csvReader.readNext()) != null; rowCount++) {
			log.debug("Process row {} of file {}", rowCount, sfe.getName());
			
			extractInternalIdEarTagIdMapEntry(cells)
				.ifPresent(ent -> internalIdEarTagIdMap.put(ent.getKey(), ent.getValue()));
			BovineEntity be = populateBovineEntity(cells, new BovineEntity());		
			Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(be.getEarTagId());
			if(opt.isPresent()) {																								
				BovineEntity currbe = populateBovineEntity(cells, opt.get());					
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
	}
	
	public BovineEntity populateBovineEntity(String[] cells, BovineEntity be) {
		CsvProcessingUtils.createId(cells, VxaAncestryCsvColumns.EAR_TAG_ID).ifPresent(be::setEarTagId);

		String csvMasterIdentifier = cells[VxaAncestryCsvColumns.MASTER_IDENTIFIER.columnIndex()];
		be.setMasterIdentifier(csvMasterIdentifier);

		String[] midParts = csvMasterIdentifier.split("-");

		String csvCountry = midParts.length == 4 ? midParts[0] : "";
		be.setCountry(StringUtils.lowerCase(csvCountry));

		int herdId = Integer.parseInt(midParts[1]);
		be.setHerdId(herdId);

		String csvName = cells[VxaAncestryCsvColumns.NAME.columnIndex()];
		be.setName(csvName);

		String csvBirthDate = cells[VxaAncestryCsvColumns.BIRTH_DATE.columnIndex()];
		be.setBirthDate(LocalDate.parse(csvBirthDate, DTF).atStartOfDay(ZoneId.of("Europe/Stockholm")).toInstant());

		String csvGender = cells[VxaAncestryCsvColumns.GENDER.columnIndex()];
		be.setGender("2".equals(csvGender) ? Gender.HEIFER : Gender.BULL);

		String csvHornStatus = cells[VxaAncestryCsvColumns.HORN_STATUS.columnIndex()];
		HornStatus hornStatus;
		switch (csvHornStatus) {
		case "P":
			hornStatus = HornStatus.POLLED;
			break;
		case "H":
			hornStatus = HornStatus.HORNED;
			break;
		case "D":
			hornStatus = HornStatus.DEHORNED;
			break;
		case "S":
			hornStatus = HornStatus.SCURS;
			break;
		default:
			hornStatus = HornStatus.UNKNOWN;
			break;
		}
		be.setHornStatus(hornStatus);

		BovineStatus bovineStatus;
		String csvStatus = StringUtils.trimToEmpty(cells[VxaAncestryCsvColumns.STATUS.columnIndex()]);
		if (!csvStatus.isEmpty()) {
			switch (csvHornStatus) {
			case "SÅLD LIV":
				bovineStatus = BovineStatus.SOLD;
				break;
			case "SLAKT/SLAKTDJUR":
				bovineStatus = BovineStatus.MEAT;
				break;
			case "UTGÅNGEN":
			case "FÖRLOSSN.SVÅR":
			case "OLYCKSFALL":
			case "ANNAN SJUKDOM":
			default:
				bovineStatus = BovineStatus.UNKNOWN;
				break;
			}
		} else {
			bovineStatus = BovineStatus.ON_FARM;
		}
		be.setBovineStatus(bovineStatus);

		Optional<Integer> optPiid = CsvProcessingUtils.extractVxaInternalId(cells[VxaAncestryCsvColumns.PATRI_INTERNAL_ID.columnIndex()]);
		if(optPiid.isPresent()) {
			Integer patriInternalId = optPiid.get();
			Integer patriEarTagId = this.internalIdEarTagIdMap.get(patriInternalId);			
			// negative if the father is not present yet, handle later...
			Integer patriId = patriEarTagId != null ? patriEarTagId : (patriInternalId * -1);
			be.setPatriId(patriId);			
		}		

		String csvMatriInternalId = StringUtils.trimToEmpty(cells[VxaAncestryCsvColumns.MATRI_MASTER_ID.columnIndex()]);
		String[] mmidParts = csvMatriInternalId.split("-");
		int matriEarTagId = NumberUtils.createInteger(mmidParts[2]);
		be.setMatriId(matriEarTagId);

		return be;
	}

	private static Optional<Map.Entry<Integer, Integer>> extractInternalIdEarTagIdMapEntry(String[] cells) {
		String internalId = StringUtils.trimToEmpty(cells[VxaAncestryCsvColumns.INTERNAL_ID.columnIndex()]);
		String earTagId = StringUtils.trimToEmpty(cells[VxaAncestryCsvColumns.EAR_TAG_ID.columnIndex()]);
		if (!internalId.isEmpty() && !earTagId.isEmpty()) {
			try {
				return Optional.of(Map.entry(Integer.parseInt(internalId), Integer.parseInt(earTagId)));
			} catch (NumberFormatException e) {
				// ignore missing data
			}
		}
		return Optional.empty();
	}
}

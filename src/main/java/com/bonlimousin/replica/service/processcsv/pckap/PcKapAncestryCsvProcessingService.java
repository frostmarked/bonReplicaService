package com.bonlimousin.replica.service.processcsv.pckap;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
public class PcKapAncestryCsvProcessingService implements CsvProcessingService {

	private static final Logger log = LoggerFactory.getLogger(PcKapAncestryCsvProcessingService.class);

	private final BovineRepository bovineRepository;
	private final BovineService bovineService;

	public PcKapAncestryCsvProcessingService(BovineRepository bovineRepository, BovineService bovineService) {
		this.bovineRepository = bovineRepository;
		this.bovineService = bovineService;
	}

	@Override
	public CsvFileConfig getCsvFileConfig() {
		return CsvFileConfig.PCKAP_ANCESTRY;
	}

	@Override
	public void processCsvFile(SourceFileEntity sfe, boolean isDryRun, CSVReader csvReader) throws IOException {
		csvReader.readNext(); // ignore header
		int rowCount = 0;
		for (String[] cells; (cells = csvReader.readNext()) != null; rowCount++) {
			log.debug("Process row {} of file {}", rowCount, sfe.getName());

			BovineEntity be = populateBovineEntity(cells, new BovineEntity());
			Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(be.getEarTagId());
			if (opt.isPresent()) {
				BovineEntity currbe = populateBovineEntity(cells, opt.get());
				currbe.setSourceFile(sfe);
				log.info("Update of existing bovine with eartagid {} and herdid {}",
						be.getEarTagId(), be.getHerdId());
				if (!isDryRun) {
					bovineService.save(currbe);
				}
			} else {
				be.setSourceFile(sfe);
				log.info("Create new bovine with eartagid {} and herdid {}", be.getEarTagId(),
						be.getHerdId());
				if (!isDryRun) {
					bovineService.save(be);
				}
			}
		}
	}

	public BovineEntity populateBovineEntity(String[] cells, BovineEntity be) {
		CsvProcessingUtils.createId(cells, CsvAncestryColumns.EAR_TAG_ID).ifPresent(be::setEarTagId);		

		String csvMasterIdentifier = cells[CsvAncestryColumns.MASTER_IDENTIFIER.columnIndex()];
		be.setMasterIdentifier(csvMasterIdentifier);

		String csvName = cells[CsvAncestryColumns.NAME.columnIndex()];
		be.setName(csvName);

		String csvCountry = cells[CsvAncestryColumns.COUNTRY_ID.columnIndex()];
		be.setCountry(StringUtils.lowerCase(csvCountry));

		CsvProcessingUtils.createId(cells, CsvAncestryColumns.HERD_ID).ifPresent(be::setHerdId);		

		String csvBirthDate = cells[CsvAncestryColumns.BIRTH_DATE.columnIndex()];
		be.setBirthDate(LocalDate.parse(csvBirthDate).atStartOfDay(ZoneId.of("Europe/Stockholm")).toInstant());

		String csvGender = cells[CsvAncestryColumns.GENDER.columnIndex()];
		be.setGender("2".equals(csvGender) ? Gender.HEIFER : Gender.BULL);

		CsvProcessingUtils.createId(cells, CsvAncestryColumns.MATRI_ID).ifPresentOrElse(be::setMatriId, () -> be.setMatriId(0));

		CsvProcessingUtils.createId(cells, CsvAncestryColumns.PATRI_ID).ifPresentOrElse(be::setPatriId, () -> be.setPatriId(0));

		be.setHornStatus(HornStatus.UNKNOWN); // TODO if exists

		String csvUnregistered = cells[CsvAncestryColumns.UNREGISTERED.columnIndex()];
		if (StringUtils.trimToNull(csvUnregistered) != null) {
			be.setBovineStatus(BovineStatus.UNKNOWN);
			// TODO unregistered date only tells us that the cow
			// has left the farm. NOT why it left the farm.
			// can be several options... dig deeper
		} else {
			be.setBovineStatus(BovineStatus.ON_FARM);
		}

		return be;
	}
}

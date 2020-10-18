package com.bonlimousin.replica.service.processcsv.vxa;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.HornStatus;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import com.bonlimousin.replica.service.processcsv.AbstractAncestryCsvProcessingService;
import com.bonlimousin.replica.service.processcsv.CsvFileConfig;
import com.bonlimousin.replica.service.processcsv.CsvProcessingUtils;
import liquibase.util.csv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VxaAncestryCsvProcessingService extends AbstractAncestryCsvProcessingService {

	private static final Logger log = LoggerFactory.getLogger(VxaAncestryCsvProcessingService.class);
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final Map<Integer, Integer> internalIdEarTagIdMap;

	public VxaAncestryCsvProcessingService(BovineRepository bovineRepository,
			BovineService bovineService) {
	    super(bovineRepository, bovineService, CsvFileConfig.VXA_ANCESTRY, VxaAncestryCsvColumns.values());
		this.internalIdEarTagIdMap = new HashMap<>();
	}

	public void addInternalIdEarTagIdMapping(int internalId, int earTagId) {
        this.internalIdEarTagIdMap.put(internalId, earTagId);
    }

    public Optional<Integer> getEarTagIdFromMapping(int internalId) {
        return Optional.ofNullable(this.internalIdEarTagIdMap.get(internalId));
    }

    @Override
    public int processCsvFile(SourceFileEntity sfe, boolean isDryRun, CSVReader csvReader) throws IOException {
	    this.internalIdEarTagIdMap.clear();
        return super.processCsvFile(sfe, isDryRun, csvReader);
    }

    @Override
    public boolean processCsvRow(SourceFileEntity sfe, boolean isDryRun, String[] cells) {
        extractInternalIdEarTagIdMapEntry(cells)
            .ifPresent(ent -> this.addInternalIdEarTagIdMapping(ent.getKey(), ent.getValue()));
        return super.processCsvRow(sfe, isDryRun, cells);
    }

    @Override
	public BovineEntity populateBovineEntity(String[] cells, BovineEntity be) {
		CsvProcessingUtils.createId(cells, VxaAncestryCsvColumns.EAR_TAG_ID).ifPresent(be::setEarTagId);

		String csvMasterIdentifier = cells[VxaAncestryCsvColumns.MASTER_IDENTIFIER.columnIndex()];
		be.setMasterIdentifier(csvMasterIdentifier);

		String[] midParts = csvMasterIdentifier.split("-");

		String csvCountry = midParts.length == 4 ? midParts[0] : "??";
		be.setCountry(StringUtils.lowerCase(csvCountry));

		if(midParts.length > 1 && NumberUtils.isParsable(midParts[1])) {
            be.setHerdId(Integer.parseInt(midParts[1]));
        } else {
		    log.warn("EarTagId {} is missing herdId in masterid{}", be.getEarTagId(), csvMasterIdentifier);
            be.setHerdId(0);
        }

		String csvName = cells[VxaAncestryCsvColumns.NAME.columnIndex()];
		be.setName(csvName);

		String csvBirthDate = cells[VxaAncestryCsvColumns.BIRTH_DATE.columnIndex()];
        try {
            be.setBirthDate(LocalDate.parse(csvBirthDate, DTF).atStartOfDay(ZoneId.of("Europe/Stockholm")).toInstant());
        } catch (Exception e) {
            log.warn("EarTagId {} has faulty birthdate {}", be.getEarTagId(), csvBirthDate);
            be.setBirthDate(Instant.MIN);
        }

        String csvGender = cells[VxaAncestryCsvColumns.GENDER.columnIndex()];
		be.setGender("2".equals(csvGender) ? Gender.HEIFER : Gender.BULL);

        String csvHornStatus = cells[VxaAncestryCsvColumns.HORN_STATUS.columnIndex()];
		be.setHornStatus(extractHornStatus(csvHornStatus));

        String csvStatus = StringUtils.trimToEmpty(cells[VxaAncestryCsvColumns.STATUS.columnIndex()]);
		be.setBovineStatus(extractBovineStatus(csvStatus));

		Optional<Integer> optPiid = CsvProcessingUtils.extractVxaInternalId(cells[VxaAncestryCsvColumns.PATRI_INTERNAL_ID.columnIndex()]);
		if(optPiid.isPresent()) {
			int patriInternalId = optPiid.get();
			Optional<Integer> optPatriEarTagId = this.getEarTagIdFromMapping(patriInternalId);
			// negative if the father is not present yet, handle later...
			int patriId = optPatriEarTagId.orElseGet(() -> (patriInternalId * -1));
			be.setPatriId(patriId);
		} else {
            log.warn("PatriId missing for {}", be.getEarTagId());
            be.setPatriId(0);
        }

		String csvMatriInternalId = StringUtils.trimToEmpty(cells[VxaAncestryCsvColumns.MATRI_MASTER_ID.columnIndex()]);
		String[] mmidParts = csvMatriInternalId.split("-");
		if(mmidParts.length > 2 && NumberUtils.isParsable(mmidParts[2])) {
            be.setMatriId(Integer.parseInt(mmidParts[2]));
        } else {
		    log.warn("MatriId has illegal format {} for {}", csvMatriInternalId, be.getEarTagId());
            be.setMatriId(0);
        }
		return be;
	}

    protected static HornStatus extractHornStatus(String csvHornStatus) {
        switch (csvHornStatus) {
            case "P":
                return HornStatus.POLLED;
            case "H":
                return HornStatus.HORNED;
            case "D":
                return HornStatus.DEHORNED;
            case "S":
                return HornStatus.SCURS;
            default:
                return HornStatus.UNKNOWN;
        }
    }

    protected static BovineStatus extractBovineStatus(String csvStatus) {
        if (!csvStatus.isEmpty()) {
            switch (csvStatus) {
                case "SÅLD LIV":
                    return BovineStatus.SOLD;
                case "SLAKT/SLAKTDJUR":
                    return BovineStatus.MEAT;
                case "UTGÅNGEN":
                case "FÖRLOSSN.SVÅR":
                case "OLYCKSFALL":
                case "ANNAN SJUKDOM":
                default:
                    return BovineStatus.UNKNOWN;
            }
        } else {
            return BovineStatus.ON_FARM;
        }
    }

	protected static Optional<Map.Entry<Integer, Integer>> extractInternalIdEarTagIdMapEntry(String[] cells) {
		String internalId = StringUtils.trimToEmpty(cells[VxaAncestryCsvColumns.INTERNAL_ID.columnIndex()]);
		String earTagId = StringUtils.trimToEmpty(cells[VxaAncestryCsvColumns.EAR_TAG_ID.columnIndex()]);
		if (!internalId.isEmpty() && !earTagId.isEmpty()) {
			try {
			    Integer intId = Integer.parseInt(internalId);
			    Integer etId = Integer.parseInt(earTagId);
			    return Optional.of(new AbstractMap.SimpleImmutableEntry<>(intId, etId));
			} catch (NumberFormatException e) {
				// ignore missing data
			}
		}
		return Optional.empty();
	}
}

package com.bonlimousin.replica.service.processcsv.pckap;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.HornStatus;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import com.bonlimousin.replica.service.processcsv.AbstractAncestryCsvProcessingService;
import com.bonlimousin.replica.service.processcsv.CsvFileConfig;
import com.bonlimousin.replica.service.processcsv.CsvProcessingUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class PcKapAncestryCsvProcessingService extends AbstractAncestryCsvProcessingService {

    public PcKapAncestryCsvProcessingService(BovineRepository bovineRepository, BovineService bovineService) {
        super(bovineRepository, bovineService, CsvFileConfig.PCKAP_ANCESTRY, CsvAncestryColumns.values());
    }

    @Override
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

        be.setMatriId(CsvProcessingUtils.createId(cells, CsvAncestryColumns.MATRI_ID).orElse(0));
        be.setPatriId(CsvProcessingUtils.createId(cells, CsvAncestryColumns.PATRI_ID).orElse(0));

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

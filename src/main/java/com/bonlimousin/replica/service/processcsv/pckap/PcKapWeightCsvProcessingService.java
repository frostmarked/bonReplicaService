package com.bonlimousin.replica.service.processcsv.pckap;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import com.bonlimousin.replica.service.processcsv.AbstractWeightCsvProcessingService;
import com.bonlimousin.replica.service.processcsv.CsvFileConfig;
import com.bonlimousin.replica.service.processcsv.CsvProcessingUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service
public class PcKapWeightCsvProcessingService extends AbstractWeightCsvProcessingService {

    public PcKapWeightCsvProcessingService(BovineRepository bovineRepository,
                                           BovineService bovineService) {
        super(bovineRepository, bovineService, CsvFileConfig.PCKAP_WEIGHT, CsvWeightColumns.values());
    }

    @Override
    public BovineEntity populateBovineEntity(String[] cells, BovineEntity be) {
        CsvProcessingUtils.createId(cells, CsvWeightColumns.EAR_TAG_ID).ifPresent(be::setEarTagId);

        String csvWeight = cells[CsvWeightColumns.WEIGHT.columnIndex()];
        Integer weight = NumberUtils.createInteger(StringUtils.replace(csvWeight, ".0", ""));

        String csvType = cells[CsvWeightColumns.TYPE.columnIndex()];
        switch (csvType) {
            case "F":
                be.setWeight0(weight);
                break;
            case "W200": // TODO where are the 200 days weight?!
                be.setWeight200(weight);
                break;
            case "W365": // TODO where are the 365 days weight?!
                be.setWeight365(weight);
                break;
            default:
                break;
        }

        return be;
    }
}

package com.bonlimousin.replica.service.processcsv.vxa;

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
public class VxaWeightCsvProcessingService extends AbstractWeightCsvProcessingService {

    public VxaWeightCsvProcessingService(BovineRepository bovineRepository,
                                         BovineService bovineService) {
        super(bovineRepository, bovineService, CsvFileConfig.VXA_WEIGHT, VxaWeightCsvColumns.values());
    }

    @Override
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

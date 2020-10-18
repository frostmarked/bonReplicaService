package com.bonlimousin.replica.service.processcsv;

import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.service.BovineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class AbstractWeightCsvProcessingService extends AbstractCsvProcessingService {

    protected static final Logger log = LoggerFactory.getLogger(AbstractWeightCsvProcessingService.class);

    public AbstractWeightCsvProcessingService(BovineRepository bovineRepository, BovineService bovineService,
                                              CsvFileConfig csvFileConfig, CsvColumns[] columns) {
        super(bovineRepository, bovineService, csvFileConfig, columns);
    }

    @Override
    public boolean processCsvRow(SourceFileEntity sfe, boolean isDryRun, String[] cells) {
        BovineEntity be = populateBovineEntity(cells, new BovineEntity());
        Optional<BovineEntity> opt = bovineRepository.findOneByEarTagId(be.getEarTagId());
        if (opt.isPresent()) {
            BovineEntity currbe = populateBovineEntity(cells, opt.get());
            log.info("Update of existing bovine with eartagid {} and herdid {}", be.getEarTagId(), be.getHerdId());
            if (!isDryRun) {
                bovineService.save(currbe);
            }
            return true;
        } else {
            log.info("No bovine with eartagid {} and herdid {} exists. Ignore weight until present.", be.getEarTagId(), be.getHerdId());
            return false;
        }
    }

    public abstract BovineEntity populateBovineEntity(String[] cells, BovineEntity be);

}

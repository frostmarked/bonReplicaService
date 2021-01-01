package com.bonlimousin.replica.service.processcsv.vxa;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.service.processcsv.AbstractCsvProcessingService;
import com.bonlimousin.replica.service.processcsv.CsvColumns;
import com.bonlimousin.replica.service.processcsv.CsvFileConfig;
import liquibase.util.csv.CSVReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = BonReplicaServiceApp.class)
class VxaWeightCsvProcessingServiceTest {

	private static final String TEST_CSV_2916 = "src/test/resources/fixtures/vxa/csv/vxa_weight_2916.csv";
    private static final String TEST_CSV_20210101 = "src/test/resources/fixtures/vxa/csv/vxa_weight20210101.csv";

	@Autowired
	private VxaWeightCsvProcessingService service;

	@Test
    void populate2916() throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(TEST_CSV_2916), CsvFileConfig.VXA_WEIGHT.separator(),
            CsvFileConfig.VXA_ANCESTRY.quoteChar());
        csvReader.readNext(); // ignore header
        BovineEntity be = service.populateBovineEntity(csvReader.readNext(), new BovineEntity());
        assertEquals(2916, be.getEarTagId());
        assertEquals(35, be.getWeight0());
        assertEquals(274, be.getWeight200());
        assertNull(be.getWeight365());

    }

    @Test
    void randomChecks() throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(TEST_CSV_20210101), CsvFileConfig.VXA_WEIGHT.separator(),
            CsvFileConfig.VXA_ANCESTRY.quoteChar());

        Map<Integer, Boolean> checkMap = new HashMap<>();
        checkMap.put(2931, false);
        int rowCount = 0;
        for (String[] cells; (cells = csvReader.readNext()) != null; rowCount++) {
            Optional<CsvColumns> ccOpt = AbstractCsvProcessingService.columnsExist(VxaWeightCsvColumns.values(), cells);
            if (!ccOpt.isPresent()) {
                BovineEntity be = service.populateBovineEntity(cells, new BovineEntity());
                if (be.getEarTagId() == 2931) {
                    checkMap.put(2931, true);
                    assertThat(be.getWeight0()).isEqualTo(43);
                    assertThat(be.getWeight200()).isEqualTo(350);
                    assertThat(be.getWeight365()).isNull();
                }
            }
        }
        assertThat(rowCount).isEqualTo(877);
        long c = checkMap.values().stream().filter(v -> v == false).count();
        assertThat(c).isEqualTo(0);
    }
}

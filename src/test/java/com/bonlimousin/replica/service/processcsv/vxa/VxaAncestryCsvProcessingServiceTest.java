package com.bonlimousin.replica.service.processcsv.vxa;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.SourceFileEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.service.SourceFileService;
import com.bonlimousin.replica.service.processcsv.CsvFileConfig;
import com.bonlimousin.replica.service.processcsv.ProcessCSVTestUtils;
import liquibase.util.csv.CSVReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = BonReplicaServiceApp.class)
class VxaAncestryCsvProcessingServiceTest {

    private static final String TEST_CSV_2688 = "src/test/resources/fixtures/vxa/csv/vxa_ancestry_2688.csv";
	private static final String TEST_CSV_2916 = "src/test/resources/fixtures/vxa/csv/vxa_ancestry_2916.csv";
    private static final String TEST_VXA_ZIP = "src/test/resources/fixtures/vxa/vxa_se015112.zip";

    @Autowired
    private SourceFileService sourceFileService;

	@Autowired
	private VxaAncestryCsvProcessingService service;

    @Test
    @Transactional
    void getEarTagIdByInternalId() throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(TEST_CSV_2688), CsvFileConfig.VXA_ANCESTRY.separator(),
            CsvFileConfig.VXA_ANCESTRY.quoteChar());
        csvReader.readNext(); // ignore header
        String[] cells = csvReader.readNext();
        service.processCsvRow(new SourceFileEntity(), true, cells);
        Optional<Integer> opt = service.getEarTagIdFromMapping(84904);
        assertThat(opt).isNotEmpty().contains(2688);
    }

	@Test
    @Transactional
	void populate2916() throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(TEST_CSV_2916), CsvFileConfig.VXA_ANCESTRY.separator(),
				CsvFileConfig.VXA_ANCESTRY.quoteChar());
		csvReader.readNext(); // ignore header
        service.addInternalIdEarTagIdMapping(84904, 2688);
		BovineEntity be = service.populateBovineEntity(csvReader.readNext(), new BovineEntity());
		assertEquals(2916, be.getEarTagId());
		assertEquals(15112, be.getHerdId());
		assertEquals(2713, be.getMatriId());
		assertEquals(Gender.HEIFER, be.getGender());
		assertEquals(BovineStatus.ON_FARM, be.getBovineStatus());
		assertEquals(2019, LocalDateTime.ofInstant(be.getBirthDate(), ZoneOffset.UTC).get(ChronoField.YEAR));
		assertEquals(2688, be.getPatriId());
	}

    @Test
    @Transactional
    void dryrunLotsOfRecords() throws IOException {
        SourceFileEntity sfe = sourceFileService.save(ProcessCSVTestUtils.createSourceFileEntity(TEST_VXA_ZIP));
        int res = service.processCsvFile(sfe, false);
        assertThat(res).isEqualTo(876);
    }
}

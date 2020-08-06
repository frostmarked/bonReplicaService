package com.bonlimousin.replica.service.entitychange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.config.KafkaTestConfiguration;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.HornStatus;
import com.bonlimousin.replica.service.BovineService;

@ActiveProfiles("emitentitychanges")
@SpringBootTest(classes = { BonReplicaServiceApp.class, KafkaTestConfiguration.class })
class EntityChangeJaversAspectIT {

	@Autowired
	private BovineService bovineService;

	@Autowired
	private ConsumerFactory<String, EntityChangeVO> entityChangeConsumerFactory;

	@BeforeAll
	public static void setup() {
		KafkaTestConfiguration.startKafka();
	}
	
	public static BovineEntity createEntity() {
		BovineEntity bovineEntity = new BovineEntity()
				.earTagId(2000)
				.masterIdentifier("2000")
				.country("se")
				.herdId(15112)
				.birthDate(Instant.now())
				.gender(Gender.BULL)
				.name("bully")
				.bovineStatus(BovineStatus.ON_FARM)
				.hornStatus(HornStatus.HORNED)
				.matriId(20)
				.patriId(30)
				.weight0(43)
				.weight200(340)
				.weight365(630);
		return bovineEntity;
	}

	@Test
	@Transactional
	void testBonvineEntityChange() {
		BovineEntity be = bovineService.save(createEntity());
		
		Consumer<String, EntityChangeVO> consumer = entityChangeConsumerFactory.createConsumer();		
		consumer.subscribe(Collections.singletonList("ENTITY_CHANGE_BOVINEENTITY"));
		ConsumerRecords<String, EntityChangeVO> records = consumer.poll(Duration.ofSeconds(1));

		assertThat(records.count()).isEqualTo(1);
		ConsumerRecord<String, EntityChangeVO> record = records.iterator().next();
		assertEquals("CREATE", record.key());
		assertThat(record.value().getEntityId()).isEqualTo(be.getId().toString());
		
		assertThat(record.value().getEntityValue())
			.containsEntry("earTagId", be.getEarTagId())
			.containsEntry("masterIdentifier", be.getMasterIdentifier())
			.containsEntry("country", be.getCountry())
			.containsEntry("herdId", be.getHerdId())
			.containsEntry("birthDate", DateTimeFormatter.ISO_INSTANT.format(be.getBirthDate()))
			.containsEntry("gender", be.getGender().name())
			.containsEntry("name", be.getName())
			.containsEntry("bovineStatus", be.getBovineStatus().name())
			.containsEntry("hornStatus", be.getHornStatus().name())
			.containsEntry("matriId", be.getMatriId())
			.containsEntry("patriId", be.getPatriId())
			.containsEntry("weight0", be.getWeight0())
			.containsEntry("weight200", be.getWeight200())
			.containsEntry("weight365", be.getWeight365())
			;
		
		List<String> fields = Arrays.asList("earTagId", 
				"masterIdentifier", "country", "herdId", "birthDate", "gender", "name", "bovineStatus",
				"hornStatus", "matriId", "patriId", "weight0", "weight200", "weight365");
		assertThat(record.value().getChangedEntityFields()).containsAll(fields);
	}
	
	@AfterAll
	public static void tearDown() {
		KafkaTestConfiguration.stopKafka();
	}
}

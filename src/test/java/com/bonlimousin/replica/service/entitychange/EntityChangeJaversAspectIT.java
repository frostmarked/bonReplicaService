package com.bonlimousin.replica.service.entitychange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(OrderAnnotation.class)
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
	@Order(1)
	@Transactional
	void testBonvineEntityChange() {
		BovineEntity be = bovineService.save(createEntity());				
		ConsumerRecords<String, EntityChangeVO> records = consumeChanges();				
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
	
	@Test
	@Order(2)
	@Transactional
	void testBonvineEntityChangeUpdate() {
		BovineEntity be = bovineService.save(createEntity());		
		BovineEntity beUpd = bovineService.save(be.name("TEST"));				
		ConsumerRecords<String, EntityChangeVO> records = consumeChanges();		
		assertThat(records.count()).isEqualTo(2);
		Iterator<ConsumerRecord<String, EntityChangeVO>> it = records.iterator();
		ConsumerRecord<String, EntityChangeVO> recordCreate = it.next();
		assertEquals("CREATE", recordCreate.key());
		ConsumerRecord<String, EntityChangeVO> record = it.next();
		assertEquals("UPDATE", record.key());
		assertThat(record.value().getEntityId()).isEqualTo(be.getId().toString());
		
		assertThat(record.value().getEntityValue())
			.containsEntry("earTagId", be.getEarTagId())
			.containsEntry("masterIdentifier", be.getMasterIdentifier())
			.containsEntry("country", be.getCountry())
			.containsEntry("herdId", be.getHerdId())
			.containsEntry("birthDate", DateTimeFormatter.ISO_INSTANT.format(be.getBirthDate()))
			.containsEntry("gender", be.getGender().name())
			.containsEntry("name", beUpd.getName())
			.containsEntry("bovineStatus", be.getBovineStatus().name())
			.containsEntry("hornStatus", be.getHornStatus().name())
			.containsEntry("matriId", be.getMatriId())
			.containsEntry("patriId", be.getPatriId())
			.containsEntry("weight0", be.getWeight0())
			.containsEntry("weight200", be.getWeight200())
			.containsEntry("weight365", be.getWeight365())
			;
		
		List<String> fields = Arrays.asList("name");
		assertThat(record.value().getChangedEntityFields()).containsAll(fields);		
	}
	
	private ConsumerRecords<String, EntityChangeVO> consumeChanges() {
		Consumer<String, EntityChangeVO> consumer = entityChangeConsumerFactory.createConsumer();		
		consumer.subscribe(Collections.singletonList("ENTITY_CHANGE_BOVINEENTITY"));
		ConsumerRecords<String, EntityChangeVO> records = consumer.poll(Duration.ofSeconds(2));
		consumer.commitSync();
		consumer.unsubscribe();
		consumer.close();
		return records;
	}
	
	@AfterAll
	public static void tearDown() {
		KafkaTestConfiguration.stopKafka();
	}
}

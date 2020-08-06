package com.bonlimousin.replica.service.entitychange;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.javers.core.commit.Commit;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EntityChangeService {

	private final Logger log = LoggerFactory.getLogger(EntityChangeService.class);

	private final KafkaTemplate<String, EntityChangeVO> kafkaTemplate;

	public EntityChangeService(KafkaTemplate<String, EntityChangeVO> entityChangeKafkaTemplate) {
		this.kafkaTemplate = entityChangeKafkaTemplate;
	}

	public void broadcastEntityChange(Commit commit) {
		if (commit.getSnapshots().isEmpty()) {
			return;
		}
		CdoSnapshot snapshot = commit.getSnapshots().get(0);
		EntityChangeVO vo = CdoSnapshotToEntityChangeVOConverter.convert(snapshot, new EntityChangeVO());
		String topic = "ENTITY_CHANGE_" + getManagedTypeSimpleName(snapshot).toUpperCase();
		String key = vo.getAction();
		send(new ProducerRecord<>(topic, key, vo));
	}
	
	public void send(ProducerRecord<String, EntityChangeVO> record) {			
		kafkaTemplate.send(record).addCallback(
				result -> log.debug(
						"Sent entity-change-topic {} with key {} and changes to params {} with resulting offset {} ",
						record.topic(), record.key(), record.value().getChangedEntityFields(), result.getRecordMetadata().offset()),
				ex -> log.error("Failed to send entity-change-topic {} with key {} and changes to params {} due to {} ",
						record.topic(), record.key(), record.value().getChangedEntityFields(), ex.getMessage(), ex));
	}

	protected static String getManagedTypeSimpleName(CdoSnapshot snapshot) {
		String className = snapshot.getManagedType().getName();
		return className.substring(className.lastIndexOf('.') + 1);
	}

}
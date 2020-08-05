package com.bonlimousin.replica.service.entitychange;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.commit.Commit;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Profile({"dev", "prod"})
@Component
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
public class EntityChangeJaversAspect {

	private final Logger log = LoggerFactory.getLogger(EntityChangeJaversAspect.class);

	private final KafkaTemplate<String, EntityChangeVO> kafkaTemplate;

	public EntityChangeJaversAspect(KafkaTemplate<String, EntityChangeVO> entityChangeKafkaTemplate) {
		this.kafkaTemplate = entityChangeKafkaTemplate;
	}

	@AfterReturning(pointcut = "execution(public * commit(..)) && this(org.javers.core.Javers)", returning = "commit")
	public void onJaversCommitExecuted(JoinPoint pjp, Commit commit) {
		if (commit.getSnapshots().size() > 0) {
			broadcastEntityChange(commit);
		}
	}

	protected void broadcastEntityChange(Commit commit) {
		CdoSnapshot snapshot = commit.getSnapshots().get(0);
		EntityChangeVO vo = CdoSnapshotToEntityChangeVOConverter.convert(snapshot, new EntityChangeVO());
		String topic = "ENTITY_CHANGE_" + getManagedTypeSimpleName(snapshot).toUpperCase();
		String key = vo.getAction();
		ProducerRecord<String, EntityChangeVO> record = new ProducerRecord<>(topic, key, vo);
		kafkaTemplate.send(record).addCallback(result -> {
			log.debug("Sent entity-change-topic {} with key {} and changes to params {} with resulting offset {} ",
					topic, key, vo.getChangedEntityFields(), result.getRecordMetadata().offset());
		}, ex -> {
			log.error("Failed to send entity-change-topic {} with key {} and changes to params {} due to {} ", topic,
					key, vo.getChangedEntityFields(), ex.getMessage(), ex);
		});
	}

	protected static String getManagedTypeSimpleName(CdoSnapshot snapshot) {
		String className = snapshot.getManagedType().getName();
		return className.substring(className.lastIndexOf('.') + 1);
	}

}
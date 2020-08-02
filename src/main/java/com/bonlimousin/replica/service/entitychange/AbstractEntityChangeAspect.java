package com.bonlimousin.replica.service.entitychange;

import java.util.List;
import java.util.Optional;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.JoinPoint;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AspectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public abstract class AbstractEntityChangeAspect {

	private final Logger log = LoggerFactory.getLogger(AbstractEntityChangeAspect.class);

	private final Javers javers;
	private final KafkaTemplate<String, EntityChangeVO> kafkaTemplate;

	protected AbstractEntityChangeAspect(Javers javers,
			KafkaTemplate<String, EntityChangeVO> entityChangeKafkaTemplate) {
		this.javers = javers;
		this.kafkaTemplate = entityChangeKafkaTemplate;
	}

	protected void onSave(JoinPoint pjp, Object returnedObject) {
		getRepositoryInterface(pjp).ifPresent(i -> {
			RepositoryMetadata metadata = DefaultRepositoryMetadata.getMetadata(i);
			for (Object domainObject : AspectUtil.collectReturnedObjects(returnedObject)) {
				broadcastSave(metadata, domainObject);
			}
		});
	}

	protected void onDelete(JoinPoint pjp) {
		getRepositoryInterface(pjp).ifPresent(i -> {
			RepositoryMetadata metadata = DefaultRepositoryMetadata.getMetadata(i);
			for (Object deletedObject : AspectUtil.collectArguments(pjp)) {
				broadcastDelete(metadata, deletedObject);
			}
		});
	}

	private Optional<Class<?>> getRepositoryInterface(JoinPoint pjp) {
		for (Class<?> i : pjp.getTarget().getClass().getInterfaces()) {
			if (i.isAnnotationPresent(JaversSpringDataAuditable.class) && CrudRepository.class.isAssignableFrom(i)) {
				return Optional.of(i);
			}
		}
		return Optional.empty();
	}

	protected void broadcastSave(RepositoryMetadata repositoryMetadata, Object domainObject) {
		JqlQuery jq = QueryBuilder.byInstance(domainObject).limit(1).build();
		List<CdoSnapshot> snaps = this.javers.findSnapshots(jq);
		if (snaps.size() == 0) {
			log.warn("No snapshot found for instance {} on save", domainObject);
			return;
		}
		Class<?> domainType = repositoryMetadata.getDomainType();
		broadcastEntityChange(domainType, snaps.get(0));
	}

	protected void broadcastDelete(RepositoryMetadata repositoryMetadata, Object domainObjectOrId) {
		Class<?> domainType = repositoryMetadata.getDomainType();
		JqlQuery jq;
		if (isIdClass(repositoryMetadata, domainObjectOrId)) {
			jq = QueryBuilder.byInstanceId(domainObjectOrId, domainType).limit(1).build();
		} else if (isDomainClass(repositoryMetadata, domainObjectOrId)) {
			jq = QueryBuilder.byInstance(domainObjectOrId).limit(1).build();
		} else {
			throw new IllegalArgumentException("Domain object or object id expected");
		}

		List<CdoSnapshot> snaps = javers.findSnapshots(jq);
		if (snaps.size() == 0) {
			log.warn("No snapshot found for object {} on delete", domainObjectOrId);
			return;
		}
		broadcastEntityChange(domainType, snaps.get(0));
	}

	protected void broadcastEntityChange(Class<?> domainType, CdoSnapshot snapshot) {
		EntityChangeVO vo = CdoSnapshotToEntityChangeVOConverter.convert(snapshot);
		String topic = "ENTITY_CHANGE_" + domainType.getSimpleName().toUpperCase();
		String key = vo.getAction();
		ProducerRecord<String, EntityChangeVO> record = new ProducerRecord<>(topic, key, vo);
		ListenableFuture<SendResult<String, EntityChangeVO>> future = kafkaTemplate.send(record);

		future.addCallback(new ListenableFutureCallback<SendResult<String, EntityChangeVO>>() {

			@Override
			public void onSuccess(SendResult<String, EntityChangeVO> result) {
				log.debug("Sent topic {} with key {} and message {} with resulting offset {} ", topic, key, vo,
						result.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				log.error("Failed to send topic {} with key {} and message {} due to {} ", topic, key, vo,
						ex.getMessage(), ex);
			}
		});
	}

	private static boolean isDomainClass(RepositoryMetadata metadata, Object o) {
		return metadata.getDomainType().isAssignableFrom(o.getClass());
	}

	private static boolean isIdClass(RepositoryMetadata metadata, Object o) {
		return metadata.getIdType().isAssignableFrom(o.getClass());
	}

}

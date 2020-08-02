package com.bonlimousin.replica.service.entitychange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

import org.javers.core.metamodel.object.CdoSnapshot;

public class CdoSnapshotToEntityChangeVOConverter {

	public static EntityChangeVO convert(CdoSnapshot snapshot) {
		EntityChangeVO vo = new EntityChangeVO();

		switch (snapshot.getType()) {
		case INITIAL:
			vo.setAction("CREATE");
			break;
		case UPDATE:
			vo.setAction("UPDATE");
			break;
		case TERMINAL:
			vo.setAction("DELETE");
			break;
		}

		vo.setEntityType(snapshot.getManagedType().getName());
		vo.setEntityId(snapshot.getGlobalId().value().split("/")[1]);
		vo.setModifiedBy(snapshot.getCommitMetadata().getAuthor());
		vo.setChangedEntityFields(snapshot.getChanged());
		Map<String, Object> map = snapshot.getState().getPropertyNames().stream()
				.collect(Collectors.toMap(key -> key, key -> snapshot.getPropertyValue(key)));
		vo.setEntityValue(map);
		LocalDateTime localTime = snapshot.getCommitMetadata().getCommitDate();
		Instant modifyDate = localTime.toInstant(ZoneOffset.UTC);
		vo.setModifiedDate(modifyDate);
		return vo;
	}
}

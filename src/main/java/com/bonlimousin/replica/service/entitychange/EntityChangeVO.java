package com.bonlimousin.replica.service.entitychange;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class EntityChangeVO {

	private String action;
	private String entityType;
	private String entityId;
	private List<String> changedEntityFields;
	private Map<String, Object> entityValue;
	private String modifiedBy;
	private Instant modifiedDate;

	public EntityChangeVO() {
		super();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public List<String> getChangedEntityFields() {
		return changedEntityFields;
	}

	public void setChangedEntityFields(List<String> changedEntityFields) {
		this.changedEntityFields = changedEntityFields;
	}

	public Map<String, Object> getEntityValue() {
		return entityValue;
	}

	public void setEntityValue(Map<String, Object> entityValue) {
		this.entityValue = entityValue;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Instant getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Instant modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}

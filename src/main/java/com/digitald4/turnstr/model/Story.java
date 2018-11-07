package com.digitald4.turnstr.model;

import com.digitald4.turnstr.proto.Turnstr;
import com.digitald4.turnstr.proto.Turnstr.AbuseType;
import com.digitald4.turnstr.proto.Turnstr.ContentType;
import com.digitald4.turnstr.proto.Turnstr.Visibility;

public class Story implements ContentItem<Turnstr.Story> {
	private Turnstr.Story originalProto;
	private Turnstr.Story proto;

	public Story(Turnstr.Story proto) {
		this.proto = this.originalProto = proto;
	}

	@Override
	public ContentType getType() {
		return ContentType.CT_STORY;
	}

	@Override
	public long getId() {
		return originalProto.getId();
	}

	@Override
	public long getUserId() {
		return originalProto.getUserId();
	}

	@Override
	public Story setUserId(long userId) {
		originalProto = originalProto.toBuilder().setUserId(userId).build();
		return this;
	}

	@Override
	public AbuseType getAbuseType() {
		return originalProto.getAbuseType();
	}

	@Override
	public long getCreatedAt() {
		return originalProto.getCreatedAt();
	}

	@Override
	public Story setCreatedAt(long timestamp) {
		originalProto = originalProto.toBuilder().setCreatedAt(timestamp).build();
		return this;
	}

	@Override
	public long getUpdatedAt() {
		return originalProto.getUpdatedAt();
	}

	@Override
	public Story setUpdatedAt(long timestamp) {
		originalProto = originalProto.toBuilder().setUpdatedAt(timestamp).build();
		return this;
	}

	@Override
	public long getDeletedAt() {
		return originalProto.getDeletedAt();
	}

	@Override
	public Story setDeletedAt(long timestamp) {
		originalProto = originalProto.toBuilder().setDeletedAt(timestamp).build();
		return this;
	}

	@Override
	public Turnstr.Story getOriginalProto() {
		return originalProto;
	}

	@Override
	public Visibility getVisibility() {
		return proto.getVisibility();
	}

	@Override
	public Turnstr.Story getProto() {
		return proto;
	}

	@Override
	public Story setProto(Turnstr.Story  proto) {
		this.proto = proto;
		return this;
	}

	@Override
	public Turnstr.Story toProto() {
		return getOriginalProto().toBuilder()
				.setId(getId())
				.setUserId(getUserId())
				.setState(getState())
				.setAbuseType(getAbuseType())
				.setCreatedAt(getCreatedAt())
				.setUpdatedAt(getUpdatedAt())
				.setDeletedAt(getDeletedAt())
				.build();
	}
}

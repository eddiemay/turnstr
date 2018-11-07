package com.digitald4.turnstr.model;

import com.digitald4.turnstr.proto.Turnstr;
import com.digitald4.turnstr.proto.Turnstr.AbuseType;
import com.digitald4.turnstr.proto.Turnstr.ContentType;
import com.digitald4.turnstr.proto.Turnstr.Visibility;

public class LiveVideo implements ContentItem<Turnstr.LiveVideo> {
	private Turnstr.LiveVideo originalProto;
	private Turnstr.LiveVideo proto;

	public LiveVideo(Turnstr.LiveVideo proto) {
		this.proto = this.originalProto = proto;
	}

	@Override
	public ContentType getType() {
		return ContentType.CT_LIVE_VIDEO;
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
	public LiveVideo setUserId(long userId) {
		originalProto = originalProto.toBuilder().setUserId(userId).build();
		return this;
	}

	@Override
	public Visibility getVisibility() {
		return proto.getVisibility();
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
	public LiveVideo setCreatedAt(long timestamp) {
		originalProto = originalProto.toBuilder().setCreatedAt(timestamp).build();
		return this;
	}

	@Override
	public long getUpdatedAt() {
		return originalProto.getUpdatedAt();
	}

	@Override
	public LiveVideo setUpdatedAt(long timestamp) {
		originalProto = originalProto.toBuilder().setUpdatedAt(timestamp).build();
		return this;
	}

	@Override
	public long getDeletedAt() {
		return originalProto.getUpdatedAt();
	}

	@Override
	public LiveVideo setDeletedAt(long timestamp) {
		originalProto = originalProto.toBuilder().setDeletedAt(timestamp).build();
		return this;
	}

	@Override
	public Turnstr.LiveVideo getOriginalProto() {
		return originalProto;
	}

	@Override
	public Turnstr.LiveVideo getProto() {
		return proto;
	}

	@Override
	public LiveVideo setProto(Turnstr.LiveVideo  proto) {
		this.proto = proto;
		return this;
	}

	@Override
	public Turnstr.LiveVideo toProto() {
		return getProto().toBuilder()
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

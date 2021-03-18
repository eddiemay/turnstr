package com.digitald4.turnstr.model;

import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.turnstr.proto.Turnstr;
import com.digitald4.turnstr.proto.Turnstr.AbuseType;
import com.digitald4.turnstr.proto.Turnstr.ContentState;
import com.digitald4.turnstr.proto.Turnstr.ContentType;
import com.digitald4.turnstr.proto.Turnstr.Visibility;
import com.google.protobuf.Message;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ContentItem<T extends Message> {

	enum ContentEnum {
		STORY(ContentType.CT_STORY, Story.class, Turnstr.Story.class),
		LIVE_VIDEO(ContentType.CT_LIVE_VIDEO, LiveVideo.class, Turnstr.LiveVideo.class);

		private ContentType contentType;
		private Class<? extends ContentItem> contentItemCls;
		private Class<? extends Message> protoCls;

		<C extends ContentItem, P extends Message> ContentEnum(
				ContentType contentType,
				Class<C> contentItemCls,
				Class<P> protoCls) {
			this.contentType = contentType;
			this.contentItemCls = contentItemCls;
			this.protoCls = protoCls;
		}

		public ContentType getContentType() {
			return contentType;
		}

		public Class<? extends ContentItem> getContentItemCls() {
			return contentItemCls;
		}

		public Class<? extends Message> getProtoCls() {
			return protoCls;
		}
	}

	Map<ContentType, ContentEnum> byContentType = Arrays.stream(ContentEnum.values())
			.collect(Collectors.toMap(ContentEnum::getContentType, Function.identity()));

	Map<Class<? extends ContentItem>, ContentEnum> byContentItemCls = Arrays.stream(ContentEnum.values())
			.collect(Collectors.toMap(ContentEnum::getContentItemCls, Function.identity()));

	Map<Class<? extends Message>, ContentEnum> byProtoCls = Arrays.stream(ContentEnum.values())
			.collect(Collectors.toMap(ContentEnum::getProtoCls, Function.identity()));

	static ContentEnum getContentEnum(Class<? extends ContentItem> contentItemCls) {
		ContentEnum contentEnum = byContentItemCls.get(contentItemCls);
		if (contentEnum == null) {
			throw new DD4StorageException(
					"No content type for: " + contentItemCls, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return contentEnum;
	}

	static ContentEnum getContentEnum(Message proto) {
		ContentEnum contentEnum = byProtoCls.get(proto.getClass());
		if (contentEnum == null) {
			throw new DD4StorageException(
					"No content type for: " + proto.getClass(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return contentEnum;
	}

	static <T extends Message> ContentItem<T> convert(T proto) {
		switch (getContentEnum(proto)) {
			case STORY: return (ContentItem<T>) new Story((Turnstr.Story) proto);
			case LIVE_VIDEO: return (ContentItem<T>) new LiveVideo((Turnstr.LiveVideo) proto);
		}
		return null;
	}

	long getId();

	long getUserId();

	ContentItem<T> setUserId(long userId);

	ContentType getType();

	Visibility getVisibility();

	default ContentState getState() {
		if (getDeletedAt() > 0) {
			return ContentState.CONTENT_STATE_DELETED;
		} else if (getAbuseType() != AbuseType.ABUSE_TYPE_UNSPECIFIED) {
			return ContentState.CONTENT_STATE_ABUSE_VIOLATION;
		}
		return ContentState.forNumber(getVisibility().getNumber());
	}

	AbuseType getAbuseType();

	long getCreatedAt();

	ContentItem<T> setCreatedAt(long timestamp);

	long getUpdatedAt();

	ContentItem<T> setUpdatedAt(long timestamp);

	long getDeletedAt();

	ContentItem<T> setDeletedAt(long timestamp);

	T getOriginalProto();

	T getProto();

	ContentItem<T> setProto(T proto);

	T toProto();

	default void checkCommentAccess(TurnstrUser user) {
		if (user == null) {
			throw new DD4StorageException("UNAUTHENTICATED", HttpServletResponse.SC_UNAUTHORIZED);
		}

		if (getState() == ContentState.CONTENT_STATE_PUBLIC_VISIBLE) {
			return;
		}

		if (getUserId() == user.getId()) {
			// A user always has access to his/her own content.
			return;
		}

		if (getState() == ContentState.CONTENT_STATE_FOLLOWER_VISIBLE) {
			if (user.getFollowingIds().contains(getUserId())) {
				return;
			}
		} else if (getState() == ContentState.CONTENT_STATE_FAMILY_VISIBLE) {
			if (user.getFamilyIds().contains(getUserId())) {
				return;
			}
		}
		throw new DD4StorageException("Not found", HttpServletResponse.SC_NOT_FOUND);
	}

	default void checkWriteAccess(TurnstrUser user) {
		if (getUserId() == user.getId()) {
			// The owner of the content is the only one that can edit it, besides admins.
			return;
		}
		throw new DD4StorageException("UNAUTHORIZED", HttpServletResponse.SC_UNAUTHORIZED);
	}
}

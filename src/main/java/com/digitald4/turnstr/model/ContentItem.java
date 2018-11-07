package com.digitald4.turnstr.model;

import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.turnstr.proto.Turnstr.AbuseType;
import com.digitald4.turnstr.proto.Turnstr.ContentState;
import com.digitald4.turnstr.proto.Turnstr.ContentType;
import com.digitald4.turnstr.proto.Turnstr.Visibility;
import javax.servlet.http.HttpServletResponse;

public interface ContentItem<T> {

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

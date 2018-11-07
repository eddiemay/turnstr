package com.digitald4.turnstr.storage;

import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.turnstr.model.ContentItem;
import com.digitald4.turnstr.model.LiveVideo;
import com.digitald4.turnstr.model.Story;
import com.digitald4.turnstr.model.TurnstrUser;
import com.digitald4.turnstr.proto.Turnstr;
import com.digitald4.turnstr.proto.Turnstr.ContentState;
import com.digitald4.turnstr.proto.Turnstr.ContentType;
import com.google.protobuf.Message;
import java.time.Clock;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;

public class ContentItemStore {
	private final DAO dao;
	private final Provider<TurnstrUser> userProvider;
	private final Clock clock;

	@Inject
	public ContentItemStore(DAO dao, Provider<TurnstrUser> userProvider, Clock clock) {
		this.dao = dao;
		this.userProvider = userProvider;
		this.clock = clock;
	}

	public <T extends Message> ContentItem<T> create(ContentItem<T> contentItem) {
		long millis = clock.millis();
		return get(dao.create(contentItem.setCreatedAt(millis).setUpdatedAt(millis).getProto()));
	}

	public ContentItem<?> getWithAccessCheck(ContentType contentType, long itemId) {
		switch (contentType) {
			case CT_STORY: return getWithAccessCheck(Turnstr.Story.class, itemId);
			case CT_LIVE_VIDEO: return getWithAccessCheck(Turnstr.LiveVideo.class, itemId);
		}
		throw new DD4StorageException("Not Found", 404);
	}

	public ContentItem<?> get(ContentType contentType, long itemId) {
		switch (contentType) {
			case CT_STORY: return get(Turnstr.Story.class, itemId);
			case CT_LIVE_VIDEO: return get(Turnstr.LiveVideo.class, itemId);
		}
		throw new DD4StorageException("Not Found", 404);
	}

	public <T extends Message> ContentItem<T> getWithAccessCheck(Class<T> cls, long itemId) {
		return checkAccess(get(cls, itemId));
	}

	public <T extends Message> ContentItem<T> get(Class<T> cls, long itemId) {
		T t = dao.get(cls, itemId);
		if (t == null) {
			throw new DD4StorageException("Not found", 404);
		}
		return get(t);
	}

	public <T extends Message> QueryResult<ContentItem<T>> list(Class<T> cls, Query query) {
		QueryResult<T> queryResult = dao.list(cls, query);
		return new QueryResult<>(queryResult.getResults()
				.stream()
				.map(this::get)
				.collect(Collectors.toList()), queryResult.getTotalSize());
	}

	public <T extends Message> ContentItem<T> update(Class<T> cls, long itemId, UnaryOperator<ContentItem<T>> updater) {
		return get(dao.update(cls, itemId, current -> updater.apply(get(current)).getProto()));
	}

	public <T extends Message> void softDelete(Class<T> cls, long itemId) {
		update(cls, itemId, current -> current.setDeletedAt(clock.millis()));
	}

	public ContentState getMinimumContentState(TurnstrUser user, long userId) {
		if (user != null) {
			if (user.getId() == userId) {
				return ContentState.CONTENT_STATE_ABUSE_VIOLATION;
			} else if (user.getFamilyIds().contains(userId)) {
				return ContentState.CONTENT_STATE_FAMILY_VISIBLE;
			} else if (user.getFollowingIds().contains(userId)) {
				return ContentState.CONTENT_STATE_FOLLOWER_VISIBLE;
			}
		}
		return ContentState.CONTENT_STATE_PUBLIC_VISIBLE;
	}

	private <T extends Message> ContentItem<T> checkAccess(ContentItem<T> contentItem) {
		TurnstrUser user = userProvider.get();
		if (contentItem.getState().getNumber() < getMinimumContentState(user, contentItem.getUserId()).getNumber()) {
			if (user == null) {
				throw new DD4StorageException("UNAUTHENTICATED", HttpServletResponse.SC_UNAUTHORIZED);
			}
			throw new DD4StorageException("Not found", HttpServletResponse.SC_NOT_FOUND);
		}
		return contentItem;
	}

	private <T extends Message> ContentItem<T> get(T item) {
		if (item instanceof Turnstr.Story) {
			return (ContentItem<T>) new Story((Turnstr.Story) item);
		} else if (item instanceof Turnstr.LiveVideo) {
			return (ContentItem<T>) new LiveVideo((Turnstr.LiveVideo) item);
		}
		return null;
	}
}

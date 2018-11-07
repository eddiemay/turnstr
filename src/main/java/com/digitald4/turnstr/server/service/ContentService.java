package com.digitald4.turnstr.server.service;

import static com.digitald4.common.util.ProtoUtil.createFilter;

import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.proto.DD4Protos.Query.OrderBy;
import com.digitald4.common.proto.DD4UIProtos.BatchDeleteRequest;
import com.digitald4.common.proto.DD4UIProtos.BatchDeleteResponse;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.model.UpdateRequest;
import com.digitald4.common.server.service.EntityService;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.util.ProtoUtil;
import com.digitald4.turnstr.model.ContentItem;
import com.digitald4.turnstr.model.TurnstrUser;
import com.digitald4.turnstr.proto.Turnstr.ContentState;
import com.digitald4.turnstr.storage.ContentItemStore;
import com.google.api.server.spi.config.Named;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Provider;

public abstract class ContentService<T extends Message> implements EntityService<ContentItem<T>> {

	private Class<T> cls;
	private ContentItemStore contentItemStore;
	private Provider<TurnstrUser> userProvider;

	ContentService(Class<T> cls, ContentItemStore contentItemStore, Provider<TurnstrUser> userProvider) {
		this.cls = cls;
		this.contentItemStore = contentItemStore;
		this.userProvider = userProvider;
	}

	@Override
	public ContentItem<T> create(ContentItem<T> contentItem) {
		return contentItemStore.create(contentItem.setUserId(userProvider.get().getId()));
	}

	@Override
	public ContentItem<T> get(long id) {
		return contentItemStore.getWithAccessCheck(cls, id);
	}

	@Override
	public QueryResult<ContentItem<T>> list(ListRequest request) {
		Query query = ProtoUtil.toQuery(request);
		QueryResult<ContentItem<T>> publicResults = contentItemStore.list(cls, query.toBuilder()
				.addFilter(createFilter("state", "=", ContentState.CONTENT_STATE_PUBLIC_VISIBLE_VALUE))
				.addOrderBy(OrderBy.newBuilder().setColumn("updated_at").setDesc(true).build())
				.build());
		TurnstrUser user = userProvider.get();
		if (user != null && user.getFollowingIds().size() > 0) {
			List<ContentItem<T>> results = new ArrayList<>(publicResults.getResults());
			long lastTime = results.get(results.size() - 1).getUpdatedAt();
			results.addAll(contentItemStore
					.list(cls, query.toBuilder()
							.addFilter(createFilter("state", "=", ContentState.CONTENT_STATE_FOLLOWER_VISIBLE_VALUE))
							.addFilter(createFilter("updated_at", ">", lastTime))
							.addOrderBy(OrderBy.newBuilder().setColumn("updated_at").setDesc(true).build())
							.build())
					.getResults()
					.stream()
					.filter(contentItem -> user.getFollowingIds().contains(contentItem.getUserId()))
					.collect(Collectors.toList()));
			if (user.getFamilyIds().size() > 0) {
				results.addAll(contentItemStore
						.list(cls, query.toBuilder()
								.addFilter(createFilter("state", "=", ContentState.CONTENT_STATE_FAMILY_VISIBLE_VALUE))
								.addFilter(createFilter("updated_at", ">", lastTime))
								.addOrderBy(OrderBy.newBuilder().setColumn("updated_at").setDesc(true).build())
								.build())
						.getResults()
						.stream()
						.filter(contentItem -> user.getFamilyIds().contains(contentItem.getUserId()))
						.collect(Collectors.toList()));
			}
			new QueryResult<>(
					results.stream()
							.sorted(Comparator.comparing(ContentItem::getUpdatedAt, Comparator.reverseOrder()))
							.limit(query.getLimit())
							.collect(Collectors.toList()),
					publicResults.getTotalSize());
		}
		return publicResults;
	}

	public QueryResult<ContentItem<T>> list(@Named("user_id") long userId) {
		ContentState minimumState = contentItemStore.getMinimumContentState(userProvider.get(), userId);
		return contentItemStore.list(cls, Query.newBuilder()
				.addFilter(createFilter("user_id", "=", userId))
				.addFilter(createFilter("state", ">=", minimumState.getNumber()))
				.addOrderBy(OrderBy.newBuilder().setColumn("updated_at").setDesc(true).build())
				.build());
	}

	@Override
	public ContentItem<T> update(@Named("id") long id, UpdateRequest<ContentItem<T>> updateRequest) {
		contentItemStore.get(cls, id).checkWriteAccess(userProvider.get());
		return contentItemStore.update(cls, id, current -> current.setProto(
				ProtoUtil.merge(updateRequest.getUpdateMask(), updateRequest.getEntity().getProto(), current.getProto())));
	}

	@Override
	public Empty delete(long id) {
		contentItemStore.get(cls, id).checkWriteAccess(userProvider.get());
		contentItemStore.softDelete(cls, id);
		return Empty.getDefaultInstance();
	}

	@Override
	public BatchDeleteResponse batchDelete(BatchDeleteRequest request) {
		return null;
	}
}

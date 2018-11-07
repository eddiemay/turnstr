package com.digitald4.turnstr.server.service;

import com.digitald4.common.proto.DD4Protos.Query.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.server.service.SingleProtoService;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.ProtoUtil;
import com.digitald4.turnstr.model.TurnstrUser;
import com.digitald4.turnstr.proto.Turnstr.Comment;
import com.digitald4.turnstr.proto.Turnstr.ContentType;
import com.digitald4.turnstr.storage.ContentItemStore;
import com.google.api.server.spi.config.Named;
import javax.inject.Inject;
import javax.inject.Provider;

public class CommentService extends SingleProtoService<Comment> {

	private final Store<Comment> commentStore;
	private final ContentItemStore contentItemStore;
	private final Provider<TurnstrUser> userProvider;

	@Inject
	public CommentService(
			Store<Comment> commentStore,
			ContentItemStore contentItemStore,
			Provider<TurnstrUser> userProvider) {
		super(commentStore);
		this.commentStore = commentStore;
		this.contentItemStore = contentItemStore;
		this.userProvider = userProvider;
	}

	public Comment create(Comment comment) {
		contentItemStore.get(comment.getContentType(), comment.getContentId()).checkCommentAccess(userProvider.get());
		return commentStore.create(comment);
	}

	public QueryResult<Comment> list(
			@Named("contentType") ContentType contentType,
			@Named("contentId") long contentId,
			ListRequest request) {
		contentItemStore.getWithAccessCheck(contentType, contentId);
		return commentStore.list(ProtoUtil.toQuery(request)
				.toBuilder()
				.addFilter(Filter.newBuilder().setColumn("content_type").setValue(String.valueOf(contentType)).build())
				.addFilter(Filter.newBuilder().setColumn("content_id").setValue(String.valueOf(contentId)).build())
				.build());
	}
}

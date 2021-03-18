package com.digitald4.turnstr.server.service;

import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.turnstr.model.TurnstrUser;
import com.digitald4.turnstr.proto.Turnstr;
import com.digitald4.turnstr.storage.ContentItemStore;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;

@Api(
    name = "users",
    version = "v1",
    namespace = @ApiNamespace(
        ownerDomain = "common.digitald4.com",
        ownerName = "common.digitald4.com"
    )
)
public class StoryService extends ContentService<Turnstr.Story> {

	private final ContentItemStore contentItemStore;
	private final Provider<HttpServletResponse> responseProvider;

	@Inject
	public StoryService(
			ContentItemStore contentItemStore,
			Provider<TurnstrUser> userProvider,
			Provider<HttpServletResponse> responseProvider) {
		super(Turnstr.Story.class, contentItemStore, userProvider);
		this.contentItemStore = contentItemStore;
		this.responseProvider = responseProvider;
	}
}

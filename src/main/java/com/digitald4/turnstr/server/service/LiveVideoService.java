package com.digitald4.turnstr.server.service;

import com.digitald4.turnstr.model.TurnstrUser;
import com.digitald4.turnstr.proto.Turnstr.LiveVideo;
import com.digitald4.turnstr.storage.ContentItemStore;
import javax.inject.Inject;
import javax.inject.Provider;

public class LiveVideoService extends ContentService<LiveVideo> {

	@Inject
	public LiveVideoService(ContentItemStore contentItemStore, Provider<TurnstrUser> userProvider) {
		super(LiveVideo.class, contentItemStore, userProvider);
	}
}

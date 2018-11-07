package com.digitald4.turnstr.server.service;

import com.digitald4.turnstr.model.TurnstrUser;
import com.digitald4.turnstr.proto.Turnstr.Story;
import com.digitald4.turnstr.storage.ContentItemStore;
import javax.inject.Inject;
import javax.inject.Provider;

public class StoryService extends ContentService<Story> {

	@Inject
	public StoryService(ContentItemStore contentItemStore, Provider<TurnstrUser> userProvider) {
		super(Story.class, contentItemStore, userProvider);
	}
}

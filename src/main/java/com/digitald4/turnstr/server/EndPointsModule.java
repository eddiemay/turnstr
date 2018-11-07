package com.digitald4.turnstr.server;

import com.digitald4.common.server.service.Echo;
import com.digitald4.common.server.service.GeneralDataService;
import com.digitald4.turnstr.server.service.CommentService;
import com.digitald4.turnstr.server.service.LiveVideoService;
import com.digitald4.turnstr.server.service.StoryService;
import com.digitald4.turnstr.server.service.UserService;
import com.google.common.collect.ImmutableList;

public class EndPointsModule extends com.digitald4.common.server.EndPointsModule {

	@Override
	public void configureServlets() {
		super.configureServlets();

		bind(Echo.class).toInstance(new Echo());
		configureEndpoints(API_URL_PATTERN,
				ImmutableList.of(Echo.class,
						/* FileService.class, */ GeneralDataService.class,
						CommentService.class, LiveVideoService.class, StoryService.class, UserService.class));
	}

	@Override
	protected String getEndPointsProjectId() {
		return "turnstr";
	}
}

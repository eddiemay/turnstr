package com.digitald4.turnstr.server.service;

import com.digitald4.common.server.service.SingleProtoService;
import com.digitald4.common.storage.Store;
import com.digitald4.turnstr.proto.Turnstr.User;

public class UserService extends SingleProtoService<User> {

	public UserService(Store<User> userStore) {
		super(userStore);
	}
}

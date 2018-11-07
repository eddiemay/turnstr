package com.digitald4.turnstr.model;

import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.model.User;
import com.digitald4.common.proto.DD4Protos.ActiveSession;
import com.digitald4.turnstr.proto.Turnstr;
import com.digitald4.turnstr.proto.Turnstr.PasswordInfo;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.Set;

public class TurnstrUser implements User<Turnstr.User, PasswordInfo> {
	private Turnstr.User userProto;
	private ActiveSession activeSession;

	public TurnstrUser() {
		userProto = Turnstr.User.getDefaultInstance();
	}

	public TurnstrUser(Turnstr.User userProto) {
		this.userProto = userProto;
	}

	@Override
	public long getId() {
		return userProto.getId();
	}

	@Override
	public int getTypeId() {
		return 10;
	}

	@Override
	public String getUsername() {
		return userProto.getUsername();
	}

	@Override
	public TurnstrUser setUsername(String username) {
		userProto.toBuilder().setUsername(username).build();
		return this;
	}

	@Override
	public ActiveSession getActiveSession() {
		return activeSession;
	}

	@Override
	public TurnstrUser setActiveSession(ActiveSession activeSession) {
		this.activeSession = activeSession;
		return this;
	}

	@Override
	public long getLastLogin() {
		return userProto.getLastLogin();
	}

	@Override
	public TurnstrUser updateLastLogin(Clock clock) {
		userProto = userProto.toBuilder().setLastLogin(clock.millis()).build();
		return this;
	}

	@Override
	public TurnstrUser setPasswordInfo(PasswordInfo passwordInfo) {
		userProto = userProto.toBuilder()
				.setPasswordInfo(passwordInfo)
				.build();
		return this;
	}

	@Override
	public TurnstrUser setProto(Turnstr.User userProto) {
		this.userProto = userProto;
		return this;
	}

	@Override
	public Turnstr.User getProto() {
		return userProto;
	}

	@Override
	public Turnstr.User toProto() {
		return userProto;
	}

	public Set<Long> getFollowingIds() {
		return ImmutableSet.of(userProto.getFollowingCount());
	}

	public Set<Long> getFamilyIds() {
		return ImmutableSet.of(userProto.getFamilyCount());
	}

	@Override
	public void verifyPassword(String password) {
		if (!userProto.getPasswordInfo().getPasswordDigest().equals(password)) {
			throw new DD4StorageException("Wrong username or password", 401);
		}
	}
}

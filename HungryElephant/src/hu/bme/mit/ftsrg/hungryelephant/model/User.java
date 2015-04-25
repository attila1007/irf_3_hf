package hu.bme.mit.ftsrg.hungryelephant.model;

import org.json.JSONObject;

public final class User implements Entity<User, String> {
	private final String username;
	private final String password;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String getId() {
		return username;
	}

	public String getUsername() {
		return username;
	}

	public boolean validatePassword(String password) {
		return this.password.equals(password);
	}

	public boolean validatePassword(User otherUser) {
		return this.password.equals(otherUser.password);
	}

	@Override
	public String toString() {
		return username;
	}

	public static User fromJSON(JSONObject json) {
		if (json == null) {
			throw new IllegalArgumentException("The JSON data is null");
		}

		String username = json.getString("username");
		String password = json.getString("password");

		return new User(username, password);
	}
}

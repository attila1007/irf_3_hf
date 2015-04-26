package hu.bme.mit.ftsrg.hungryelephant.handler;

import hu.bme.mit.ftsrg.hungryelephant.model.DatabaseModel;
import hu.bme.mit.ftsrg.hungryelephant.model.User;

import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;

public final class UserController extends Controller {
	public UserController(DatabaseModel model) {
		super(model);
	}

	@Override
	public HttpResponse dispatch(String method, String[] action, String data) {
		if (action.length != 1) {
			return null;
		}

		switch (action[0]) {
		case "login":
			return doLogin(method, data);

		case "logout":
			return doLogout(method, data);

		default:
			return null;
		}
	}

	private HttpResponse doLogin(String method, String data) {
		if (!method.equals("POST")) {
			return null;
		}

		JSONObject jsonResponse;
		try {
			// parse request
			User user = User.fromJSON(new JSONObject(data));

			synchronized (getModel()) {
				// get user by username
				User modelUser = getModel().users().get(user.getUsername());

				if (modelUser != null && modelUser.validatePassword(user)) {
					// password ok
					Optional<UUID> currentSessionId = getModel().sessions()
							.entrySet().stream()
							.filter(e -> e.getValue().equals(modelUser))
							.map(e -> e.getKey()).findAny();

					if (currentSessionId.isPresent()) {
						// already logged in
						jsonResponse = createErrorResponse("Already logged in");
						jsonResponse.put("sessionId", currentSessionId.get());
					} else {
						UUID sessionId = createNewSessionId();
						getModel().sessions().put(sessionId, modelUser);

						jsonResponse = createOkResponse().put("sessionId",
								sessionId.toString());
					}
				} else {
					// password invalid
					jsonResponse = createErrorResponse("Invalid credentials");
				}
			}
		} catch (Exception e) {
			jsonResponse = createErrorResponse(e.getClass().getSimpleName()
					+ ": " + e.getMessage());
		}

		return new HttpResponse(HttpStatusCodes.OK, "application/json",
				jsonResponse.toString(2));
	}

	private HttpResponse doLogout(String method, String data) {
		if (!method.equals("POST")) {
			return null;
		}

		JSONObject jsonResponse;
		try {
			// parse request
			JSONObject jsonRequest = new JSONObject(data);
			UUID sessionId = UUID
					.fromString(jsonRequest.getString("sessionId"));

			synchronized (getModel()) {
				// get user by sessionId
				User user = getModel().sessions().get(sessionId);

				if (user != null) {
					// logged in
					getModel().sessions().remove(sessionId);
					jsonResponse = createOkResponse();
				} else {
					// not logged in
					return new HttpResponse(HttpStatusCodes.UNAUTHORIZED);
				}
			}
		} catch (Exception e) {
			jsonResponse = createErrorResponse(e.getClass().getSimpleName()
					+ ": " + e.getMessage());
		}

		return new HttpResponse(HttpStatusCodes.OK, "application/json",
				jsonResponse.toString(2));
	}

	// XXX remove in production
	// this code part makes testing easier as it eliminates randomness
	private static int nextUuid = -1;

	private synchronized static UUID createNewSessionId() {
		if (nextUuid > 0) {
			return new UUID(0, nextUuid++);
		} else {
			return UUID.randomUUID();
		}
	}
}

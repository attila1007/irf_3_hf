package hu.bme.mit.ftsrg.hungryelephant.handler;

import hu.bme.mit.ftsrg.hungryelephant.model.DatabaseModel;

import org.json.JSONObject;

public abstract class Controller {
	private final DatabaseModel model;

	public Controller(DatabaseModel model) {
		this.model = model;
	}
	/*mσσσσσσσσdosνtαs*/
	public abstract HttpResponse dispatch(String method, String[] action,
			String data) throws Exception;

	public final DatabaseModel getModel() {
		return model;
	}

	protected final JSONObject createOkResponse() {
		return new JSONObject().put("status", "ok");
	}

	protected final JSONObject createErrorResponse(String message) {
		return new JSONObject().put("status", "error").put("message", message);
	}
}

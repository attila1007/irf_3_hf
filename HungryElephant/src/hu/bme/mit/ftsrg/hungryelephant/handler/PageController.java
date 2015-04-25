package hu.bme.mit.ftsrg.hungryelephant.handler;

import hu.bme.mit.ftsrg.hungryelephant.model.DatabaseModel;

public class PageController extends Controller {
	public PageController(DatabaseModel model) {
		super(model);
	}

	@Override
	public HttpResponse dispatch(String method, String[] action, String data) {
		if (action.length != 1) {
			return null;
		}

		if (!method.equals("GET")) {
			return null;
		}

		String content;
		try {
			synchronized (getModel()) {
				// get content for requested page
				content = getModel().pages().get(action[0]);

				if (content == null) {
					// page was not found
					return new HttpResponse(HttpStatusCodes.NOT_FOUND);
				}
			}
		} catch (Exception e) {
			content = e.getClass().getSimpleName() + ": " + e.getMessage();
		}

		return new HttpResponse(HttpStatusCodes.OK, "text/plain", content);
	}
}

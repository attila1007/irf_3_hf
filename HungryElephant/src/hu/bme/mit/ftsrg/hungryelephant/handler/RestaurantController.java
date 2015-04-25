package hu.bme.mit.ftsrg.hungryelephant.handler;

import hu.bme.mit.ftsrg.hungryelephant.model.DatabaseModel;
import hu.bme.mit.ftsrg.hungryelephant.model.Restaurant;
import hu.bme.mit.ftsrg.hungryelephant.model.User;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;

public final class RestaurantController extends Controller {
	public RestaurantController(DatabaseModel model) {
		super(model);
	}

	@Override
	public HttpResponse dispatch(String method, String[] action, String data)
			throws UnsupportedEncodingException {
		if (action.length == 0) {
			return doList(method, data);
		} else if (action.length == 1) {
			return doInfo(method, action[0], data);
		} else if (action.length == 2 && action[1].equals("orders")) {
			return doOrders(method, action[0], data);
		} else {
			return null;
		}
	}

	private HttpResponse doList(String method, String data) {
		if (!method.equals("GET")) {
			return null;
		}

		JSONObject jsonResponse = createOkResponse();

		synchronized (getModel()) {
			// query restaurants
			List<JSONObject> list = getModel()
					.restaurants()
					.values()
					.stream()
					.sorted()
					.map(r -> {
						return new JSONObject().put("name", r.getName()).put(
								"address", r.getAddress());
					}).collect(Collectors.toList());

			// add to response
			jsonResponse.put("restaurants", list);
		}

		return new HttpResponse(HttpStatusCodes.OK, "application/json",
				jsonResponse.toString(2));
	}

	private HttpResponse doInfo(String method, String name, String data)
			throws UnsupportedEncodingException {
		if (!method.equals("GET")) {
			return null;
		}

		name = URLDecoder.decode(name, "UTF-8");

		JSONObject jsonResponse;
		synchronized (getModel()) {
			Restaurant restaurant = getModel().restaurants().get(name);

			if (restaurant == null) {
				return new HttpResponse(HttpStatusCodes.NOT_FOUND);
			} else {
				jsonResponse = createOkResponse();

				// create restaurant object
				JSONObject jsonRestaurant = new JSONObject().put("name",
						restaurant.getName()).put("address",
						restaurant.getAddress());

				// create menu object
				List<JSONObject> menu = restaurant
						.menu()
						.entrySet()
						.stream()
						.map(e -> new JSONObject().put("name",
								e.getKey().getName())
								.put("price", e.getValue()))
						.collect(Collectors.toList());

				// add to response
				jsonResponse
						.put("restaurant", jsonRestaurant.put("menu", menu));
			}
		}

		return new HttpResponse(HttpStatusCodes.OK, "application/json",
				jsonResponse.toString(2));
	}

	private HttpResponse doOrders(String method, String name, String data)
			throws UnsupportedEncodingException {
		if (!method.equals("POST")) {
			return null;
		}

		name = URLDecoder.decode(name, "UTF-8");

		JSONObject jsonResponse;
		try {
			// parse request
			JSONObject jsonRequest = new JSONObject(data);
			UUID sessionId = UUID
					.fromString(jsonRequest.getString("sessionId"));

			synchronized (getModel()) {
				// get user by sessionId
				User user = getModel().sessions().get(sessionId);

				// get restaurant
				Restaurant restaurant = getModel().restaurants().get(name);

				if (user == null) {
					return new HttpResponse(HttpStatusCodes.UNAUTHORIZED);
				} else if (restaurant == null) {
					return new HttpResponse(HttpStatusCodes.NOT_FOUND);
				} else if (!restaurant.dispatchers().contains(user)) {
					jsonResponse = createErrorResponse("This user is not a dispatcher at this restaurant");
				} else {
					jsonResponse = createOkResponse();

					// query ids
					int[] ids = restaurant.orders().stream()
							.mapToInt(o -> o.getId()).toArray();
					jsonResponse.put("ids", ids);
				}
			}
		} catch (Exception e) {
			jsonResponse = createErrorResponse(e.getClass().getSimpleName()
					+ ": " + e.getMessage());
		}

		return new HttpResponse(HttpStatusCodes.OK, "application/json",
				jsonResponse.toString(2));
	}
}

package hu.bme.mit.ftsrg.hungryelephant.handler;

import hu.bme.mit.ftsrg.hungryelephant.model.DatabaseModel;
import hu.bme.mit.ftsrg.hungryelephant.model.Food;
import hu.bme.mit.ftsrg.hungryelephant.model.Order;
import hu.bme.mit.ftsrg.hungryelephant.model.Restaurant;
import hu.bme.mit.ftsrg.hungryelephant.model.User;

import java.util.Optional;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderController extends Controller {

	public OrderController(DatabaseModel model) {
		super(model);
	}

	@Override
	public HttpResponse dispatch(String method, String[] action, String data) {
		if (action.length == 0 && method.equals("PUT")) {
			return doCreate(method, data);
		} else if (action.length == 1 && method.equals("POST")) {
			return doInfo(method, Integer.parseInt(action[0]), data);
		} else if (action.length == 1 && method.equals("DELETE")) {
			return doDelete(method, Integer.parseInt(action[0]), data);
		} else {
			return null;
		}
	}

	private HttpResponse doCreate(String method, String data) {
		JSONObject jsonResponse;
		try {
			// parse request
			JSONObject jsonRequest = new JSONObject(data);
			UUID sessionId = UUID
					.fromString(jsonRequest.getString("sessionId"));
			JSONObject jsonOrder = jsonRequest.getJSONObject("order");
			String restaurantName = jsonOrder.getString("restaurant");
			JSONArray jsonFoods = jsonOrder.getJSONArray("foods");

			synchronized (getModel()) {
				// get user by sessionId
				User user = getModel().sessions().get(sessionId);

				// get restaurant
				Restaurant restaurant = getModel().restaurants().get(
						restaurantName);

				if (user == null) {
					return new HttpResponse(HttpStatusCodes.UNAUTHORIZED);
				} else if (restaurant == null) {
					return new HttpResponse(HttpStatusCodes.NOT_FOUND);
				} else {
					jsonResponse = null;

					// create order
					Order order = new Order(user, restaurant);

					// parse foods
					for (int i = 0; i < jsonFoods.length(); i++) {
						String foodName = jsonFoods.getString(i);
						Optional<Food> food = restaurant.menu().keySet()
								.stream()
								.filter(f -> f.getName().equals(foodName))
								.findAny();

						if (food.isPresent()) {
							order.foods().add(food.get());
						} else {
							jsonResponse = createErrorResponse("The restaurant does not offer this food: "
									+ foodName);
							break;
						}
					}

					if (jsonResponse == null) {
						// persist
						getModel().orders().put(order.getId(), order);
						restaurant.orders().add(order);

						// create response
						jsonResponse = createOkResponse().put("id",
								order.getId());
					}
				}
			}
		} catch (Exception e) {
			jsonResponse = createErrorResponse(e.getClass().getSimpleName()
					+ ": " + e.getMessage());
		}

		return new HttpResponse(HttpStatusCodes.OK, "application/json",
				jsonResponse.toString(2));
	}

	private HttpResponse doInfo(String method, int id, String data) {
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
				Order order = getModel().orders().get(id);

				if (user == null) {
					return new HttpResponse(HttpStatusCodes.UNAUTHORIZED);
				} else if (order == null) {
					return new HttpResponse(HttpStatusCodes.NOT_FOUND);
				} else if (!order.getUser().equals(user)
						&& !order.getRestaurant().dispatchers().contains(user)) {
					return new HttpResponse(HttpStatusCodes.FORBIDDEN);
				} else {
					jsonResponse = createOkResponse().put("user",
							order.getUser().getUsername());

					// create order object
					JSONObject jsonOrder = new JSONObject()
							.put("id", order.getId())
							.put("restaurant", order.getRestaurant().getName())
							.put("foods", order.foods())
							.put("total", order.calculatePrice());

					// add to response
					jsonResponse.put("order", jsonOrder);
				}
			}
		} catch (Exception e) {
			jsonResponse = createErrorResponse(e.getClass().getSimpleName()
					+ ": " + e.getMessage());
		}

		return new HttpResponse(HttpStatusCodes.OK, "application/json",
				jsonResponse.toString(2));
	}

	private HttpResponse doDelete(String method, int id, String data) {
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
				Order order = getModel().orders().get(id);

				if (user == null) {
					return new HttpResponse(HttpStatusCodes.UNAUTHORIZED);
				} else if (order == null) {
					return new HttpResponse(HttpStatusCodes.NOT_FOUND);
				} else if (!order.getUser().equals(user)
						&& !order.getRestaurant().dispatchers().contains(user)) {
					return new HttpResponse(HttpStatusCodes.FORBIDDEN);
				} else {
					// persist
					order.getRestaurant().orders().remove(order);
					getModel().orders().remove(order.getId());

					// create response
					jsonResponse = createOkResponse();
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

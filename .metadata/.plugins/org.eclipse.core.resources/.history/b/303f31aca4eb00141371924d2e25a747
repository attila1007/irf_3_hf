package hu.bme.mit.ftsrg.hungryelephant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Order implements Entity<Order, Integer> {
	private static int nextId = 1;
	private final int id;
	private final User user;
	private final Restaurant restaurant;
	private final List<Food> foods = new ArrayList<>();

	public Order(User user, Restaurant restaurant) {
		synchronized (Order.class) {
			this.id = nextId++;
			this.user = user;
			this.restaurant = restaurant;
		}
	}

	@Override
	public Integer getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public List<Food> foods() {
		Collections.sort(foods);
		return foods;
	}

	public int calculatePrice() {
		return foods.stream().mapToInt(food -> restaurant.menu().get(food))
				.sum();
	}

	@Override
	public String toString() {
		return String.format("Order #%d for %s, restaurant: %s", id, user,
				restaurant);
	}
}

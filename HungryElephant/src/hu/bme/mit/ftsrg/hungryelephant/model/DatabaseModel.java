package hu.bme.mit.ftsrg.hungryelephant.model;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public final class DatabaseModel {
	private final SortedMap<String, User> users = new TreeMap<>();
	private final SortedMap<String, Restaurant> restaurants = new TreeMap<>();
	private final SortedMap<String, Food> foods = new TreeMap<>();
	private final SortedMap<Integer, Order> orders = new TreeMap<>();
	private final SortedMap<String, String> pages = new TreeMap<>();
	private final SortedMap<UUID, User> sessions = new TreeMap<>();

	public SortedMap<String, User> users() {
		return users;
	}

	public SortedMap<String, Restaurant> restaurants() {
		return restaurants;
	}

	public SortedMap<String, Food> foods() {
		return foods;
	}

	public SortedMap<Integer, Order> orders() {
		return orders;
	}

	public SortedMap<String, String> pages() {
		return pages;
	}

	public SortedMap<UUID, User> sessions() {
		return sessions;
	}
}

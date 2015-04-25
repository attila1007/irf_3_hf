package hu.bme.mit.ftsrg.hungryelephant.model;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class Restaurant implements Entity<Restaurant, String> {
	private final String name;
	private final String address;
	private final SortedMap<Food, Integer> menu = new TreeMap<>();
	private final SortedSet<User> dispatchers = new TreeSet<>();
	private final SortedSet<Order> orders = new TreeSet<>();

	public Restaurant(String name, String address) {
		this.name = name;
		this.address = address;
	}

	@Override
	public String getId() {
		return name;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public SortedMap<Food, Integer> menu() {
		return menu;
	}

	public SortedSet<User> dispatchers() {
		return dispatchers;
	}

	public SortedSet<Order> orders() {
		return orders;
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", name, address);
	}
}

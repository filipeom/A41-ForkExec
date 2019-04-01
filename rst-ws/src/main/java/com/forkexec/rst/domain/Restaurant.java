package com.forkexec.rst.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Restaurant
 *
 * A restaurant server.
 *
 */
public class Restaurant {


	private Map<String, RestaurantMenu> menus = new ConcurrentHashMap<>();

	private Map<String, RestaurantMenuOrder> orders = new ConcurrentHashMap<>();

	private AtomicInteger orderIdCounter = new AtomicInteger(0);

	// Singleton -------------------------------------------------------------

	/** Private constructor prevents instantiation from other classes. */
	private Restaurant() {
		// Initialization of default values
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Restaurant INSTANCE = new Restaurant();
	}

	public static synchronized Restaurant getInstance() {
		return SingletonHolder.INSTANCE;
	}

	// Menu ------------------------------------------------------------------

	public void reset() {
		menus.clear();
		orders.clear();
		orderIdCounter.set(0);
	}

	public Set<String> getMenusIDs() {
		return menus.keySet();
	}

	public RestaurantMenu getMenu(String menuId) {
		return menus.get(menuId);
	}

	public void registerMenu(String menuId, String entree, String plate, String dessert, int price, int preparationTime, int quantity) {
		menus.put(menuId, new RestaurantMenu(menuId, entree, plate, dessert, price, preparationTime, quantity));
	}

	// MenuOrder -------------------------------------------------------------

	private String generateOrderId(String id) {
		int orderId = orderIdCounter.incrementAndGet();
		return Integer.toString(orderId);
	}

	public void decreseMenuQuantity(RestaurantMenu menu, int quantity) throws QuantityException {
		synchronized (menu) {
			int currentQuantity = menu.getQuantity();
			if ( currentQuantity < quantity ) {
				String message = String.format("Tried to order %d units of menu %s but only %d units are available.",
					quantity, menu.getMenuId(), currentQuantity);
				throw new QuantityException(message);
			}
			menu.setQuantity(currentQuantity - quantity);
		}
	}

	public RestaurantMenuOrder registerOrder(String menuId, int quantity) throws QuantityException {
		RestaurantMenu menu = getMenu(menuId);
		decreseMenuQuantity(menu, quantity);
		String id = this.generateOrderId(menuId);
		RestaurantMenuOrder order = new RestaurantMenuOrder(id, menuId, quantity);
		orders.put(id, order);
		return order;
	}

}

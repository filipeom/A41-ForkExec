package com.forkexec.hub.domain;

/**
 * Cart Item 
 *
 * Represents a item in a users cart.
 *
 */
public class CartItem {
  /** Restaurant identifier */
  private String restaurantId;
  /** Menu identifier */
  private String menuId;
  /** Quantity of item */
  private int quantity;
  /** The sub total price */
  private int price;

  /** Created a item with all arguments */
  public CartItem(String rid, String mid, int quantity, int price) {
    this.restaurantId = rid;
    this.menuId = mid;
    this.quantity = quantity;
    this.price = price;
  }

  public String getRestaurantId() {
    return this.restaurantId;
  }

  public String getMenuId() {
    return this.menuId;
  }

  public int getFoodQuantity() {
    return this.quantity;
  }

  public int getPrice() {
    return this.price;
  }
}

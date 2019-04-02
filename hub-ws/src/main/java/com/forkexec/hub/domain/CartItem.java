package com.forkexec.hub.domain;

import com.forkexec.hub.ws.FoodId;

/**
 * Cart Item 
 *
 * Represents a item in a users cart.
 *
 */
public class CartItem {
  /** Food identifier */
  private FoodId foodId;
  /** Quantity of item */
  private int quantity;
  /** The sub total price */
  private int price;

  /** Created a item with all arguments */
  public CartItem(FoodId foodId, int quantity, int price) {
    this.foodId = foodId;
    this.quantity = quantity;
    this.price = price;
  }

  public FoodId getFoodId() {
    return this.foodId;
  }

  public int getFoodQuantity() {
    return this.quantity;
  }

  public int getPrice() {
    return this.price;
  }
}

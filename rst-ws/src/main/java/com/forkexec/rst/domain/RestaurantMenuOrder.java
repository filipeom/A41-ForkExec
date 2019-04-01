package com.forkexec.rst.domain;

public class RestaurantMenuOrder {

  private String id;
  private String menuId;
  private int menuQuantity;

  public RestaurantMenuOrder(String id, String menuId, int menuQuantity) {
    this.id = id;
    this.menuId = menuId;
    this.menuQuantity = menuQuantity;
  }

  public String getId() {
    return this.id;
  }

  public String getMenuId() {
    return this.menuId;
  }

  public int getMenuQuantity() {
    return this.menuQuantity;
  }
  
}

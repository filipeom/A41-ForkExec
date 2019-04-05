package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CartContentsIT extends BaseIT {
  private String userId = "filipeom@hotmail.com";
  private FoodId foodId = new FoodId();
  private int quantity = 3;

  @Before
  public void setUp()
          throws InvalidInitFault_Exception, InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {

    List<FoodInit> foods = new ArrayList<FoodInit>();
    foodId.setRestaurantId("A41_Restaurant1");
    foodId.setMenuId("Menu1");

    {
      FoodInit foodInit = new FoodInit();
      Food food = new Food();
      FoodId id = new FoodId();
      id.setMenuId("Menu1");
      id.setRestaurantId("A41_Restaurant1");
      food.setId(id);
      food.setEntree("Canja");
      food.setPlate("Arroz_de_Marisco");
      food.setDessert("Arroz_doce");
      food.setPrice(10);
      food.setPreparationTime(25);
      foodInit.setFood(food);
      foodInit.setQuantity(5);
      foods.add(foodInit);
    }
    {
      FoodInit foodInit = new FoodInit();
      Food food = new Food();
      FoodId id = new FoodId();
      id.setMenuId("Menu2");
      id.setRestaurantId("A41_Restaurant1");
      food.setId(id);
      food.setEntree("Canja");
      food.setPlate("Caril_de_Frango");
      food.setDessert("Arroz_doce");
      food.setPrice(11);
      food.setPreparationTime(29);
      foodInit.setFood(food);
      foodInit.setQuantity(3);
      foods.add(foodInit);
    }

    hubClient.ctrlInitFood(foods);
    hubClient.addFoodToCart(userId, foodId, quantity);
  }

  @Test
  public void success()
          throws InvalidUserIdFault_Exception {
    List<FoodOrderItem> cart = hubClient.cartContents(userId);
    assertFalse(cart.isEmpty());
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void nullUserID()
          throws InvalidUserIdFault_Exception {
    hubClient.cartContents(null);
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void emptyUserID()
          throws InvalidUserIdFault_Exception {
    hubClient.cartContents("");
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void userIDWithWitheSpace()
          throws InvalidUserIdFault_Exception {
    hubClient.cartContents("jorge @gmail.com");
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void notExistingUser()
          throws InvalidUserIdFault_Exception, RuntimeException {
    hubClient.cartContents("fakeUser@gmail.com");
  }

  @Test
  public void confirmCart()
          throws InvalidUserIdFault_Exception {
    List<FoodOrderItem> cart = hubClient.cartContents(userId);

    assertFalse(cart.isEmpty());
    assertEquals(1, cart.size());
    assertEquals(quantity, cart.get(0).getFoodQuantity());
    assertEquals("Menu1", cart.get(0).getFoodId().getMenuId());
    assertEquals("A41_Restaurant1", cart.get(0).getFoodId().getRestaurantId());
  }

  @Test
  public void addAnotherFoodToUserCart()
          throws InvalidUserIdFault_Exception,InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception {

    hubClient.addFoodToCart(userId, foodId, quantity);
    List<FoodOrderItem> cart = hubClient.cartContents(userId);

    assertFalse(cart.isEmpty());
    assertEquals(2, cart.size());
  }

  @After
  public void delete(){
    hubClient.ctrlClear();
  }
}

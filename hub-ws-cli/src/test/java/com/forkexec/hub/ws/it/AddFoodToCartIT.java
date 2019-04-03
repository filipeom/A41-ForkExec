package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.forkexec.hub.ws.FoodInit;
import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;
import com.forkexec.hub.ws.FoodOrderItem;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.Menu;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidFoodQuantityFault_Exception;
import com.forkexec.hub.ws.InvalidFoodIdFault_Exception;

public class AddFoodToCartIT extends BaseIT {

  String userId = "Filipe";
  FoodId foodId = new FoodId();
  int quantity = 3;

  @Before
  public void setUp() throws InvalidInitFault_Exception {

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
  }

  //Success tests
  @Test
  public void success() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    hubClient.addFoodToCart(userId, foodId, quantity);
    List<FoodOrderItem> items = hubClient.cartContents(userId);
    assertEquals(items.get(0).getFoodId().getRestaurantId(), foodId.getRestaurantId());
    assertEquals(items.get(0).getFoodId().getMenuId(), foodId.getMenuId());
    assertEquals(items.get(0).getFoodQuantity(), quantity);
  }

  //Input Tests
  @Test(expected = InvalidUserIdFault_Exception.class)
  public void nullUserIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    hubClient.addFoodToCart(null, foodId, quantity);
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void emptyUserIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    hubClient.addFoodToCart("", foodId, quantity);
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void whitespaceUserIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    hubClient.addFoodToCart(" ", foodId, quantity);
  }

  @Test(expected = InvalidFoodIdFault_Exception.class)
  public void nullFoodIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    hubClient.addFoodToCart(userId, null, quantity);
  }

  @Test(expected =  InvalidFoodQuantityFault_Exception.class)
  public void zeroQuantityTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    hubClient.addFoodToCart(userId, foodId, 0);
  }

  @Test(expected =  InvalidFoodQuantityFault_Exception.class)
  public void negativeQuantityTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    hubClient.addFoodToCart(userId, foodId, -1);
  }

  @Test(expected =  InvalidFoodIdFault_Exception.class)
  public void nullMenuIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    FoodId id = new FoodId();
    id.setRestaurantId("A41_Restaurant1");
    id.setMenuId(null);
    hubClient.addFoodToCart(userId, id, quantity);
  }

  @Test(expected =  InvalidFoodIdFault_Exception.class)
  public void emptyMenuIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    FoodId id = new FoodId();
    id.setRestaurantId("A41_Restaurant1");
    id.setMenuId("");
    hubClient.addFoodToCart(userId, id, quantity);
  }

  @Test(expected =  InvalidFoodIdFault_Exception.class)
  public void whitespaceMenuIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    FoodId id = new FoodId();
    id.setRestaurantId("A41_Restaurant1");
    id.setMenuId(" ");
    hubClient.addFoodToCart(userId, id, quantity);
  }

  @Test(expected =  InvalidFoodIdFault_Exception.class)
  public void nullRestaurantIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    FoodId id = new FoodId();
    id.setRestaurantId(null);
    id.setMenuId("Menu1");
    hubClient.addFoodToCart(userId, id, quantity);
  }

  @Test(expected =  InvalidFoodIdFault_Exception.class)
  public void emptyRestaurantIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    FoodId id = new FoodId();
    id.setRestaurantId("");
    foodId.setMenuId("Menu1");
    hubClient.addFoodToCart(userId, id, quantity);
  }

  @Test(expected =  InvalidFoodIdFault_Exception.class)
  public void whitespaceRestaurantIdTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    FoodId id = new FoodId();
    id.setRestaurantId(" ");
    id.setMenuId("Menu1");
    hubClient.addFoodToCart(userId, id, quantity);
  }

  @After
  public void delete(){
    hubClient.ctrlClear();
  }

}

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
import com.forkexec.hub.ws.FoodOrder;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import com.forkexec.hub.ws.EmptyCartFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.NotEnoughPointsFault_Exception;
import com.forkexec.hub.ws.InvalidFoodQuantityFault_Exception;
import com.forkexec.hub.ws.InvalidFoodIdFault_Exception;
import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;


public class OrderCartIT extends BaseIT {

  String userId = "filipeom@hotmail.com";
  FoodId foodId1 = new FoodId();
  FoodId foodId2 = new FoodId();
  FoodId foodId3 = new FoodId();
  int quantity = 1;
  String creditCardNumber = "4024007102923926";

  @Before
  public void setUp() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {

      List<FoodInit> foods = new ArrayList<FoodInit>();
      foodId1.setRestaurantId("A41_Restaurant1");
      foodId1.setMenuId("Menu1");
      foodId2.setRestaurantId("A41_Restaurant1");
      foodId2.setMenuId("Menu2");
      foodId3.setRestaurantId("A41_Restaurant1");
      foodId3.setMenuId("Menu3");
      hubClient.activateAccount(userId);

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
        food.setPrice(50);
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
        food.setPrice(100);
        food.setPreparationTime(29);
        foodInit.setFood(food);
        foodInit.setQuantity(3);
        foods.add(foodInit);
      }
      {
        FoodInit foodInit = new FoodInit();
        Food food = new Food();
        FoodId id = new FoodId();
        id.setMenuId("Menu3");
        id.setRestaurantId("A41_Restaurant1");
        food.setId(id);
        food.setEntree("Canja");
        food.setPlate("Arroz_de_pato");
        food.setDessert("Mousse");
        food.setPrice(200);
        food.setPreparationTime(29);
        foodInit.setFood(food);
        foodInit.setQuantity(1);
        foods.add(foodInit);
      }

    hubClient.ctrlInitFood(foods);
  }

  //Success tests
  @Test
  public void success() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception {
    hubClient.addFoodToCart(userId, foodId1, quantity);
    FoodOrder foodOrder = hubClient.orderCart(userId);
    assertEquals(foodOrder.getItems().size(), 1);
    assertEquals(foodOrder.getItems().get(0).getFoodId().getRestaurantId(), foodId1.getRestaurantId());
    assertEquals(foodOrder.getItems().get(0).getFoodId().getMenuId(), foodId1.getMenuId());
  }

  @Test
  public void successWithPointsLimit() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception {
    hubClient.addFoodToCart(userId, foodId2, quantity);
    FoodOrder foodOrder = hubClient.orderCart(userId);
    assertEquals(foodOrder.getItems().size(), 1);
    assertEquals(foodOrder.getItems().get(0).getFoodId().getRestaurantId(), foodId2.getRestaurantId());
    assertEquals(foodOrder.getItems().get(0).getFoodId().getMenuId(), foodId2.getMenuId());
  }

  @Test
  public void successWithTwoItems() throws InvalidMoneyFault_Exception, InvalidCreditCardFault_Exception, EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception {
    hubClient.addFoodToCart(userId, foodId1, quantity);
    hubClient.addFoodToCart(userId, foodId2, quantity);
    hubClient.loadAccount(userId, 50, creditCardNumber);
    FoodOrder foodOrder = hubClient.orderCart(userId);
    assertEquals(foodOrder.getItems().size(), 2);
    assertEquals(foodOrder.getItems().get(0).getFoodId().getRestaurantId(), foodId1.getRestaurantId());
    assertEquals(foodOrder.getItems().get(0).getFoodId().getMenuId(), foodId1.getMenuId());
    assertEquals(foodOrder.getItems().get(1).getFoodId().getRestaurantId(), foodId2.getRestaurantId());
    assertEquals(foodOrder.getItems().get(1).getFoodId().getMenuId(), foodId2.getMenuId());
  }

  //Input Tests
  @Test(expected = InvalidUserIdFault_Exception.class)
  public void nullUserIdTest() throws InvalidFoodQuantityFault_Exception, EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {
    hubClient.orderCart(null);
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void emptyUserIdTest() throws InvalidFoodQuantityFault_Exception, EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {
    hubClient.orderCart("");
  }

  @Test(expected = InvalidUserIdFault_Exception.class)
  public void whitespaceUserIdTest() throws InvalidFoodQuantityFault_Exception, EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {
    hubClient.orderCart(" ");
  }

  @Test(expected = EmptyCartFault_Exception.class)
  public void emptyCartTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {
    hubClient.addFoodToCart(userId, foodId1, quantity);
    hubClient.clearCart(userId);
    hubClient.orderCart(userId);
  }

  @Test(expected = NotEnoughPointsFault_Exception.class)
  public void notEnoughPointsTest() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {
    hubClient.addFoodToCart(userId, foodId3, quantity);
    hubClient.orderCart(userId);
  }

  @After
  public void delete(){
    hubClient.ctrlClear();
  }

}

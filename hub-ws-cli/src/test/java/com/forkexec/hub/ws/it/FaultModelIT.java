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
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.Menu;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.hub.ws.InvalidFoodQuantityFault_Exception;
import com.forkexec.hub.ws.InvalidFoodIdFault_Exception;
import com.forkexec.hub.ws.EmptyCartFault_Exception;
import com.forkexec.hub.ws.NotEnoughPointsFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;

public class FaultModelIT extends BaseIT {

  String userId = "filipeom@hotmail.com";
  int money = 50;
  int quantity = 5;
  String creditCardNumber = "4024007102923926";
  FoodId foodId = new FoodId();

  @Before
  public void setUp() throws InvalidUserIdFault_Exception, InvalidInitFault_Exception {

    List<FoodInit> foods = new ArrayList<FoodInit>();
    foodId.setMenuId("Menu1");
    foodId.setRestaurantId("A41_Restaurant1");

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
      food.setPrice(1000);
      food.setPreparationTime(25);
      foodInit.setFood(food);
      foodInit.setQuantity(5);
      foods.add(foodInit);
    }

    hubClient.ctrlInitFood(foods);
    hubClient.activateAccount(userId);
  }

  @Test
  public void faultTest() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception,
    InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, EmptyCartFault_Exception, NotEnoughPointsFault_Exception {
    hubClient.loadAccount(userId, money, creditCardNumber);
    hubClient.addFoodToCart(userId, foodId, quantity);
    hubClient.orderCart(userId);
    assertEquals(hubClient.accountBalance(userId), 600);
  }

  @After
  public void delete(){
    hubClient.ctrlClear();
  }

}

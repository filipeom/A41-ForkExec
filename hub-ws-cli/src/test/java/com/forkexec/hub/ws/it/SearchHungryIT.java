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
import com.forkexec.hub.ws.InvalidTextFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;


public class SearchHungryIT extends BaseIT {

  @Before
  public void setUp() throws InvalidInitFault_Exception {

      List<FoodInit> foods = new ArrayList<FoodInit>();

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
        food.setPreparationTime(10);
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
        food.setPlate("Pizza");
        food.setDessert("Mousse");
        food.setPrice(8);
        food.setPreparationTime(18);
        foodInit.setFood(food);
        foodInit.setQuantity(2);
        foods.add(foodInit);
      }

    hubClient.ctrlInitFood(foods);
  }

  //Success tests
  @Test
  public void success() throws InvalidTextFault_Exception {
    List<Food> foods = hubClient.searchHungry("Canja");
    assertEquals(3, foods.size());
    assertEquals(foods.get(0).getId().getMenuId(), "Menu2");
    assertEquals(foods.get(1).getId().getMenuId(), "Menu3");
    assertEquals(foods.get(2).getId().getMenuId(), "Menu1");
  }

  @Test
  public void oneItemSuccess() throws InvalidTextFault_Exception {
    List<Food> foods = hubClient.searchHungry("Mousse");
    assertEquals(1, foods.size());
    assertEquals(foods.get(0).getId().getMenuId(), "Menu3");
  }

  @Test
  public void anotherOneItemSuccess() throws InvalidTextFault_Exception {
    List<Food> foods = hubClient.searchHungry("Frango");
    assertEquals(1, foods.size());
    assertEquals(foods.get(0).getId().getMenuId(), "Menu2");
  }

  @Test
  public void noMenuExist() throws InvalidTextFault_Exception {
    List<Food> foods = hubClient.searchHungry("galinha");
    assertEquals(0, foods.size());
  }

  @Test
  public void lowercaseNotExists() throws InvalidTextFault_Exception {
    List<Food> foods = hubClient.searchHungry("canja");
    assertEquals(0, foods.size());
  }

  //Input Tests
  @Test(expected = InvalidTextFault_Exception.class)
  public void nullIdTest() throws InvalidTextFault_Exception {
    hubClient.searchHungry(null);
  }

  @Test(expected = InvalidTextFault_Exception.class)
  public void emptyIdTest() throws InvalidTextFault_Exception {
    hubClient.searchHungry("");
  }

  @Test(expected = InvalidTextFault_Exception.class)
  public void whitespaceIdTest() throws InvalidTextFault_Exception {
    hubClient.searchHungry(" ");
  }

  @After
  public void delete(){
    hubClient.ctrlClear();
  }

}

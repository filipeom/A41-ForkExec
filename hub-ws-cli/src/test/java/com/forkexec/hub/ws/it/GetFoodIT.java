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
import com.forkexec.rst.ws.BadInitFault_Exception;
import com.forkexec.hub.ws.InvalidFoodIdFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;

public class GetFoodIT extends BaseIT {

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
       food.setEntree("SopadeLegumes");
       food.setPlate("ArrozdeMarisco");
       food.setDessert("Arrozdoce");
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
       food.setPlate("CarildeFrango");
       food.setDessert("Arrozdoce");
       food.setPrice(11);
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
   public void oneItemSuccess() throws InvalidFoodIdFault_Exception {
     FoodId foodId = new FoodId();
     foodId.setRestaurantId("A41_Restaurant1");
     foodId.setMenuId("Menu1");
     Food food = hubClient.getFood(foodId);

     assertEquals(food.getEntree(), "SopadeLegumes");
     assertEquals(food.getPlate(), "ArrozdeMarisco");
     assertEquals(food.getDessert(), "Arrozdoce");
     assertEquals(food.getPrice(), 10);
     assertEquals(food.getPreparationTime(), 25);
   }

   @Test
   public void anotherOneItemSuccess() throws InvalidFoodIdFault_Exception {
     FoodId foodId = new FoodId();
     foodId.setRestaurantId("A41_Restaurant1");
     foodId.setMenuId("Menu3");
     Food food = hubClient.getFood(foodId);

     assertEquals(food.getEntree(), "Canja");
     assertEquals(food.getPlate(), "Pizza");
     assertEquals(food.getDessert(), "Mousse");
     assertEquals(food.getPrice(), 8);
     assertEquals(food.getPreparationTime(), 18);
   }

   //Input Tests
   @Test(expected = InvalidFoodIdFault_Exception.class)
   public void nullFoodIdTest() throws InvalidFoodIdFault_Exception {
     hubClient.getFood(null);
   }

   @Test(expected = InvalidFoodIdFault_Exception.class)
   public void emptyMenuIdTest() throws InvalidFoodIdFault_Exception {
     FoodId foodId = new FoodId();
     foodId.setRestaurantId("A41_Restaurant1");
     foodId.setMenuId("");
     Food food = hubClient.getFood(foodId);
   }

   @Test(expected = InvalidFoodIdFault_Exception.class)
   public void emptyRestaurantIdTest() throws InvalidFoodIdFault_Exception {
     FoodId foodId = new FoodId();
     foodId.setRestaurantId("");
     foodId.setMenuId("Menu1");
     Food food = hubClient.getFood(foodId);
   }

   @Test(expected = InvalidFoodIdFault_Exception.class)
   public void whitespaceMenuIdTest() throws InvalidFoodIdFault_Exception {
     FoodId foodId = new FoodId();
     foodId.setRestaurantId("A41_Restaurant1");
     foodId.setMenuId(" ");
     Food food = hubClient.getFood(foodId);
   }

   @Test(expected = InvalidFoodIdFault_Exception.class)
   public void whitespaceRestaurantIdTest() throws InvalidFoodIdFault_Exception {
     FoodId foodId = new FoodId();
     foodId.setRestaurantId(" ");
     foodId.setMenuId("Menu1");
     Food food = hubClient.getFood(foodId);
   }

   @Test(expected = InvalidFoodIdFault_Exception.class)
   public void nullRestaurantIdTest() throws InvalidFoodIdFault_Exception {
     FoodId foodId = new FoodId();
     foodId.setRestaurantId(null);
     foodId.setMenuId("Menu1");
     Food food = hubClient.getFood(foodId);
   }

   @Test(expected = InvalidFoodIdFault_Exception.class)
   public void nullMenuIdTest() throws InvalidFoodIdFault_Exception {
     FoodId foodId = new FoodId();
     foodId.setRestaurantId("A41_Restaurant1");
     foodId.setMenuId(null);
     Food food = hubClient.getFood(foodId);
   }

   @After
   public void delete(){
     hubClient.ctrlClear();
   }
}

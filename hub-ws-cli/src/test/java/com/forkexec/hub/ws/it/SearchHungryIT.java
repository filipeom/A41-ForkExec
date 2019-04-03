package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.forkexec.rst.ws.MenuInit;
import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.BadInitFault_Exception;
import com.forkexec.hub.ws.InvalidTextFault_Exception;
import com.forkexec.hub.ws.Food;

public class SearchHungryIT extends BaseIT {

  @Before
  public void setUp() throws BadInitFault_Exception{

      List<MenuInit> menus1 = new ArrayList<MenuInit>();
      List<MenuInit> menus2 = new ArrayList<MenuInit>();

    {
      MenuInit menuInit = new MenuInit();
      Menu menu = new Menu();
      MenuId id = new MenuId();
      id.setId("Menu1");
      menu.setId(id);
      menu.setEntree("SopadeLegumes");
      menu.setPlate("ArrozdeMarisco");
      menu.setDessert("Arrozdoce");
      menu.setPrice(10);
      menu.setPreparationTime(25);
      menuInit.setMenu(menu);
      menuInit.setQuantity(5);
      menus1.add(menuInit);
    }
    {
      MenuInit menuInit = new MenuInit();
      Menu menu = new Menu();
      MenuId id = new MenuId();
      id.setId("Menu2");
      menu.setId(id);
      menu.setEntree("Canja");
      menu.setPlate("CarildeFrango");
      menu.setDessert("Arrozdoce");
      menu.setPrice(11);
      menu.setPreparationTime(15);
      menuInit.setMenu(menu);
      menuInit.setQuantity(3);
      menus1.add(menuInit);
    }
    {
      MenuInit menuInit = new MenuInit();
      Menu menu = new Menu();
      MenuId id = new MenuId();
      id.setId("Menu3");
      menu.setId(id);
      menu.setEntree("Canja");
      menu.setPlate("Pizza");
      menu.setDessert("Mousse");
      menu.setPrice(8);
      menu.setPreparationTime(18);
      menuInit.setMenu(menu);
      menuInit.setQuantity(2);
      menus2.add(menuInit);
    }
    {
      MenuInit menuInit = new MenuInit();
      Menu menu = new Menu();
      MenuId id = new MenuId();
      id.setId("Menu4");
      menu.setId(id);
      menu.setEntree("SopadePeixe");
      menu.setPlate("Arrozdepato");
      menu.setDessert("Gelatina");
      menu.setPrice(8);
      menu.setPreparationTime(20);
      menuInit.setMenu(menu);
      menuInit.setQuantity(9);
      menus2.add(menuInit);
    }

    restaurantClients.get(0).ctrlInit(menus1);
    restaurantClients.get(1).ctrlInit(menus2);
  }

  //Success tests
  @Test
  public void success() throws InvalidTextFault_Exception {
    List<Food> foods = hubClient.searchHungry("Canja");
    assertEquals(2, foods.size());
    assertEquals(foods.get(0).getId().getRestaurantId(), "A41_Restaurant1");
    assertEquals(foods.get(1).getId().getRestaurantId(), "A41_Restaurant2");
  }

  @Test
  public void oneItemSuccess() throws InvalidTextFault_Exception {
    List<Food> foods = hubClient.searchHungry("Mousse");
    assertEquals(1, foods.size());
    assertEquals(foods.get(0).getId().getRestaurantId(), "A41_Restaurant2");
  }

  @Test
  public void anotherOneItemSuccess() throws InvalidTextFault_Exception {
    List<Food> foods = hubClient.searchHungry("Frango");
    assertEquals(1, foods.size());
    assertEquals(foods.get(0).getId().getRestaurantId(), "A41_Restaurant1");
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

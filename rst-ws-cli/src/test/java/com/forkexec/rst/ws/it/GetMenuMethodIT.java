package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GetMenuMethodIT extends BaseIT {

  private String MENUID = "2";
  private String PLATE = "plate";
  private String DESSERT = "dessert";
  private String ENTREE = "entree";
  private int PRICE = 2;
  private int PREPARATION_TIME = 3;
  private int QUANTITY = 4;


  private MenuId createMenuId(String id) {
    MenuId menuId = new MenuId();
    menuId.setId(id);
    return menuId;
  }

  private Menu createMenu(MenuId menuId, String dessert, String plate, String entree,
                          int price, int preparation_time) {
    Menu menu = new Menu();
    menu.setDessert(dessert);
    menu.setPlate(plate);
    menu.setEntree(entree);
    menu.setPrice(price);
    menu.setPreparationTime(preparation_time);
    menu.setId(menuId);
    return menu;
  }

  private MenuInit createMenuInit(Menu menu, int quantity) {

    MenuInit menuInit = new MenuInit();
    menuInit.setMenu(menu);
    menuInit.setQuantity(quantity);
    return menuInit;
  }

  @Before
  public void setUp() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId(MENUID);
    Menu menu = createMenu(menuId, DESSERT, PLATE, ENTREE, PRICE, PREPARATION_TIME);
    MenuInit menuInit = createMenuInit(menu, QUANTITY);
    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @After
  public void cleanUp() {
    client.ctrlClear();
  }

  @Test
  public void success() throws BadMenuIdFault_Exception {
    MenuId menuid = createMenuId(MENUID);

    assertEquals(MENUID, client.getMenu(menuid).getId().getId());
  }

  @Test(expected = BadMenuIdFault_Exception.class)
  public void getMenuWithNullID() throws BadMenuIdFault_Exception {
    MenuId menuId = createMenuId(null);
    client.getMenu(menuId);
  }

  @Test(expected = BadMenuIdFault_Exception.class)
  public void getMenuWithEmptyId() throws BadMenuIdFault_Exception {
    MenuId menuId = createMenuId("");
    client.getMenu(menuId);
  }

  @Test(expected = BadMenuIdFault_Exception.class)
  public void getMenuWithWitheSpaces() throws BadMenuIdFault_Exception {
    MenuId menuId = createMenuId("0 2");
    client.getMenu(menuId);
  }

  @Test(expected = BadMenuIdFault_Exception.class)
  public void tryToGetNotExistingMenu() throws BadMenuIdFault_Exception {
    MenuId menuId = createMenuId("1");
    client.getMenu(menuId);
  }

}

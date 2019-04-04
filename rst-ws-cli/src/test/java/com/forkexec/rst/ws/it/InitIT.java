package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

public class InitIT extends BaseIT {

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
  public void setUp() {
  }

  @After
  public void cleanUp() {
    client.ctrlClear();
  }

  @Test(expected = BadInitFault_Exception.class)
  public void NullInitialMenu() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    menuInitList.add(null);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void nullMenu() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    Menu menu = createMenu(null, "dessert", "plate", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void menuIdWithNullId() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId(null);
    Menu menu = createMenu(menuId, "dessert", "plate", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void menuIdWithEmptyId() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("");
    Menu menu = createMenu(menuId, "dessert", "plate", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void menuIdWithWhiteSpace() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("10 2");
    Menu menu = createMenu(menuId, "dessert", "plate", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithNullEntree() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "plate", null, 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithEmptyEntree() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "plate", "", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithWhiteSpaceInEntree() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "plate", "entr ee", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithNullPlate() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", null, "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithEmptyPlate() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithWhiteSpaceInPlate() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "pla te", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithNullDessert() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, null, "plate", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithEmptyDessert() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "", "plate", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithWhiteSpaceInDessert() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "des s ert", "plate", "entree", 2, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }


  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithPriceNegative() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "plate", "entree", -1, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }


  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithPriceZero() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "plate", "entree", 0, 2);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithNegativePreparationTime() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "plate", "entree", 1, -1);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }


  @Test(expected = BadInitFault_Exception.class)
  public void MenuWithPreparationTimeZero() throws BadInitFault_Exception {
    List<MenuInit> menuInitList = new ArrayList<>();

    MenuId menuId = createMenuId("0");
    Menu menu = createMenu(menuId, "dessert", "plate", "entree", 1, 0);
    MenuInit menuInit = createMenuInit(menu, 2);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

}
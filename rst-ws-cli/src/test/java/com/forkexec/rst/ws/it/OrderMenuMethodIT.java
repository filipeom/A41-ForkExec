package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class OrderMenuMethodIT extends BaseIT {
  private int QUANTITY = 3;
  private String MENU_ID = "0";


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

    MenuId menuId = createMenuId(MENU_ID);
    Menu menu = createMenu(menuId, "dessert", "plate", "entree", 1, 2);
    MenuInit menuInit = createMenuInit(menu, QUANTITY);

    menuInitList.add(menuInit);

    client.ctrlInit(menuInitList);
  }

  @After
  public void cleanUp() {
    client.ctrlClear();
  }

  @Test
  public void success()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(MENU_ID);
    MenuOrder menuOrder = client.orderMenu(menuId, QUANTITY);

    assertEquals("1", menuOrder.getId().getId());
    assertEquals("0", menuOrder.getMenuId().getId());
    assertEquals(QUANTITY, menuOrder.getMenuQuantity());
  }

  @Test(expected = BadMenuIdFault_Exception.class)
  public void NullMenuID()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    client.orderMenu(null, QUANTITY);
  }

  @Test(expected = BadMenuIdFault_Exception.class)
  public void MenuIDWithNullID()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(null);

    client.orderMenu(menuId, QUANTITY);
  }


  @Test(expected = BadMenuIdFault_Exception.class)
  public void MenuIDWithEmptyID()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId("");

    client.orderMenu(menuId, QUANTITY);
  }


  @Test(expected = BadMenuIdFault_Exception.class)
  public void MenuIDWithWhiteSpacedID()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId("0 1");

    client.orderMenu(menuId, QUANTITY);
  }

  //leading and trailing spaces should be removed
  @Test
  public void MenuIDWithWhiteSpacedIDSuccess()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(" 0 ");

    MenuOrder menuOrder = client.orderMenu(menuId, QUANTITY);

    assertEquals("1", menuOrder.getId().getId());
    assertEquals("0", menuOrder.getMenuId().getId());
    assertEquals(QUANTITY, menuOrder.getMenuQuantity());
  }

  @Test(expected = BadMenuIdFault_Exception.class)
  public void MenuIDWithWhiteSpacedIDNoSuccess()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(" 1 ");

    client.orderMenu(menuId, QUANTITY);
  }

  @Test(expected = BadMenuIdFault_Exception.class)
  public void NonExistingMenuID()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId("2");

    client.orderMenu(menuId, QUANTITY);
  }

  @Test(expected = BadQuantityFault_Exception.class)
  public void NegativeQuantity()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(MENU_ID);

    client.orderMenu(menuId, -1);
  }

  @Test(expected = BadQuantityFault_Exception.class)
  public void OrderNoMenu()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(MENU_ID);

    client.orderMenu(menuId, 0);
  }


  @Test(expected = BadQuantityFault_Exception.class)
  public void OrderMoreQuantityThanExists()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(MENU_ID);

    client.orderMenu(menuId, 0);
  }

  @Test
  public void OrderMinusOneOfExistingQuantity()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(MENU_ID);

    MenuOrder menuOrder = client.orderMenu(menuId, QUANTITY - 1);

    assertEquals("1", menuOrder.getId().getId());
    assertEquals("0", menuOrder.getMenuId().getId());
    assertEquals(QUANTITY - 1, menuOrder.getMenuQuantity());

  }

  @Test(expected = InsufficientQuantityFault_Exception.class)
  public void OrderOneMoreOfExistingQuantity()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(MENU_ID);

    client.orderMenu(menuId, QUANTITY + 1);
  }

  // More than one order
  @Test
  public void orderTwiceCheckOrderId()
          throws BadMenuIdFault_Exception, BadQuantityFault_Exception,
          InsufficientQuantityFault_Exception {

    MenuId menuId = createMenuId(MENU_ID);

    MenuOrder menuOrder = client.orderMenu(menuId, 2);

    assertEquals("1", menuOrder.getId().getId());
    assertEquals("0", menuOrder.getMenuId().getId());
    assertEquals(2, menuOrder.getMenuQuantity());

    menuOrder = client.orderMenu(menuId, 1);
    assertEquals("2", menuOrder.getId().getId());
    assertEquals("0", menuOrder.getMenuId().getId());
    assertEquals(1, menuOrder.getMenuQuantity());
  }
}

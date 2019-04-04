package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SearchMenusMethodIT extends BaseIT {

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

    MenuId menuId0 = createMenuId("0");
    Menu menu0 = createMenu(menuId0, "dessert", "plate", "entree", 1, 2);
    MenuInit menuInit0 = createMenuInit(menu0, 2);

    MenuId menuId1 = createMenuId("1");
    Menu menu1 = createMenu(menuId1, "sobremesa", "prato", "ENTRADA", 3, 4);
    MenuInit menuInit1 = createMenuInit(menu1, 5);


    menuInitList.add(menuInit0);
    menuInitList.add(menuInit1);

    client.ctrlInit(menuInitList);
  }

  @After
  public void cleanUp() {
    client.ctrlClear();
  }

  @Test
  public void success() throws BadTextFault_Exception {
    List<Menu> menus = client.searchMenus("dessert");

    assertFalse(menus.isEmpty());
    assertEquals(1, menus.size());
    assertEquals("dessert", menus.get(0).getDessert());
    assertEquals("0", menus.get(0).getId().getId());
  }

  @Test(expected = BadTextFault_Exception.class)
  public void searchNullDescription() throws BadTextFault_Exception {
    client.searchMenus(null);
  }


  @Test(expected = BadTextFault_Exception.class)
  public void searchWithEmptyDescription() throws BadTextFault_Exception {
    client.searchMenus("");
  }


  @Test(expected = BadTextFault_Exception.class)
  public void descriptionWithWhiteSpace() throws BadTextFault_Exception {
    client.searchMenus("de ssert");
  }

  @Test
  public void partialExistingDescription() throws BadTextFault_Exception {
    List<Menu> menus = client.searchMenus("pla");

    assertFalse(menus.isEmpty());
    assertEquals(1, menus.size());
    assertEquals("0", menus.get(0).getId().getId());
    assertEquals("plate", menus.get(0).getPlate());
  }

  @Test
  public void partialExistingDescriptionInBothMenus() throws BadTextFault_Exception {
    List<Menu> menus = client.searchMenus("p");

    String id0 = menus.get(0).getId().getId();
    String id1 = menus.get(1).getId().getId();

    assertFalse(menus.isEmpty());
    assertEquals(2, menus.size());
    assertTrue(("0".equals(id0) && "1".equals(id1)) ||
            ("1".equals(id0) && "0".equals(id1)));


  }

  @Test
  public void notExistingDescription() throws BadTextFault_Exception {
    List<Menu> menus = client.searchMenus("pizza");

    assertTrue(menus.isEmpty());
  }

  @Test
  public void notExistingDescriptionExtraCharacters() throws BadTextFault_Exception {
    List<Menu> menus = client.searchMenus("entreee");

    assertTrue(menus.isEmpty());
  }

  //CASE SENSITIVE
  @Test
  public void caseSensitiveSimilarEntrees() throws BadTextFault_Exception {
    //Menus have 'entree' and 'ENTRADA' as entree's description
    List<Menu> menus = client.searchMenus("eN");

    assertTrue(menus.isEmpty());
  }

  @Test
  public void similarEntreesCaseSensitive() throws BadTextFault_Exception {
    //Menus have 'entree' and 'ENTRADA' as entree's description
    List<Menu> menus = client.searchMenus("EN");

    assertFalse(menus.isEmpty());
    assertEquals(1, menus.size());
    assertEquals("1", menus.get(0).getId().getId());
    assertEquals("ENTRADA", menus.get(0).getEntree());
  }

  @Test
  public void caseSensitiveEntree() throws BadTextFault_Exception {
    //Menus have 'entree' and 'ENTRADA' as entree's description
    List<Menu> menus = client.searchMenus("eNTRADA");

    assertTrue(menus.isEmpty());
  }


}
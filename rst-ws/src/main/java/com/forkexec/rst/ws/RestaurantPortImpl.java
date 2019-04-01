package com.forkexec.rst.ws;

import java.util.List;
import java.util.ArrayList;

import javax.jws.WebService;

import com.forkexec.rst.domain.Restaurant;
import com.forkexec.rst.domain.RestaurantMenu;
import com.forkexec.rst.domain.RestaurantMenuOrder;
import com.forkexec.rst.domain.QuantityException;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.rst.ws.RestaurantPortType",
            wsdlLocation = "RestaurantService.wsdl",
            name ="RestaurantWebService",
            portName = "RestaurantPort",
            targetNamespace="http://ws.rst.forkexec.com/",
            serviceName = "RestaurantService"
)
public class RestaurantPortImpl implements RestaurantPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private RestaurantEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public RestaurantPortImpl(RestaurantEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------


	public Menu getMenu(MenuId menuId) throws BadMenuIdFault_Exception {
		if ( menuId.getId() == null )
      throwBadMenuIdFault("Menu identifier cannot be null!");
    String id = menuId.getId().trim();
    if ( id.length() == 0 )
      throwBadMenuIdFault("Menu identifier cannot be empty or whitespace!");

    Restaurant restaurant = Restaurant.getInstance();
    RestaurantMenu rm = restaurant.getMenu(id);
    Menu menu = newMenu(rm);

    if ( menu != null )
      return menu;

		return null;
	}


	@Override
	public List<Menu> searchMenus(String descriptionText) throws BadTextFault_Exception {
		if ( descriptionText == null )
      throwBadTextFault("Text cannot be null!");
    String text = descriptionText.trim();
    if ( text.length() == 0 )
      throwBadTextFault("Text cannot be empty or whitespace!");

    Restaurant restaurant = Restaurant.getInstance();
    List<Menu> menus = new ArrayList<Menu>();
    for( String menuId : restaurant.getMenusIDs() ) {
      RestaurantMenu rm = restaurant.getMenu(menuId);
      if( rm.getEntree().contains(descriptionText) || rm.getPlate().contains(descriptionText) || rm.getDessert().contains(descriptionText) ) {
        Menu menu = newMenu(rm);
        menus.add(menu);
      }
    }
		return menus;
	}

	@Override
	public MenuOrder orderMenu(MenuId arg0, int arg1)
			throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {

    String id = arg0.getId();
    int quantity = arg1;

    if ( id == null )
      throwBadMenuIdFault("Menu identifier cannot be null!");
    id = id.trim();
    if ( id.length() == 0 )
      throwBadMenuIdFault("Menu identifier cannot be empty or whitespace!");

    if ( quantity <= 0 )
      throwBadQuantityFault("Quantity must be greater than 0!");

    Restaurant restaurant = Restaurant.getInstance();

    try {
      RestaurantMenuOrder order = restaurant.registerOrder(id, quantity);
      MenuOrder menuOrder = newMenuOrder(order);
      return menuOrder;
    }
    catch(QuantityException qe) {
      throwInsufficientQuantityFault("Cannot order that quantity!");
    }

    return null;
	}

	// Control operations ----------------------------------------------------

	/** Diagnostic operation to check if service is running. */
	@Override
	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the park does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Restaurant";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
    Restaurant.getInstance().reset();
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInit(List<MenuInit> initialMenus) throws BadInitFault_Exception {
    for (MenuInit m : initialMenus) {

      if ( m == null )
        throwBadInitFault("Menu cannot be null!");

      String id = m.getMenu().getId().getId();
      if ( id == null)
        throwBadInitFault("Menu identifier cannot be null!");
      id = id.trim();
      if ( id.length() == 0)
        throwBadInitFault("Menu identifier cannot be empty or whitespace!");

      String entree = m.getMenu().getEntree();
      if ( entree == null )
       	throwBadInitFault("Menu entree cannot be null!");
      entree = entree.trim();
      if ( entree.length() == 0)
        throwBadInitFault("Menu entree cannot be empty or whitespace!");

      String plate = m.getMenu().getPlate();
      if ( plate == null )
       	throwBadInitFault("Menu plate cannot be null!");
      plate = plate.trim();
      if ( plate.length() == 0)
        throwBadInitFault("Menu plate cannot be empty or whitespace!");

      String dessert = m.getMenu().getDessert();
      if ( dessert == null )
       	throwBadInitFault("Menu dessert cannot be null!");
      dessert = dessert.trim();
      if ( dessert.length() == 0)
        throwBadInitFault("Menu dessert cannot be empty or whitespace!");

      int price = m.getMenu().getPrice();
      if ( price <= 0 )
        throwBadInitFault("Price must be a positive number!");

      int preparationTime = m.getMenu().getPreparationTime();
      if ( preparationTime <= 0 )
        throwBadInitFault("Preparation time must be a positive number!");

      int quantity = m.getQuantity();
      if ( quantity <= 0 )
        throwBadInitFault("Quantity must be a positive number!");

      Restaurant restaurant = Restaurant.getInstance();
      restaurant.registerMenu(id, entree, plate, dessert, price, preparationTime, quantity);
    }
	}

	// View helpers ----------------------------------------------------------

  private MenuId newMenuId(String id) {
    MenuId menuId = new MenuId();
    menuId.setId(id);
    return menuId;
  }

  private Menu newMenu(RestaurantMenu rm) {
    Menu menu = new Menu();
    MenuId id = newMenuId( rm.getMenuId() );
    menu.setId(id);
    menu.setEntree( rm.getEntree() );
    menu.setPlate( rm.getPlate() );
    menu.setDessert( rm.getDessert() );
    menu.setPrice( rm.getPrice() );
    menu.setPreparationTime( rm.getPreparationTime() );
    return menu;
  }

  private MenuOrderId newMenuOrderId(String id) {
    MenuOrderId menuOrderId = new MenuOrderId();
    menuOrderId.setId(id);
    return menuOrderId;
  }

  private MenuOrder newMenuOrder(RestaurantMenuOrder order) {
    MenuOrder menuOrder = new MenuOrder();
    MenuOrderId id = newMenuOrderId( order.getId() );
    MenuId menuId = newMenuId( order.getMenuId() );
    menuOrder.setId(id);
    menuOrder.setMenuId(menuId);
    menuOrder.setMenuQuantity( order.getMenuQuantity() );
    return menuOrder;
  }

	// Exception helpers -----------------------------------------------------

	/** Helper to throw a new BadInit exception. */
	private void throwBadInit(final String message) throws BadInitFault_Exception {
		BadInitFault faultInfo = new BadInitFault();
		faultInfo.message = message;
		throw new BadInitFault_Exception(message, faultInfo);
	}

  private void throwBadMenuIdFault(final String message) throws BadMenuIdFault_Exception {
    BadMenuIdFault faultInfo = new BadMenuIdFault();
    faultInfo.setMessage(message);
    throw new BadMenuIdFault_Exception(message, faultInfo);
  }

  private void throwBadTextFault(final String message) throws BadTextFault_Exception {
    BadTextFault faultInfo = new BadTextFault();
    faultInfo.setMessage(message);
    throw new BadTextFault_Exception(message, faultInfo);
  }

  private void throwBadQuantityFault(final String message) throws BadQuantityFault_Exception {
    BadQuantityFault faultInfo = new BadQuantityFault();
    faultInfo.setMessage(message);
    throw new BadQuantityFault_Exception(message, faultInfo);
  }

  private void throwInsufficientQuantityFault(final String message) throws InsufficientQuantityFault_Exception {
    InsufficientQuantityFault faultInfo = new InsufficientQuantityFault();
    faultInfo.setMessage(message);
    throw new InsufficientQuantityFault_Exception(message, faultInfo);
  }

  private void throwBadInitFault(final String message) throws BadInitFault_Exception {
    BadInitFault faultInfo = new BadInitFault();
    faultInfo.setMessage(message);
    throw new BadInitFault_Exception(message, faultInfo);
  }


}

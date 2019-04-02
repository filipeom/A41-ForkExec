package com.forkexec.hub.ws;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import java.lang.RuntimeException;

import javax.jws.WebService;

import com.forkexec.hub.domain.Hub;
import com.forkexec.hub.domain.CartItem;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.NotEnoughBalanceFault_Exception;

import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.MenuInit;
import com.forkexec.rst.ws.MenuOrder;
import com.forkexec.rst.ws.BadMenuIdFault_Exception;

import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.hub.ws.HubPortType",
wsdlLocation = "HubService.wsdl",
name ="HubWebService",
portName = "HubPort",
targetNamespace="http://ws.hub.forkexec.com/",
serviceName = "HubService"
)
public class HubPortImpl implements HubPortType {

  /**
   * The Endpoint manager controls the Web Service instance during its whole
   * lifecycle.
   */
  private HubEndpointManager endpointManager;

  /** Constructor receives a reference to the endpoint manager. */
  public HubPortImpl(HubEndpointManager endpointManager) {
    this.endpointManager = endpointManager;
  }

  // UDDIRecord getters ----------------------------------------------------

  public Collection<UDDIRecord> getRestaurants() throws UDDINamingException {
    return endpointManager.getUddiNaming().listRecords("A41_Restaurant%");
  } 

  public Collection<UDDIRecord> getPoints() throws UDDINamingException {
    return endpointManager.getUddiNaming().listRecords("A41_Points%");
  }

  public RestaurantClient getRestaurantClient(String rstName) 
      throws UDDINamingException, RestaurantClientException{
      return new RestaurantClient(endpointManager.getUddiNaming().lookup(rstName));
  }

  public PointsClient getPointsClient(String ptsName) 
      throws UDDINamingException, PointsClientException {
      return new PointsClient(endpointManager.getUddiNaming().lookup(ptsName));
  }

  // Main operations -------------------------------------------------------

  @Override
  public void activateAccount(String userId) throws InvalidUserIdFault_Exception {
    if (userId == null)
      throwInvalidUserId("User identifier cannot be null!");

    try {
      getPointsClient("A41_Points1").activateUser(userId);

    } catch(InvalidEmailFault_Exception | EmailAlreadyExistsFault_Exception e) {
      throwInvalidUserId("Invalid email address!");
    } catch(UDDINamingException | PointsClientException e) {
      throw new RuntimeException("Unable to get Points Client.");
    }
  }

  @Override
  public void loadAccount(String userId, int moneyToAdd, String creditCardNumber)
    throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
    if (userId == null)
      throwInvalidUserId("User identifier cannot be null!");
    if (moneyToAdd == 0)
      throwInvalidMoney("Money to add cannot be zero!");
    if (creditCardNumber == null)
      throwInvalidCreditCard("Credit card number cannot be null!");

    try {
      CreditCardClient ccClient = new CreditCardClient();

      if (!ccClient.validateNumber(creditCardNumber))
        throw new Exception();

    } catch(CreditCardClientException e) {
      throw new RuntimeException("Unable to get Credit Card Client.");
    } catch(Exception e) {
      throwInvalidCreditCard("Invalid credit card number!");
    }

    try {
      getPointsClient("A41_Points1").addPoints(userId, convertEURpoints(moneyToAdd));

    } catch(InvalidEmailFault_Exception e) {
      throwInvalidUserId("Email address is not valid!");
    } catch(InvalidPointsFault_Exception | InvalidMoneyFault_Exception e) {
      throwInvalidMoney("Invalid money number!");
    } catch(UDDINamingException | PointsClientException e) {
      throw new RuntimeException("Unable to get Points Client.");
    } 
    return;
  }

  @Override
  public List<Food> searchDeal(String description) throws InvalidTextFault_Exception {
    if (description == null)
      throwInvalidText("Description cannot be null!");

    List<Food> foodList = new ArrayList<Food>();
    try {
      for (UDDIRecord record : getRestaurants()) {
        RestaurantClient rstClient = new RestaurantClient(record.getUrl());
        for (Menu menu : rstClient.searchMenus(description))
          foodList.add(newFood(menu, record.getOrgName()));
      }
    } catch (UDDINamingException | RestaurantClientException e) {
      throw new RuntimeException("Unable to create Restaurant Client");
    } catch (Exception e) {
      throwInvalidText("Description is malformed!");
    }
    Collections.sort(foodList, Hub.PRICE_COMPARATOR);
    return foodList;
  }

  @Override
  public List<Food> searchHungry(String description) throws InvalidTextFault_Exception {
    if (description == null)
      throwInvalidText("Description cannot be null!");

    List<Food> foodList = new ArrayList<Food>();
    try {
      for (UDDIRecord record : getRestaurants()) {
        RestaurantClient rstClient = new RestaurantClient(record.getUrl());
        for (Menu menu : rstClient.searchMenus(description))
          foodList.add(newFood(menu, record.getOrgName()));
      }
    } catch (UDDINamingException | RestaurantClientException e) {
      throw new RuntimeException("Unable to create Restaurant Client");
    } catch (Exception e) {
      throwInvalidText("Description is malformed!");
    }
    Collections.sort(foodList, Hub.PREPARATION_TIME_COMPARATOR);
    return foodList;
  }

  @Override
  public void addFoodToCart(String userId, FoodId foodId, int foodQuantity)
    throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
    if (userId == null)
      throwInvalidUserId("User identifier cannot be null!");
    userId = userId.trim();
    if (userId.length() == 0)
      throwInvalidUserId("User identifier cannot be whitespace or empty!");
    if (foodId == null)
      throwInvalidFoodId("Food identifier cannot be null!");
    if (foodQuantity <= 0)
      throwInvalidFoodQuantity("Food quantity has to be bigger than zero!");

    if (!Hub.getInstance().existsUserCart(userId))
      throw new RuntimeException("User doesn't have any order");

    int price = 0;
    try {
      price = getFood(foodId).getPrice();
      price = price * foodQuantity;
    } catch (InvalidFoodIdFault_Exception e) {
      throwInvalidFoodId("Cannot get food object from with this food identifier");
    }

    Hub.getInstance().addFoodItemToCart(userId, foodId, foodQuantity, price);
    return;
  }

  @Override
  public void clearCart(String userId) throws InvalidUserIdFault_Exception {
    if (userId == null)
      throwInvalidUserId("User identifier cannot be null!");
    userId = userId.trim();
    if (userId.length() == 0)
      throwInvalidUserId("User identifier cannot be whitespace or empty!");

    if (!Hub.getInstance().existsUserCart(userId))
      throw new RuntimeException("User doesn't have any order");
    Hub.getInstance().clearCart(userId);
    return;
  }

  @Override
  public FoodOrder orderCart(String userId)
    throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {
    if (userId == null)
      throwInvalidUserId("User identifier cannot be null!");
    userId = userId.trim();
    if (userId.length() == 0)
      throwInvalidUserId("User identifier cannot be whitespace or empty!");

    List<CartItem> cart = Hub.getInstance().getUserCart(userId);
    if (cart.size() == 0)
      throwEmptyCart("Cannot order an empty cart!");

    // Sum Cart Price - CartItem price contains sub total of menu (menuPrice * quantity)
    int totalPrice = cart.stream().mapToInt(o -> o.getPrice()).sum();
    try {
      getPointsClient("A41_Points1").spendPoints(userId, totalPrice);
    } catch (InvalidEmailFault_Exception e) {
      throwInvalidUserId("User identifier failed in Points");
    } catch (InvalidPointsFault_Exception | NotEnoughBalanceFault_Exception e) {
      throwNotEnoguhPoints("Unable to make purchase with current Points.");
    } catch (Exception e) {
      throw new RuntimeException("Unable to get Points Client.");
    }

    FoodOrder order = new FoodOrder();
    try {
      for (CartItem item : cart) {
        getRestaurantClient(item.getFoodId().getRestaurantId())
          .orderMenu(newMenuId(item.getFoodId().getMenuId()), item.getFoodQuantity());
        order.getItems().add(newFoodOrderItem(item.getFoodId(), item.getFoodQuantity()));
      }
    } catch (Exception e) {
      throw new RuntimeException("Unchecked Exception.");
    }
    order.setFoodOrderId(newFoodOrderId(Hub.getInstance().getFoodOrderId()));
    return order;
  }

  @Override
  public int accountBalance(String userId) throws InvalidUserIdFault_Exception {
    if (userId == null)
      throwInvalidUserId("User identifier cannot be null!");

    try {
      return getPointsClient("A41_Points1").pointsBalance(userId);

    } catch(InvalidEmailFault_Exception e) {
      throwInvalidUserId("Cannot validade email address");
    } catch(UDDINamingException | PointsClientException e) {
      throw new RuntimeException("Unable to get Points Client.");
    }
    return 0;
  }

  @Override
  public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception {
    if (foodId == null)
      throwInvalidFoodId("Food Identifier cannot be null!");

    String rstId = foodId.getRestaurantId().trim();
    String menuId = foodId.getMenuId().trim();

    if (rstId.length() == 0 || menuId.length() == 0)
      throwInvalidFoodId("Identifiers cannot be empty or whitespace!");

    try {
      MenuId mid = new MenuId();
      mid.setId(menuId);

      return newFood(getRestaurantClient(rstId).getMenu(mid), rstId);
    } catch (BadMenuIdFault_Exception e) {
      throwInvalidFoodId("Bad menu identfier.");
    } catch (UDDINamingException | RestaurantClientException e) {
      throw new RuntimeException("Unable to get Restaurant Client.");
    }
    return null;
  }

  @Override
  public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception {
    if (userId == null)
      throwInvalidUserId("User identifier cannot be null!");
    userId = userId.trim();
    if (userId.length() == 0)
      throwInvalidUserId("User identifier cannot be whitespace or empty");

    if (!Hub.getInstance().existsUserCart(userId))
      throw new RuntimeException("User doesn't have any order");

    return Hub.getInstance().getUserCart(userId).stream().map(item -> {
      FoodOrderItem foodItem = new FoodOrderItem();
      foodItem.setFoodId(item.getFoodId());
      foodItem.setFoodQuantity(item.getFoodQuantity());
      return foodItem;
    }).collect(Collectors.toList());
  }

  // Control operations ----------------------------------------------------

  /** Diagnostic operation to check if service is running. */
  @Override
  public String ctrlPing(String inputMessage) {
    StringBuilder builder = new StringBuilder();

    // If no input is received, return a default name.
    if (inputMessage == null || inputMessage.trim().length() == 0)
      inputMessage = "friend";

    // If the service does not have a name, return a default.
    String wsName = endpointManager.getWsName();
    if (wsName == null || wsName.trim().length() == 0)
      wsName = "Hub";

    // Build a string with a message to return.
    builder.append("Hello ").append(inputMessage);
    builder.append(" from ").append(wsName);

    // Ping all available restaurants and points servers
    try {
      for (UDDIRecord record : getRestaurants()) {
        RestaurantClient rstClient = new RestaurantClient(record.getUrl());
        builder.append("\n").append(rstClient.ctrlPing(inputMessage));
      }

      for (UDDIRecord record : getPoints()) {
        PointsClient ptsClient = new PointsClient(record.getUrl());
        builder.append("\n").append(ptsClient.ctrlPing(inputMessage));
      }
    } catch (UDDINamingException e) {
      throw new RuntimeException("Unable to find Restaurants or Points servers");
    } catch (RestaurantClientException | PointsClientException e) {
      throw new RuntimeException("Unable to construct Restaurant or Points client.");
    }
    return builder.toString();
  }

  /** Return all variables to default values. */
  @Override
  public void ctrlClear() {
    Hub.getInstance().reset();
    try {
      for (UDDIRecord record : getRestaurants()) {
        RestaurantClient rstClient = new RestaurantClient(record.getUrl());
        rstClient.ctrlClear();
      }

      for (UDDIRecord record : getPoints()) {
        PointsClient ptsClient = new PointsClient(record.getUrl());
        ptsClient.ctrlClear();
      }
    } catch (UDDINamingException e) {
      throw new RuntimeException("Unable to find Restaurants or Points servers");
    } catch (RestaurantClientException | PointsClientException e) {
      throw new RuntimeException("Unable to construct Restaurant or Points client.");
    }
    return;
  } 

  /** Set variables with specific values. */
  @Override
  public void ctrlInitFood(List<FoodInit> initialFoods) throws InvalidInitFault_Exception {
    if (initialFoods == null)
      throwInvalidInit("Initial foods list cannot be null!");

    try {
      for (UDDIRecord record : getRestaurants()) {
        RestaurantClient rstClient = new RestaurantClient(record.getUrl());
        rstClient.ctrlInit(newInitialMenus(initialFoods, record.getOrgName()));
      }
    } catch (UDDINamingException | RestaurantClientException e){
      throw new RuntimeException("Unable to get Restaurant Client.");
    } catch (Exception e) {
      throwInvalidInit("Could not init the restaurants");
    }
    return;
  }

  @Override
  public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception {
    if (startPoints == 0)
      throwInvalidInit("Start points cannot be zero!");

    try {
      for (UDDIRecord record : getPoints()) {
        PointsClient ptsClient = new PointsClient(record.getUrl());
        ptsClient.ctrlInit(startPoints);
      }
    } catch (UDDINamingException | PointsClientException e) {
      throw new RuntimeException("Unable to get Points Client.");
    } catch (Exception e) {
      throwInvalidInit("Could not init the points");
    }
    return;
  }

  //General helpers --------------------------------------------------------
  
  public int convertEURpoints(int money) throws InvalidMoneyFault_Exception {
    int points = 0;
    switch(money) {
      case 10:
        points = 1000; break;
      case 20:
        points = 2100; break;
      case 30:
        points = 3300; break;
      case 50:
        points = 5500; break;
      default:
        throwInvalidMoney("Unable to convert that quantity(EUR).");

    }
    return points;
  }

  // View helpers ----------------------------------------------------------

  private Food newFood(Menu menu, String rid) {
    FoodId foodId = new FoodId();
    foodId.setRestaurantId(rid);
    foodId.setMenuId(menu.getId().getId());

    Food food = new Food();
    food.setId(foodId);
    food.setEntree(menu.getEntree());
    food.setPlate(menu.getPlate());
    food.setDessert(menu.getDessert());
    food.setPrice(menu.getPrice());
    food.setPreparationTime(menu.getPreparationTime());
    return food;
  }

  public List<MenuInit> newInitialMenus(List<FoodInit> initialFoods, String rstName) {
    List<MenuInit> initialMenus = new ArrayList<MenuInit>();

    for (FoodInit init : initialFoods) {
      if (!init.getFood().getId().getRestaurantId().equals(rstName))
        continue;

      Food food = init.getFood();

      MenuId mid = new MenuId();
      mid.setId(food.getId().getMenuId());

      Menu menu = new Menu();
      menu.setId(mid);
      menu.setEntree(food.getEntree());
      menu.setPlate(food.getPlate());
      menu.setDessert(food.getDessert());
      menu.setPrice(food.getPrice());
      menu.setPreparationTime(food.getPreparationTime());

      MenuInit menuInit = new MenuInit(); 
      menuInit.setMenu(menu);
      menuInit.setQuantity(init.getQuantity());

      initialMenus.add(menuInit);
    }
    return initialMenus;
  }

  public MenuId newMenuId(String id) {
    MenuId menuId = new MenuId();
    menuId.setId(id);
    return menuId;
  }

  public FoodOrderId newFoodOrderId(String id) {
    FoodOrderId foid = new FoodOrderId();
    foid.setId(id);
    return foid;
  }

  public FoodOrderItem newFoodOrderItem(FoodId foodId, int quantity) {
    FoodOrderItem item = new FoodOrderItem();
    item.setFoodId(foodId);
    item.setFoodQuantity(quantity);
    return item;
  }

  // Exception helpers -----------------------------------------------------

  /** Helper to throw a new Invalid Credit Card exception. */
  private void throwInvalidCreditCard(final String message) throws InvalidCreditCardFault_Exception {
    InvalidCreditCardFault faultInfo = new InvalidCreditCardFault();
    faultInfo.message = message;
    throw new InvalidCreditCardFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new Invalid FoodId exception. */
  private void throwInvalidFoodId(final String message) throws InvalidFoodIdFault_Exception {
    InvalidFoodIdFault faultInfo = new InvalidFoodIdFault();
    faultInfo.message = message;
    throw new InvalidFoodIdFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new Invalid Food Quantity exception. */
  private void throwInvalidFoodQuantity(final String message) throws InvalidFoodQuantityFault_Exception {
    InvalidFoodQuantityFault faultInfo = new InvalidFoodQuantityFault();
    faultInfo.message = message;
    throw new InvalidFoodQuantityFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new Invalid Init exception. */
  private void throwInvalidInit(final String message) throws InvalidInitFault_Exception {
    InvalidInitFault faultInfo = new InvalidInitFault();
    faultInfo.message = message;
    throw new InvalidInitFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new Invalid Money exception. */
  private void throwInvalidMoney(final String message) throws InvalidMoneyFault_Exception {
    InvalidMoneyFault faultInfo = new InvalidMoneyFault();
    faultInfo.message = message;
    throw new InvalidMoneyFault_Exception(message, faultInfo);
  } 

  /** Helper to throw a new Invalid Text exception. */
  private void throwInvalidText(final String message) throws InvalidTextFault_Exception {
    InvalidTextFault faultInfo = new InvalidTextFault();
    faultInfo.message = message;
    throw new InvalidTextFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new Invalid UserId exception. */
  private void throwInvalidUserId(final String message) throws InvalidUserIdFault_Exception {
    InvalidUserIdFault faultInfo = new InvalidUserIdFault();
    faultInfo.message = message;
    throw new InvalidUserIdFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new Empty Cart exception. */
  private void throwEmptyCart(final String message) throws EmptyCartFault_Exception {
    EmptyCartFault faultInfo = new EmptyCartFault();
    faultInfo.message = message;
    throw new EmptyCartFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new Not Enought Points exception. */
  private void throwNotEnoguhPoints(final String message) throws NotEnoughPointsFault_Exception {
    NotEnoughPointsFault faultInfo = new NotEnoughPointsFault();
    faultInfo.message = message;
    throw new NotEnoughPointsFault_Exception(message, faultInfo);
  }
}

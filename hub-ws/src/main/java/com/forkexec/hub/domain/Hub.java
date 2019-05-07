package com.forkexec.hub.domain;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forkexec.hub.ws.Food;

/**
 * Hub
 *
 * A restaurants hub server.
 *
 */
public class Hub {

  /** 
   * Map of user carts. Uses concurrent hash table implementation
   * supporting full concurrency of retrievals and high expected concurrency
   * for updates.
   */  
  private Map<String, List<CartItem>> cart = new ConcurrentHashMap<>();

  /**
   * Global food order identifier. Uses lock-free thread-safe single variable
   */
  private AtomicInteger foodOrderId = new AtomicInteger(0);

  /**
   * Pattern for identifiers.
   */
  private static Pattern validString = Pattern.compile("^[\\p{Alnum}\\-'._]+$", Pattern.UNICODE_CHARACTER_CLASS);

  /**
   * Pattern for emails.
   */
  private static Pattern validEmail = Pattern.compile("(\\w\\.?)*\\w+@\\w+(\\.?\\w)*", Pattern.CASE_INSENSITIVE);

  /**
   * A comparator for food based on price.
   *
   * Use as: Hub.PRICE_COMPARATOR
   */
  public final static Comparator<Food> PRICE_COMPARATOR = new PriceComparator();

  /**
   * A comparator for food based on preparation time.
   *
   * Use as: Hub.PREPARATION_TIME_COMPARATOR
   */
  public final static Comparator<Food> PREPARATION_TIME_COMPARATOR = new PreparationTimeComparator();

  /**
   * This is a private class implementing the comparator.
   */
  private static class PriceComparator implements Comparator<Food> {
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Food food1, Food food2) {
      return food1.getPrice() - food2.getPrice();
    }
  }

  /**
   * This is a private class implementing the comparator.
   */
  private static class PreparationTimeComparator implements Comparator<Food> {
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Food food1, Food food2) {
      return food1.getPreparationTime() - food2.getPreparationTime();
    }
  }

  // Singleton -------------------------------------------------------------

  /** Private constructor prevents instantiation from other classes. */
  private Hub() {
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    private static final Hub INSTANCE = new Hub();
  }

  public static synchronized Hub getInstance() {
    return SingletonHolder.INSTANCE;
  }

  // orders ----------------------------------------------------------------

  public void reset() {
    cart.clear();
  }

  public boolean validateId(String id) {
    if (id != null)
      return validString.matcher(id).matches() ? true : false;
    return false;
  }

  public boolean validateEmail(String email) {
    if (email != null)
      return validEmail.matcher(email).matches() ? true : false;
    return false;
  }

  public synchronized String getFoodOrderId() {
    return Integer.toString(foodOrderId.incrementAndGet());
  }

  public synchronized boolean existsUserCart(String uid) {
    return this.cart.containsKey(uid);
  }

  public synchronized List<CartItem> getUserCart(String uid) {
    return this.cart.get(uid);
  }

  public synchronized void addFoodItemToCart(String uid, String rid, 
      String mid, int quantity, int price) {
    if (!validateEmail(uid))
      return;

    if (!existsUserCart(uid)) 
      this.cart.put(uid, new ArrayList<CartItem>());

    getUserCart(uid).add(new CartItem(rid, mid, quantity, price));
  }

  public synchronized void clearCart(String uid) {
    if (!this.existsUserCart(uid))
      return;
    getUserCart(uid).clear();
  }
}

package com.forkexec.hub.domain;

import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.forkexec.hub.ws.FoodOrder;

/**
 * Hub
 *
 * A restaurants hub server.
 *
 */
public class Hub {

  /**
   * Map of orders. Also uses concurrent hash table implementation.
   */
  private Map<String, FoodOrder> orders = new ConcurrentHashMap<>();

  /**
   * Map of user orders. Also uses concurrent hash table implementation.
   */
  private Map<String, String> userOrders = new ConcurrentHashMap<>();

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

  public void reset() {
    orders.clear();
  }

  public Boolean checkString(String arg) {
    return arg != null && !"".equals(arg);
  }

  // users -----------------------------------------------------------------
  
  public Boolean userHasOrder(String uid) {
    return userOrders.containsKey(uid);
  }

  public String getUserOrder(String uid) {
    return userOrders.get(uid);
  }

  public void addUserOrder(String uid, String oid) {
    if (checkString(uid) && checkString(oid))
      userOrders.put(uid, oid);
  }
}

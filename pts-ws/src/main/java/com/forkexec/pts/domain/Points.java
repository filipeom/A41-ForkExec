package com.forkexec.pts.domain;

import com.forkexec.pts.domain.QuantityException;
import com.forkexec.pts.domain.User;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
 

/**
 * Points
 * <p>
 * A points server.
 */
public class Points {

    /**
     * Constant representing the default initial balance for every new client
     */
    private static final int DEFAULT_INITIAL_BALANCE = 100;
    /**
     * Global with the current value for the initial balance of every new client
     */
    private final AtomicInteger initialBalance = new AtomicInteger(DEFAULT_INITIAL_BALANCE);
    /**
     *  Map structure to keep state of users
     */
    private final Map<String, User> users = new ConcurrentHashMap<>();


    // Singleton -------------------------------------------------------------

    /**
     * Private constructor prevents instantiation from other classes.
     */
    private Points() { }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final Points INSTANCE = new Points();
    }

    public static synchronized Points getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void reset() {
        users.clear();
        initialBalance.set(DEFAULT_INITIAL_BALANCE);
    }

    public void setInitialBalance(int balance) {
        initialBalance.set(balance);
    }

    public void addUser(String userEmail) {
        users.put(userEmail, new User(userEmail, initialBalance.get()));
    }

    public boolean userExists(String userEmail) {
        return users.get(userEmail) != null ? true : false;
    }

    public int getUserPoints(String userEmail) {
        User user = users.get(userEmail);

        return user.getPoints();
    }

    public int addUserPoints(String userEmail, int pointsToAdd) {
        User user = users.get(userEmail);

        synchronized(user) {
            int balance = user.getPoints() + pointsToAdd;
            
            user.setBalance(balance);
            return balance;
        }
    }

    public int spendUserPoints(String userEmail, int pointsToSpend) throws QuantityException {

        User user = users.get(userEmail);
        
        synchronized(user) {
            int balance = user.getPoints() - pointsToSpend;
            
            if (balance < 0) {
                String message = String.format("Cost is %d, yet user only has %d points", pointsToSpend, user.getPoints());
                throw new QuantityException(message);
            }
            
            user.setBalance(balance);
            return balance;
        }
    }
}

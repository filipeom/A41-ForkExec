package com.forkexec.pts.domain;


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

    /**
     * Checks if userEmail already exists if not add it
     * @param userEmail
     * @return
     */
    public void addUser(String userEmail) {
        users.put(userEmail, new User(userEmail, initialBalance.get()));
    }
    /**
     * 
     * @param userEmail
     * @return
     */
    public boolean userExists(String userEmail) {
        return users.get(userEmail) != null ? true : false;
    }
    /**
     * TODO - what to return if userEmail not in the database
     * @param userEmail
     * @return
     */
    public int getUserPoints(String userEmail) {
        User user = users.get(userEmail);

        if (user != null) {
            return user.getPoints();
        }
        return 0; //FIX
    }

}

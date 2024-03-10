package it.mikeslab.widencoins.database;

import java.util.Map;
import java.util.Optional;

/**
 * Primary based on Coin System.
 * Only for the purpose of the plugin.
 * Manages CRUD operations for the player's coins.
 */
public interface Repository {

    /**
     * Updates, if exists, or inserts a new entry for the player.
     * @param key
     * @param values
     */
    void upsert(String key, Map<String, String> values);

    /**
     * Removes the entry for the player.
     * @param key
     */
    void remove(String key);

    /**
     * Returns the amount of coins the player has.
     * @param key
     * @return
     */
    Optional<Map<String, String>> get(String key);




}

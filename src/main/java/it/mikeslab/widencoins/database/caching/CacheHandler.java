package it.mikeslab.widencoins.database.caching;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Stopwatch;
import it.mikeslab.widencoins.WidenCoins;
import it.mikeslab.widencoins.database.DBConfigHandler;
import it.mikeslab.widencoins.database.DatabaseImpl;
import it.mikeslab.widencoins.util.LoggerUtil;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class CacheHandler {

    private final LoadingCache<UUID, Map.Entry<Double, Boolean>> playerCoinsCache;
    private final double DEFAULT_COINS = 0.0;
    private final DatabaseImpl databaseImpl;
    private final String collection;

    /**
     * Constructor
     * @param dbConfigHandler database configuration handler
     */
    public CacheHandler(DBConfigHandler dbConfigHandler) {

        // Get the database implementation
        this.databaseImpl = dbConfigHandler.getDatabase();

        // Get the collection
        this.collection = dbConfigHandler.getUriBuilder().getCollection();

        playerCoinsCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .build(k ->
                        // Define default value
                        new AbstractMap.SimpleEntry<>(Double.parseDouble(
                                databaseImpl.select(
                                        collection,
                                        k.toString()
                                ).getOrDefault(
                                        WidenCoins.COINS_KEY, // If user is not in the database, return default value
                                        String.valueOf(DEFAULT_COINS)
                                )
                        ), false) // This boolean is used to check if value got edited during the cache time

                );
    }


    /**
     * Get the coins of a player
     * @param playerUUID player UUID
     * @return amount of coins
     */
    public double getCoins(UUID playerUUID) {
        return playerCoinsCache.get(playerUUID).getKey();
    }


    /**
     * Set the coins of a player
     * @param playerUUID player UUID
     * @param amount amount of coins
     */
    public void setCoins(UUID playerUUID, double amount) {
        playerCoinsCache.put(playerUUID, new AbstractMap.SimpleEntry<>(amount, true));
    }

    /**
     * Add coins to a player
     * @param playerUUID player UUID
     * @param amount amount of coins
     */
    public void addCoins(UUID playerUUID, double amount) {
        double currentAmount = getCoins(playerUUID);
        setCoins(playerUUID, currentAmount + amount);
    }

    /**
     * Takes coins from a player
     * @param playerUUID player UUID
     * @param amount amount of coins
     * @return true if the player has enough coins, false otherwise
     */
    public boolean takeCoins(UUID playerUUID, double amount) {
        double currentAmount = getCoins(playerUUID);

        if(currentAmount - amount < 0) {
            return false;
        }

        setCoins(playerUUID, currentAmount - amount);
        return true;
    }


    /**
     * Save all the cache to the database
     * Run this method when the plugin is being disabled
     */
    public void saveAll() {

        Stopwatch stopwatch = Stopwatch.createStarted();

        playerCoinsCache.asMap().forEach((k, v) -> {

            // If the value got edited during the cache time, update the database
            // This is useful to avoid unnecessary database calls

            if (v.getValue()) {

                // Upsert the value
                databaseImpl.upsert(
                        collection,
                        k.toString(),
                        Map.of(WidenCoins.COINS_KEY, String.valueOf(v.getKey()))
                );
            }

        });

        stopwatch.stop();
        LoggerUtil.log(
                Level.INFO,
                LoggerUtil.LogSource.DATABASE,
                "Cache saved to database in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms"
        );
    }
}

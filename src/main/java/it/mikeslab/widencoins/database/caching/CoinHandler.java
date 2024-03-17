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

public class CoinHandler {

    private final double DEFAULT_COINS = 0.0;
    private final DatabaseImpl databaseImpl;
    private final String collection;
    private final String fieldKey = WidenCoins.COINS_KEY;

    /**
     * Constructor
     * @param dbConfigHandler database configuration handler
     */
    public CoinHandler(DBConfigHandler dbConfigHandler) {

        // Get the database implementation
        this.databaseImpl = dbConfigHandler.getDatabase();

        // Get the collection
        this.collection = dbConfigHandler.getUriBuilder().getCollection();

    }


    /**
     * Get the coins of a player
     * @param playerUUID player UUID
     * @return amount of coins
     */
    public double getCoins(UUID playerUUID) {
        String playerUUIDAsString = playerUUID.toString();
        Map<String, String> queryResult = databaseImpl.select(collection, playerUUIDAsString);

        if(queryResult == null || queryResult.isEmpty()) {
            return DEFAULT_COINS;
        }

        String coinsAsString = queryResult.get(fieldKey);
        return Double.parseDouble(coinsAsString);
    }


    /**
     * Set the coins of a player
     * @param playerUUID player UUID
     * @param amount amount of coins
     */
    public void setCoins(UUID playerUUID, double amount) {
        String playerUUIDAsString = playerUUID.toString();

        databaseImpl.upsert(
                collection,
                playerUUIDAsString,
                Map.of(WidenCoins.COINS_KEY, String.valueOf(amount))
        );
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

}

package it.mikeslab.widencoins.database.utility;

import it.mikeslab.widencoins.WidenCoins;
import it.mikeslab.widencoins.database.DBConfigHandler;
import it.mikeslab.widencoins.database.DatabaseImpl;

import java.util.Map;
import java.util.UUID;

public class CoinUtil {

    private final double DEFAULT_COINS = 0.0;
    private final DatabaseImpl databaseImpl;
    private final String collection;
    private final String fieldKey = WidenCoins.COINS_KEY;

    /**
     * Constructor
     * @param dbConfigHandler database configuration handler
     */
    public CoinUtil(DBConfigHandler dbConfigHandler) {

        // Get the database implementation
        this.databaseImpl = dbConfigHandler.getDatabase();

        // Get the collection
        this.collection = dbConfigHandler.getUriBuilder().getCollection();

    }


    /**
     * Get the coins of a player
     * @param playerUsername player username
     * @return amount of coins
     */
    public double getCoins(String playerUsername) {

        Map<String, String> queryResult = databaseImpl.select(collection, playerUsername);

        if(queryResult == null || queryResult.isEmpty()) {
            return DEFAULT_COINS;
        }

        String coinsAsString = queryResult.get(fieldKey);
        return Double.parseDouble(coinsAsString);
    }


    /**
     * Set the coins of a player
     * @param playerUsername player username
     * @param amount amount of coins
     */
    public void setCoins(String playerUsername, double amount) {

        databaseImpl.upsert(
                collection,
                playerUsername,
                Map.of(WidenCoins.COINS_KEY, String.valueOf(amount))
        );

    }

    /**
     * Add coins to a player
     * @param playerUsername player username
     * @param amount amount of coins
     */
    public void addCoins(String playerUsername, double amount) {
        double currentAmount = getCoins(playerUsername);
        setCoins(playerUsername, currentAmount + amount);
    }

    /**
     * Takes coins from a player
     * @param playerUsername player username
     * @param amount amount of coins
     * @return true if the player has enough coins, false otherwise
     */
    public boolean takeCoins(String playerUsername, double amount) {
        double currentAmount = getCoins(playerUsername);

        if(currentAmount - amount < 0) {
            return false;
        }

        setCoins(playerUsername, currentAmount - amount);
        return true;
    }

}

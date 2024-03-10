package it.mikeslab.widencoins.command.helper;

import it.mikeslab.widencoins.WidenCoins;
import it.mikeslab.widencoins.database.Repository;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class CoinDBHandler {

    private final Repository repository;

    public void upsertCoins(UUID uuid, int amount) {
        repository.upsert(
                    uuid.toString(),
                    Map.of(WidenCoins.COINS_KEY, String.valueOf(amount))
        );
    }


    public int getCurrentAmount(UUID uuid) {
        return repository.get(uuid.toString())
                .map(map -> map.get(WidenCoins.COINS_KEY))
                .map(Integer::parseInt)
                .orElse(0);
    }


    public void resetCoins(UUID uuid) {
        repository.remove(uuid.toString());
    }

    /**
     * Takes coins from the player.
     * WARNING: This method does not check if the player has enough coins.
     * @param uuid
     * @param amount
     */
    public void takeCoins(UUID uuid, int amount) {
        int currentCoins = this.getCurrentAmount(uuid);

        currentCoins -= amount;

        this.upsertCoins(uuid, currentCoins);
    }


    public void giveCoins(UUID uuid, int amount) {
        int currentCoins = this.getCurrentAmount(uuid);

        currentCoins += amount;

        this.upsertCoins(uuid, currentCoins);
    }


    public void setCoins(UUID uuid, int amount) {
        this.upsertCoins(uuid, amount);
    }








}

package it.mikeslab.widencoins.economy;

import it.mikeslab.widencoins.database.utility.CoinUtil;
import it.mikeslab.widencoins.lang.LangHandler;
import it.mikeslab.widencoins.lang.LangKey;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

@RequiredArgsConstructor
public class EconomyImplementer implements Economy {

    private final LangHandler langHandler;
    private final CoinUtil coinUtil;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "CoinEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return String.format("%,.2f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return langHandler.get(LangKey.CURRENCY_NAME_PLURAL);
    }

    @Override
    public String currencyNameSingular() {
        return langHandler.get(LangKey.CURRENCY_NAME_SINGULAR);
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {

        if(player.isOnline()) {

            // Since we're using a cache with a default value, we can assume that the player has an account
            // even if it's not in the database

            return true;
        }

        return false;
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    @Deprecated
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return false;
    }

    @Override
    @Deprecated
    public double getBalance(String playerName) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return coinUtil.getCoins(player.getName());
    }

    @Override
    @Deprecated
    public double getBalance(String playerName, String world) {
        return 0;
    }

    @Override
    @Deprecated
    public double getBalance(OfflinePlayer player, String world) {
        return 0;
    }

    @Override
    @Deprecated
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return coinUtil.getCoins(player.getName()) >= amount;
    }

    @Override
    @Deprecated
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    @Deprecated
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return false;
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {

        if(player.isOnline()) {

            coinUtil.takeCoins(player.getName(), amount);

            return new EconomyResponse(
                    amount,
                    coinUtil.getCoins(player.getName()),
                    EconomyResponse.ResponseType.SUCCESS,
                    ""
            );
        }

        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Player is not online"
        );



    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {

        if(player.isOnline()) {
            coinUtil.addCoins(player.getName(), amount);

            return new EconomyResponse(
                    amount,
                    coinUtil.getCoins(player.getName()),
                    EconomyResponse.ResponseType.SUCCESS,
                    ""
            );
        }

        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Player is not online"
        );
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        coinUtil.addCoins(player.getName(), 0);
        return true;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return false;
    }


}

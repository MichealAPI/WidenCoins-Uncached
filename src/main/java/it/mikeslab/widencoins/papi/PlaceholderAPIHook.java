package it.mikeslab.widencoins.papi;

import it.mikeslab.widencoins.WidenCoins;
import it.mikeslab.widencoins.database.caching.CacheHandler;
import it.mikeslab.widencoins.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@RequiredArgsConstructor
public class PlaceholderAPIHook {

    private final WidenCoins instance;
    private final CacheHandler cacheHandler;

    public void hook() {

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CoinExpansion(instance, cacheHandler).register();

            LoggerUtil.log(
                    Level.INFO,
                    LoggerUtil.LogSource.API,
                    "PlaceholderAPI hooked to " + instance.getName()
            );

        } else {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.API,
                    "PlaceholderAPI not found, disabling PlaceholderAPI expansion"
            );
        }
    }


    public void unhook() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            CoinExpansion coinExpansion = new CoinExpansion(instance, cacheHandler);
            coinExpansion.unregister();

            LoggerUtil.log(
                    Level.INFO,
                    LoggerUtil.LogSource.API,
                    "PlaceholderAPI unhooked from " + instance.getName()
            );
        }
    }



}

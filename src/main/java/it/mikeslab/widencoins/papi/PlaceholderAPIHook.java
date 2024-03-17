package it.mikeslab.widencoins.papi;

import it.mikeslab.widencoins.WidenCoins;
import it.mikeslab.widencoins.database.utility.CoinUtil;
import it.mikeslab.widencoins.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@RequiredArgsConstructor
public class PlaceholderAPIHook {

    private final WidenCoins instance;
    private final CoinUtil coinUtil;

    /**
     * Hooks the PlaceholderAPI expansion
     */
    public void hook() {

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CoinExpansion(instance, coinUtil).register();

            LoggerUtil.log(
                    Level.INFO,
                    LoggerUtil.LogSource.API,
                    "PlaceholderAPI hooked into " + instance.getName()
            );

        } else {
            LoggerUtil.log(
                    Level.WARNING,
                    LoggerUtil.LogSource.API,
                    "PlaceholderAPI not found, disabling PlaceholderAPI expansion"
            );
        }
    }


    /**
     * Unhooks the PlaceholderAPI expansion
     * Note: this PlaceholderAPI expansion is supposed to be persistent across reloads
     *       so no need to unhook it on plugin disable
     */
    public void unhook() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            CoinExpansion coinExpansion = new CoinExpansion(instance, coinUtil);
            coinExpansion.unregister();

            LoggerUtil.log(
                    Level.INFO,
                    LoggerUtil.LogSource.API,
                    "PlaceholderAPI unhooked from " + instance.getName()
            );
        }
    }



}

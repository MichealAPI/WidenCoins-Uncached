package it.mikeslab.widencoins.economy;

import it.mikeslab.widencoins.WidenCoins;
import it.mikeslab.widencoins.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.util.logging.Level;

@RequiredArgsConstructor
public class VaultHook {

    private final WidenCoins plugin;

    private Economy provider;

    public void hook() {
        provider = plugin.economyImplementer;
        Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.Normal);
        LoggerUtil.log(Level.INFO, LoggerUtil.LogSource.API, "VaultAPI hooked to " + plugin.getName());
    }

    public void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, this.provider);
        LoggerUtil.log(Level.INFO, LoggerUtil.LogSource.API, "VaultAPI unhooked from " + plugin.getName());
    }

}

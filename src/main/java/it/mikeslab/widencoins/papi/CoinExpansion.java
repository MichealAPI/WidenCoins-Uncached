package it.mikeslab.widencoins.papi;

import it.mikeslab.widencoins.WidenCoins;
import it.mikeslab.widencoins.database.caching.CacheHandler;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class CoinExpansion extends PlaceholderExpansion {

    private final WidenCoins instance;
    private final CacheHandler cacheHandler;

    @Override
    public @NotNull String getIdentifier() {
        return "widencoins";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MikesLab";
    }

    @Override
    public @NotNull String getVersion() {
        return instance.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {

        if(params.equalsIgnoreCase("balance")) {
            UUID playerUUID = player.getUniqueId();

            return String.valueOf(cacheHandler.getCoins(playerUUID));
        }

        if(params.equalsIgnoreCase("balance_formatted")) {
            UUID playerUUID = player.getUniqueId();

            return String.format("%,.2f", cacheHandler.getCoins(playerUUID));
        }

        return null; // Placeholder is unknown by the expansion
    }
}

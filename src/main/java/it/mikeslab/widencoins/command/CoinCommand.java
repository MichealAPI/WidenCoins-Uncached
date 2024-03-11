package it.mikeslab.widencoins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.mikeslab.widencoins.Permission;
import it.mikeslab.widencoins.database.caching.CacheHandler;
import it.mikeslab.widencoins.lang.LangHandler;
import it.mikeslab.widencoins.lang.LangKey;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("coin")
@RequiredArgsConstructor
public class CoinCommand extends BaseCommand {

    private final CacheHandler cacheHandler;
    private final LangHandler langHandler;

    @Default
    @HelpCommand
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("give")
    @CommandPermission(Permission.GIVE_COINS)
    @Description("Give coins to a player")
    @Syntax("<player> <amount>")
    @CommandCompletion("@players")
    public void onGive(CommandSender sender, OnlinePlayer target, double amount) {

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        cacheHandler.addCoins(playerUUID, amount);

        sender.sendMessage(langHandler.get(LangKey.COIN_GIVEN)
                .replace("%player%", player.getName())
                .replace("%amount%", String.valueOf(amount))
        );

        if(player != sender) {
            player.sendMessage(
                    langHandler.get(LangKey.COIN_RECEIVED)
                            .replace("%amount%", String.valueOf(amount))
            );
        }
    }


    @Subcommand("take")
    @CommandPermission(Permission.TAKE_COINS)
    @Description("Take coins from a player")
    @Syntax("<player> <amount>")
    @CommandCompletion("@players")
    public void onTake(CommandSender sender, OnlinePlayer target, double amount) {

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // returns false if the player doesn't have enough coins
        boolean result = cacheHandler.takeCoins(playerUUID, amount);

        if(!result) {
            sender.sendMessage(langHandler.get(LangKey.COIN_NOT_ENOUGH));
            return;
        }

        // get current target's coins
        double currentCoins = cacheHandler.getCoins(playerUUID);

        sender.sendMessage(langHandler.get(LangKey.COIN_TAKEN)
                .replace("%player%", player.getName())
                .replace("%amount%", String.valueOf(amount)
                .replace("%current%", String.valueOf(currentCoins)))
        );
    }

    @Subcommand("set")
    @CommandPermission(Permission.SET_COINS)
    @Description("Set a player's coins")
    @Syntax("<player> <amount>")
    @CommandCompletion("@players")
    public void onSet(CommandSender sender, OnlinePlayer target, double amount) {

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        cacheHandler.setCoins(playerUUID, amount);

        sender.sendMessage(langHandler.get(LangKey.COIN_SET)
                .replace("%player%", player.getName())
                .replace("%amount%", String.valueOf(amount))
        );
    }

    @Subcommand("reset")
    @CommandPermission(Permission.RESET_COINS)
    @Description("Reset a player's coins")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onReset(CommandSender sender, OnlinePlayer target) {

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        cacheHandler.setCoins(playerUUID, 0);

        sender.sendMessage(langHandler.get(LangKey.COIN_RESET)
                .replace("%player%", player.getName())
        );
    }



    @Subcommand("view")
    @Description("View a player's coins")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onView(CommandSender sender, OnlinePlayer target) {

        boolean senderIsPlayer = sender instanceof Player;
        boolean self = sender == target.getPlayer();

        // if the sender is a player and is trying to see his own coins but doesn't have permission
        if(self && senderIsPlayer && !sender.hasPermission(Permission.VIEW_COINS_SELF)) return;

        // if the sender is trying to see someone else's coins but doesn't have permission
        if(!self && !sender.hasPermission(Permission.VIEW_COINS_OTHERS)) return;

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // if the sender is a player and is trying to see his own coins
        if(self && senderIsPlayer) {

            Player commandSenderAsPlayer = (Player) sender;

            commandSenderAsPlayer.sendMessage(langHandler.get(LangKey.COIN_VIEW_SELF)
                    .replace("%amount%", String.valueOf(cacheHandler.getCoins(playerUUID)))
            );

        } else {
            sender.sendMessage(langHandler.get(LangKey.COIN_VIEW_OTHER)
                    .replace("%player%", player.getName())
                    .replace("%amount%", String.valueOf(cacheHandler.getCoins(playerUUID)))
            );
        }
    }




}

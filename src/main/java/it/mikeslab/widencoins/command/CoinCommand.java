package it.mikeslab.widencoins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.mikeslab.widencoins.Permission;
import it.mikeslab.widencoins.WidenCoins;
import it.mikeslab.widencoins.database.utility.CoinUtil;
import it.mikeslab.widencoins.lang.ChatColor;
import it.mikeslab.widencoins.lang.LangHandler;
import it.mikeslab.widencoins.lang.LangKey;
import it.mikeslab.widencoins.util.LoggerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Level;

@CommandAlias("%command-aliases")
public class CoinCommand extends BaseCommand {

    private final WidenCoins instance;
    private final CoinUtil coinUtil;
    private final LangHandler langHandler;

    public CoinCommand(WidenCoins instance) {
        this.instance = instance;

        this.coinUtil = instance.coinUtil;
        this.langHandler = instance.langHandler;
    }

    @Default
    @HelpCommand
    @Syntax("(command)")
    @Description("Show help")
    public void onHelp(CommandHelp help, Player sender) {

        if(!sender.hasPermission(Permission.COMMAND_HELP)) {

            sender.sendMessage(ChatColor.color(
                    "&8» &#FFB400This Server is running &#FF7800WidenCoins &#FFB400v%version% by &#FF7800MikesLab"
                            .replace("%version%", instance.getPluginMeta().getVersion())
            ));

            return;
        }


        sender.sendMessage(ChatColor.color( "&8&m-------------&r &6WidenCoins &8&m-------------&r"));

        for(HelpEntry command : help.getHelpEntries()) {

            sender.sendMessage(ChatColor.color("&8/&7"
                    + command.getCommand() + " "
                    + command.getParameterSyntax()
                    + " &8- &e" + command.getDescription())
            );

        }

        sender.sendMessage(ChatColor.color("&8&m------------------------------------&r"));

    }

    @Subcommand("give")
    @CommandPermission(Permission.GIVE_COINS)
    @Description("Give coins to a player")
    @Syntax("(player) (amount)")
    @CommandCompletion("@players")
    public void onGive(CommandSender sender, OnlinePlayer target, @Conditions("positive") Double amount) {

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        coinUtil.addCoins(playerUUID, amount);

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
    @Syntax("(player) (amount)")
    @CommandCompletion("@players")
    public void onTake(CommandSender sender, OnlinePlayer target, @Conditions("positive") Double amount) {

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // returns false if the player doesn't have enough coins
        boolean result = coinUtil.takeCoins(playerUUID, amount);

        if(!result) {
            sender.sendMessage(langHandler.get(LangKey.COIN_NOT_ENOUGH));
            return;
        }

        // get current target's coins
        double currentCoins = coinUtil.getCoins(playerUUID);

        sender.sendMessage(langHandler.get(LangKey.COIN_TAKEN)
                .replace("%player%", player.getName())
                .replace("%amount%", String.valueOf(amount))
                .replace("%current%", String.valueOf(currentCoins))
        );
    }

    @Subcommand("set")
    @CommandPermission(Permission.SET_COINS)
    @Description("Set a player's coins")
    @Syntax("(player) (amount)")
    @CommandCompletion("@players")
    public void onSet(CommandSender sender, OnlinePlayer target, @Conditions("positive") Double amount) {

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        coinUtil.setCoins(playerUUID, amount);

        sender.sendMessage(langHandler.get(LangKey.COIN_SET)
                .replace("%player%", player.getName())
                .replace("%amount%", String.valueOf(amount))
        );
    }

    @Subcommand("reset")
    @CommandPermission(Permission.RESET_COINS)
    @Description("Reset a player's coins")
    @Syntax("(player)")
    @CommandCompletion("@players")
    public void onReset(CommandSender sender, OnlinePlayer target) {

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        coinUtil.setCoins(playerUUID, 0);

        sender.sendMessage(langHandler.get(LangKey.COIN_RESET)
                .replace("%player%", player.getName())
        );
    }


    @Subcommand("view")
    @Description("View coins")
    @Syntax("(player)")
    @CommandCompletion("@players")
    public void onView(CommandSender sender, @Optional OnlinePlayer target) {

        if(target == null) {
            if(sender instanceof Player) {
                target = new OnlinePlayer((Player) sender);
            } else {
                return;
            }
        }

        Player player = target.getPlayer();
        UUID playerUUID = player.getUniqueId();

        boolean isSelf = sender == player;

        if(isSelf && sender.hasPermission(Permission.VIEW_COINS_SELF)) {

            String coins = "" + coinUtil.getCoins(playerUUID);

            sender.sendMessage(langHandler.get(LangKey.COIN_VIEW_SELF)
                    .replace("%amount%", coins)
            );

            return;
        }


        if(!isSelf && sender.hasPermission(Permission.VIEW_COINS_OTHERS)) {

            String coins = "" + coinUtil.getCoins(playerUUID);

            sender.sendMessage(langHandler.get(LangKey.COIN_VIEW_OTHER)
                    .replace("%player%", player.getName())
                    .replace("%amount%", coins)
            );

            return;
        }

        sender.sendMessage(langHandler.get(LangKey.NO_PERMISSION));




    }


    @Subcommand("reload")
    @CommandPermission(Permission.COMMAND_RELOAD)
    @Description("Reload the plugin configuration and languages")
    public void onReload(CommandSender sender) {

        try {
            langHandler.reload();

            instance.saveConfig();
            instance.reloadConfig();

            sender.sendMessage(langHandler.get(LangKey.RELOAD_SUCCESS));
        } catch (Exception e) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.CONFIG, e);
        }

    }






}

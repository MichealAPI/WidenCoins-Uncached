package it.mikeslab.widencoins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.mikeslab.widencoins.Permission;
import it.mikeslab.widencoins.command.helper.CoinDBHandler;
import it.mikeslab.widencoins.util.lang.LangHandler;
import it.mikeslab.widencoins.util.lang.LangKey;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("coin")
@RequiredArgsConstructor
public class CoinCommand extends BaseCommand {

    private final CoinDBHandler dataHandler;
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
    public void onGive(CommandSender sender, OnlinePlayer target, int amount) {
        dataHandler.giveCoins(target.getPlayer().getUniqueId(), amount);

        sender.sendMessage(langHandler.get(LangKey.COIN_GIVEN)
                .replace("%player%", target.getPlayer().getName())
                .replace("%amount%", String.valueOf(amount))
        );

        if(target.getPlayer() != sender) {
            target.getPlayer().sendMessage(
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
    public void onTake(CommandSender sender, OnlinePlayer target, int amount) {

        // get current target's coins
        int currentCoins = dataHandler.getCurrentAmount(target.getPlayer().getUniqueId());

        if(currentCoins < amount) {
            sender.sendMessage(langHandler.get(LangKey.COIN_NOT_ENOUGH));
            return;
        }

        dataHandler.takeCoins(target.getPlayer().getUniqueId(), amount);

        sender.sendMessage(langHandler.get(LangKey.COIN_TAKEN)
                .replace("%player%", target.getPlayer().getName())
                .replace("%amount%", String.valueOf(amount))
        );
    }

    @Subcommand("set")
    @CommandPermission(Permission.SET_COINS)
    @Description("Set a player's coins")
    @Syntax("<player> <amount>")
    @CommandCompletion("@players")
    public void onSet(CommandSender sender, OnlinePlayer target, int amount) {
        dataHandler.setCoins(target.getPlayer().getUniqueId(), amount);

        sender.sendMessage(langHandler.get(LangKey.COIN_SET)
                .replace("%player%", target.getPlayer().getName())
                .replace("%amount%", String.valueOf(amount))
        );
    }

    @Subcommand("reset")
    @CommandPermission(Permission.RESET_COINS)
    @Description("Reset a player's coins")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onReset(CommandSender sender, OnlinePlayer target) {
        dataHandler.resetCoins(target.getPlayer().getUniqueId());

        sender.sendMessage(langHandler.get(LangKey.COIN_RESET)
                .replace("%player%", target.getPlayer().getName())
        );
    }

    @Subcommand("view")
    @Description("View a player's coins")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onView(CommandSender sender, OnlinePlayer target) {

        boolean senderIsPlayer = sender instanceof Player;
        boolean self = sender == target.getPlayer();

        if(self && senderIsPlayer && !sender.hasPermission(Permission.VIEW_COINS_SELF)) return;

        if(!self && !sender.hasPermission(Permission.VIEW_COINS_OTHERS)) return;

        if(self && senderIsPlayer) {

            Player playerCommandSender = (Player) sender;

            playerCommandSender.sendMessage(langHandler.get(LangKey.COIN_VIEW_SELF)
                    .replace("%amount%", String.valueOf(dataHandler.getCurrentAmount(playerCommandSender.getUniqueId())))
            );
        } else {
            sender.sendMessage(langHandler.get(LangKey.COIN_VIEW_OTHER)
                    .replace("%player%", target.getPlayer().getName())
                    .replace("%amount%", String.valueOf(dataHandler.getCurrentAmount(target.getPlayer().getUniqueId())))
            );
        }

    }




}

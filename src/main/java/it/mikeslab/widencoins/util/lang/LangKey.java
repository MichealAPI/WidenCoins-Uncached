package it.mikeslab.widencoins.util.lang;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LangKey {

    PREFIX("prefix", "&9&lWidenCoins &8» &r"),
    COIN_GIVEN("coin.given", "&aYou have given &e%player% &a%amount% coins"),
    COIN_TAKEN("coin.taken", "&aYou have taken &e%amount% coins from &e%player%"),
    COIN_SET("coin.set", "&aYou have set &e%player%'s &acoins to &e%amount%"),
    COIN_RESET("coin.reset", "&aYou have reset &e%player%'s &acoins"),
    COIN_VIEW_OTHER("coin.view", "&a%player% &ahas &e%amount% &acoins"),
    COIN_VIEW_SELF("coin.view-self", "&aYou have &e%amount% &acoins"),
    COIN_RECEIVED("coin.received", "&aYou have received &e%amount% &acoins"),
    COIN_NOT_ENOUGH("coin.not-enough", "&cThe player does not have enough coins"),
    NO_PERMISSION("no-permission", "&cYou do not have permission to do that");

    private final String path, defaultValue;

}

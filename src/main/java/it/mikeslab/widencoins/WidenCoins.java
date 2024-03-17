package it.mikeslab.widencoins;

import co.aikar.commands.CommandReplacements;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import it.mikeslab.widencoins.command.CoinCommand;
import it.mikeslab.widencoins.database.DBConfigHandler;
import it.mikeslab.widencoins.database.caching.CacheHandler;
import it.mikeslab.widencoins.economy.EconomyImplementer;
import it.mikeslab.widencoins.economy.VaultHook;
import it.mikeslab.widencoins.lang.LangHandler;
import it.mikeslab.widencoins.lang.LangKey;
import it.mikeslab.widencoins.papi.PlaceholderAPIHook;
import it.mikeslab.widencoins.util.LoggerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;

public final class WidenCoins extends JavaPlugin {

    public static String COINS_KEY;

    public CacheHandler cacheHandler;
    public LangHandler langHandler;
    private DBConfigHandler dbConfigHandler;
    private VaultHook vaultHook;
    private PlaceholderAPIHook placeholderAPIHook;
    private boolean customEconomyEnabled;

    public EconomyImplementer economyImplementer;


    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        // Set the plugin name for the LoggerUtil
        LoggerUtil.setPluginName(this.getName());

        // Default key for coins in the database
        COINS_KEY = this.getConfig().getString("database.key", "coins");

        // Evaluates which database to use and initializes the connection
        this.initDatabaseConnection();

        // Initialize util which is used for the coin command, it's based on our database connection

        this.cacheHandler = new CacheHandler(dbConfigHandler);

        // Initialize languages
        this.initLanguages();

        // Register commands
        this.registerCommands();

        // Hooks in Vault and initializes the economy, if enabled
        this.customEconomyEnabled = this.getConfig().getBoolean(
                "custom-economy.enabled",
                false
        );

        if(customEconomyEnabled) {

            this.initCustomEconomy();

        }

        this.placeholderAPIHook = new PlaceholderAPIHook(this, cacheHandler);
        this.placeholderAPIHook.hook();


    }

    @Override
    public void onDisable() {

        // Saves all stored data to the MongoDB database instance
        this.cacheHandler.saveAll();

        this.dbConfigHandler
                .getDatabase()
                .disconnect();

        if(customEconomyEnabled) {
            this.vaultHook.unhook();
        }

        // Note: PlaceholderAPIHook is not unhooked here, because it's supposed to be persistent across reloads
        // placeholderAPIHook.unhook();
    }


    private void initDatabaseConnection() {
        ConfigurationSection dbSection = this.getConfig().getConfigurationSection("database");
        this.dbConfigHandler = new DBConfigHandler(dbSection);
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        String commandAliases = this.getConfig().getString("command-aliases", "coins");

        CommandReplacements replacements = manager.getCommandReplacements();
        replacements.addReplacement("%command-aliases%", commandAliases);

        manager.getLocales().loadLanguage(this.langHandler.langConfig, Locale.ENGLISH);

        manager.getCommandConditions().addCondition(Double.class, "positive", (c, exec, value) -> {

            if(value == null) {
                return;
            }

            if(value <= 0) {
                throw new ConditionFailedException(langHandler.getString(LangKey.INVALID_AMOUNT));
            }


        });

        // register commands
        manager.registerCommand(new CoinCommand(this));


        manager.enableUnstableAPI("help");
    }

    private void initCustomEconomy() {

        if(this.getServer().getPluginManager().getPlugin("Vault") == null) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.CONFIG, "Vault not found, disabling custom economy");
            return;
        }

        this.economyImplementer = new EconomyImplementer(
                this.langHandler,
                this.cacheHandler
        );

        this.vaultHook = new VaultHook(this);

        this.vaultHook.hook();

    }


    private void initLanguages() {

        // Generating default folders and files
        String defaultLangFilename = "messages_en.yml";

        File langFolder = new File(this.getDataFolder(), "lang");

        // If the folder doesn't exist, tries to create both the file and the folder
        if(!langFolder.exists()) {
            this.saveResource(
                    "lang" + File.separator + defaultLangFilename,
                    false
            );
        }

        // Load selected language
        String selectedLangFilename = this.getConfig().getString("language", null);

        if(selectedLangFilename == null) {
            LoggerUtil.log(Level.WARNING, LoggerUtil.LogSource.CONFIG, "No language selected, defaulting to '"
                    + defaultLangFilename +
                    "'"
            );

            this.getConfig().set("language", defaultLangFilename); // set default language
            this.saveConfig();

            selectedLangFilename = defaultLangFilename;
        }


        // Checking if selected language file really exists
        File selectedLangFile = new File(langFolder, selectedLangFilename);

        if(!selectedLangFile.exists()) {
            LoggerUtil.log(Level.WARNING, LoggerUtil.LogSource.CONFIG, "Selected language file not found, defaulting to '"
                    + defaultLangFilename +
                    "'"
            );

            selectedLangFilename = defaultLangFilename;
        }

        // Load the language file
        this.langHandler = new LangHandler(langFolder, selectedLangFilename);

    }

}

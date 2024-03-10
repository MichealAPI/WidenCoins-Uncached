package it.mikeslab.widencoins;

import co.aikar.commands.PaperCommandManager;
import it.mikeslab.widencoins.command.CoinCommand;
import it.mikeslab.widencoins.command.helper.CoinDBHandler;
import it.mikeslab.widencoins.database.DBConfigHandler;
import it.mikeslab.widencoins.database.Repository;
import it.mikeslab.widencoins.util.LoggerUtil;
import it.mikeslab.widencoins.util.lang.LangHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
public final class WidenCoins extends JavaPlugin {

    public static String COINS_KEY;

    private DBConfigHandler dbConfigHandler;
    private CoinDBHandler coinDBHandler;
    private LangHandler langHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        // Default key for coins in the database
        COINS_KEY = this.getConfig().getString("database.key", "coins");

        // Evaluates which database to use and initializes the connection
        this.initDatabaseConnection();

        // Initialize util which is used for the coin command, it's based on our database connection
        // Previously performed
        Repository repository = dbConfigHandler.getRepository();
        this.coinDBHandler = new CoinDBHandler(repository);

        // Initialize languages
        this.initLanguages();

        // Register commands
        this.registerCommands();



    }

    @Override
    public void onDisable() {
        this.dbConfigHandler
                .getDatabase()
                .disconnect();
    }


    private void initDatabaseConnection() {
        ConfigurationSection dbSection = this.getConfig().getConfigurationSection("database");
        this.dbConfigHandler = new DBConfigHandler(dbSection);
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        // register commands
        manager.registerCommand(new CoinCommand(coinDBHandler, langHandler));

        manager.enableUnstableAPI("help");
    }

    private void initLanguages() {

        // Generating default folders and files
        String defaultLangFilename = "messages_en.yml";

        File langFolder = new File(this.getDataFolder(), "lang");


        this.saveResource(
                "lang" + File.separator + defaultLangFilename,
                false
        );


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

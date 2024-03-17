package it.mikeslab.widencoins.lang;

import it.mikeslab.widencoins.util.LoggerUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LangHandler {

    private final File languageSubFolder;
    private final JavaPlugin instance;

    private final Map<LangKey, String> langCacheMap;
    public FileConfiguration langConfig;
    private String prefix;
    private String selectedLanguage;


    /**
     * Constructor
     * @param languageSubFolder the language subfolder
     * @param javaPlugin the JavaPlugin instance
     */
    public LangHandler(File languageSubFolder, JavaPlugin javaPlugin) {

        this.instance = javaPlugin;
        this.languageSubFolder = languageSubFolder;
        this.selectedLanguage = instance.getConfig()
                .getString("language");

        this.langCacheMap = new HashMap<>();

        this.load();
    }


    /**
     * Get the language string from the language file with the prefix
     * @param key the key to get the value from
     * @return the value of the key
     */
    public String get(LangKey key) {

        StringBuilder stringWithPrefix = new StringBuilder();

        if(prefix != null) {
            stringWithPrefix.append(prefix);
        }

        String value = this.getString(key);

        stringWithPrefix.append(value);

        return stringWithPrefix.toString();
    }

    /**
     * Get the language string from the language file without the prefix
     * @param key the key to get the value from
     * @return the value of the key
     */
    public String getString(LangKey key) {

        if(langConfig == null) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.CONFIG, "Language file not loaded, defaulting...");
            return key.getDefaultValue();
        }

        // check if the key is already in the cache
        if(langCacheMap.containsKey(key)) {
            return ChatColor.color(langCacheMap.get(key));
        }

        // if not, add it to the cache
        langCacheMap.put(key, langConfig.getString(key.getPath(), key.getDefaultValue()));

        return this.getString(key);
    }


    /**
     * Load the language file
     */
    private void load() {

        if(languageSubFolder.isDirectory()) {

            String langFileName = selectedLanguage;

            File langFile = new File(languageSubFolder, langFileName);

            if(langFile.exists()) {

                this.langConfig = YamlConfiguration.loadConfiguration(langFile);

            } else {
                LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.CONFIG, "Language file not found: " + langFileName);
            }

        }

        this.prefix = this.get(LangKey.PREFIX);

    }

    /**
     * Reload the language file
     */
    public void reload() {

        this.selectedLanguage = instance.getConfig()
                .getString("language");

        // Note: this.prefix is set to null here to prevent the prefix from being cached
        this.prefix = null;

        this.langCacheMap.clear();
        this.load();
    }




}
package it.mikeslab.widencoins.lang;

import it.mikeslab.widencoins.util.LoggerUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LangHandler {

    private final File languageSubFolder;
    private final String selectedLanguage;

    private final Map<LangKey, String> langCacheMap;
    public FileConfiguration langConfig;
    private String prefix;

    /**
     * Constructor
     * @param languageSubFolder the language subfolder
     * @param language the language to use
     */
    public LangHandler(File languageSubFolder, String language) {
        this.languageSubFolder = languageSubFolder;
        this.selectedLanguage = language;

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

        // Note: this.prefix is set to null here to prevent the prefix from being cached
        this.prefix = null;

        this.langCacheMap.clear();
        this.load();
    }







}

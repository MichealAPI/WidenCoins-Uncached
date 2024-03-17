package it.mikeslab.widencoins.database;

import it.mikeslab.widencoins.database.mongo.MongoDBImpl;
import it.mikeslab.widencoins.util.LoggerUtil;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Level;


/**
 * Handles the database configuration and creates the appropriate database and repository.
 */

@Getter
public class DBConfigHandler {

    private final ConfigurationSection databaseSection;
    private final URIBuilder uriBuilder;
    private MongoDBImpl database;


    public DBConfigHandler(ConfigurationSection databaseSection) {
        this.databaseSection = databaseSection;

        this.uriBuilder = this.fromConfig();

        if (uriBuilder == null) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.DATABASE, "Invalid database configuration");
            return;
        }


        this.database = new MongoDBImpl(uriBuilder);

        this.database.connect();
    }




    private URIBuilder fromConfig() {

        return URIBuilder.builder()
                .uri(databaseSection.getString("uri"))
                .database(databaseSection.getString("database"))
                .collection(databaseSection.getString("collection"))
                .build();

    }
}

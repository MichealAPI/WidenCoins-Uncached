package it.mikeslab.widencoins.database;

import it.mikeslab.widencoins.database.mongo.MongoDBImpl;
import it.mikeslab.widencoins.database.mongo.MongoDBRepository;
import it.mikeslab.widencoins.database.redis.RedisImpl;
import it.mikeslab.widencoins.database.redis.RedisRepository;
import it.mikeslab.widencoins.util.LoggerUtil;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Handles the database configuration and creates the appropriate database and repository.
 */

@Getter
public class DBConfigHandler {

    private final ConfigurationSection databaseSection;
    private DatabaseImpl database;
    private Repository repository;


    public DBConfigHandler(ConfigurationSection databaseSection) {
        this.databaseSection = databaseSection;

        URIBuilder uriBuilder = this.fromConfig();

        if (uriBuilder == null) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.DATABASE, "Invalid database configuration");
            return;
        }

        switch (uriBuilder.getType()) {
            case MONGODB:

                this.database = new MongoDBImpl(uriBuilder);

                // table stands for collection when working with MongoDB
                this.repository = new MongoDBRepository((MongoDBImpl) database, uriBuilder.getTable());

                break;

            case REDIS:

                this.database = new RedisImpl(uriBuilder);

                this.repository = new RedisRepository((RedisImpl) database);

                break;
        }


    }




    private URIBuilder fromConfig() {

        if (!isDatabaseTypeValid(databaseSection.getString("type"))) {
            return null;
        }

        return URIBuilder.builder()
                .type(DBType.valueOf(databaseSection.getString("type")))
                .host(databaseSection.getString("host"))
                .username(databaseSection.getString("username"))
                .password(databaseSection.getString("password"))
                .port(databaseSection.getInt("port"))
                .database(databaseSection.getString("database"))
                .table(databaseSection.getString("table"))
                .build();
    }

    private boolean isDatabaseTypeValid(String type) {
        try {
            DBType.valueOf(type);
            return true;
        } catch (IllegalArgumentException e) {
            //WidenCoins.getInstance().getLogger().severe("Invalid database type: " + type);
            // todo: implement proper logging system
            System.out.println("Invalid database type: " + type);
            return false;
        }
    }

    public enum DBType {
        MONGODB,
        REDIS
    }
}

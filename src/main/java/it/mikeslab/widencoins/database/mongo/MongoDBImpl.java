package it.mikeslab.widencoins.database.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import it.mikeslab.widencoins.database.DatabaseImpl;
import it.mikeslab.widencoins.database.URIBuilder;
import it.mikeslab.widencoins.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Collections are improperly called "tables" in this implementation
 * due to the fact that we support multiple databases,
 * each with its own structure
 */
@RequiredArgsConstructor
public class MongoDBImpl implements DatabaseImpl {

    // No need of collection since we call them, improperly, "tables"
    private final URIBuilder uriBuilder;
    private MongoClient mongoClient;
    private MongoDatabase database;

    @Override
    public boolean connect() {

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        String uri = this.getUri();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi)
                .build();

        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase(uriBuilder.getDatabase());

        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);
            LoggerUtil.log(
                    Level.INFO,
                    LoggerUtil.LogSource.DATABASE,
                    "Pinged your deployment. You successfully connected to MongoDB!"
            );

        } catch (MongoException me) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.DATABASE,
                    "Error while connecting to MongoDB: " + me.getMessage()
            );
            return false;
        }

        return true;
    }

    @Override
    public boolean disconnect() {

        try {
            this.mongoClient.close();
            this.mongoClient = null; // Preventing memory leaks

        } catch (Exception e) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.DATABASE, "Error while disconnecting from MongoDB: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean isConnected() {

        // Preventing NullPointerException in case the client is null
        if (this.mongoClient == null) {
            return false;
        }

        try {

            // Send a ping to confirm a successful connection
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);

        } catch (MongoException me) {
            LoggerUtil.log(Level.SEVERE,
                            LoggerUtil.LogSource.DATABASE,
                    "Error while checking connection to MongoDB: " + me.getMessage()
            );
            return false;
        }


        // If no exception was thrown, we are connected
        return true;
    }



    @Override
    public boolean createTable(String name, String... columns) {

        try {
            database.createCollection(name);

        } catch (MongoException me) {
            LoggerUtil.log(Level.SEVERE,
                            LoggerUtil.LogSource.DATABASE,
                    "Error while creating a collection in MongoDB: " + me.getMessage()
            );
            return false;
        }

        return true;
    }



    @Override
    public boolean deleteTable(String name) {

        try {
            database.getCollection(name).drop();

        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                            LoggerUtil.LogSource.DATABASE,
                    "Error while deleting a collection in MongoDB: " + me.getMessage()
            );
            return false;
        }

        return true;
    }

    @Override
    public boolean insert(String table, String key, Map<String, String> values) {

        // Values is an unmodifiable map, so we need to create a new one
        Map<String, String> tempValues = new HashMap<>(values);
        tempValues.put("key", key);

        try {
            database.getCollection(table).insertOne(new Document(tempValues));
        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                    LoggerUtil.LogSource.DATABASE,
                    "Error while inserting into MongoDB: " + me.getMessage()
            );
            return false;
        }

        return true;
    }

    @Override
    public boolean update(String table, String key, Map<String, String> values) {

        try {
            database.getCollection(table).updateOne(new Document("key", key), new Document("$set", new Document(values)));

        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                    LoggerUtil.LogSource.DATABASE,
                    "Error while updating MongoDB: " + me.getMessage()
            );
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(String table, String key) {

        try {
            database.getCollection(table).deleteOne(new Document("key", key));

        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                    LoggerUtil.LogSource.DATABASE,
                    "Error while deleting from MongoDB: " + me.getMessage()
            );
            return false;
        }

        return true;
    }

    @Override
    public boolean exists(String table, String key) {

        try {
            Document result = database.getCollection(table).find(new Document("key", key)).first();

            return result != null;

        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                            LoggerUtil.LogSource.DATABASE,
                    "Error while checking if a key exists in MongoDB: " + me.getMessage()
            );
        }

        return false;
    }

    @Override
    public Map<String, String> select(String table, String key) {

        try {
            Document result = database.getCollection(table).find(new Document("key", key)).first();

            Map<String, String> resultMap = new HashMap<>();

            // Convert the result to a Map<String, String>
            if (result != null) {
                result.forEach((k, v) -> resultMap.put(k, v.toString()));
            }

            // Given this, Map will never be null, but it can be empty
            return resultMap;

        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                            LoggerUtil.LogSource.DATABASE,
                    "Error while selecting from MongoDB: " + me.getMessage()
            );
        }

        return null;
    }


    public String getUri() {

        return "mongodb+srv://" + uriBuilder.getUsername() + ":"
                                + uriBuilder.getPassword() + "@"
                                + uriBuilder.getHost()
                                + "/?appName=WidenCoins";
        
    }
}

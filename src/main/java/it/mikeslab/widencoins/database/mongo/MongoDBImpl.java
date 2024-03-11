package it.mikeslab.widencoins.database.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
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

/**
 * MongoDB's implementation of the Database interface.
 */
@RequiredArgsConstructor
public class MongoDBImpl implements DatabaseImpl {

    // No need of collection since we call them, improperly, "collections"
    private final URIBuilder uriBuilder;
    private MongoClient mongoClient;
    private MongoDatabase database;


    /**
     * Connect to the database
     * @return true if the client was connected, false otherwise
     */
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

        return isConnected(false);
    }


    /**
     * Disconnect from the database
     * @return true if the client was disconnected, false otherwise
     */
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


    /**
     * Check if the client is connected
     * @return true if the client is connected, false otherwise
     */
    @Override
    public boolean isConnected(boolean silent) {

        // Preventing NullPointerException in case the client is null
        if (this.mongoClient == null) {
            return false;
        }

        try {

            // Send a ping to confirm a successful connection
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);

            if (!silent) {
                LoggerUtil.log(Level.INFO, LoggerUtil.LogSource.DATABASE, "Pinged your deployment. You successfully connected to MongoDB!");
            }

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


    /**
     * Create a collection
     * @param name the name of the collection to create
     * @return true if the collection was created, false otherwise
     */
    @Override
    public boolean createCollection(String name) {

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


    /**
     * Drop a collection
     * @param name the name of the collection to drop
     * @return true if the collection was dropped, false otherwise
     */
    public boolean dropCollection(String name) {

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



    /**
     * Insert or update a document in a collection
     * @param collection the collection to insert into
     * @param key the key to insert, for this implementation it's the UUID
     * @param values the values to insert
     * @return true if the document was inserted, false otherwise
     */
    @Override
    public boolean upsert(String collection, String key, Map<String, String> values) {

        try {

            UpdateOptions options = new UpdateOptions().upsert(true);

            database.getCollection(collection).updateOne(
                    new Document("uuid", key),
                    new Document("$set", new Document(values)),
                    options);

        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                    LoggerUtil.LogSource.DATABASE,
                    "Error while upserting in MongoDB: " + me.getMessage()
            );
            return false;
        }

        return true;
    }

    /**
     * Delete a document from a collection
     * @param collection the collection to delete from
     * @param key the key to delete, for this implementation it's the UUID
     * @return true if the document was deleted, false otherwise
     */
    @Override
    public boolean delete(String collection, String key) {

        try {
            database.getCollection(collection).deleteOne(new Document("uuid", key));

        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                    LoggerUtil.LogSource.DATABASE,
                    "Error while deleting from MongoDB: " + me.getMessage()
            );
            return false;
        }

        return true;
    }

    /**
     * Check if a key exists in a collection
     * @param collection the collection to check
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    @Override
    public boolean exists(String collection, String key) {

        try {
            Document result = database.getCollection(collection).find(new Document("uuid", key)).first();

            return result != null;

        } catch (MongoException me) {
            LoggerUtil.log(Level.WARNING,
                            LoggerUtil.LogSource.DATABASE,
                    "Error while checking if a key exists in MongoDB: " + me.getMessage()
            );
        }

        return false;
    }

    /**
     * Select a document from a collection
     * @param collection the collection to select from
     * @param key the key to select, for this implementation it's the UUID
     * @return a Map<String, String> with the result where the key is the field name and the value is the field value
     */
    @Override
    public Map<String, String> select(String collection, String key) {

        try {
            Document result = database.getCollection(collection).find(new Document("uuid", key)).first();

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


    /**
     * Get the URI to connect to the database
     * @return the URI
     */
    public String getUri() {

        return "mongodb+srv://" + uriBuilder.getUsername() + ":"
                                + uriBuilder.getPassword() + "@"
                                + uriBuilder.getHost()
                                + "/?appName=WidenCoins";
        
    }

}

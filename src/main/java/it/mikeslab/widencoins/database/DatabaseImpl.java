package it.mikeslab.widencoins.database;

import java.util.Map;

public interface DatabaseImpl {

    boolean connect();
    boolean disconnect();
    boolean upsert(String collection, String key, Map<String, String> values);
    boolean delete(String collection, String key);
    boolean isConnected(boolean silent);
    boolean exists(String collection, String key);
    boolean createCollection(String collection);
    boolean dropCollection(String collection);
    Map<String, String> select(String collection, String key);

}

package it.mikeslab.widencoins.database;

import java.util.Map;

public interface DatabaseImpl {

    boolean connect();

    boolean disconnect();

    boolean isConnected();

    boolean createTable(String name, String... columns);

    boolean deleteTable(String name);

    boolean insert(String table, String key, Map<String, String> values);

    boolean update(String table, String key, Map<String, String> values);

    boolean delete(String table, String key);

    boolean exists(String table, String key);

    Map<String, String> select(String table, String key);
}

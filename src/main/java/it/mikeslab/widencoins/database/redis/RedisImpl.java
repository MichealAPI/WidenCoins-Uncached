package it.mikeslab.widencoins.database.redis;

import it.mikeslab.widencoins.database.DatabaseImpl;
import it.mikeslab.widencoins.database.URIBuilder;
import it.mikeslab.widencoins.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.logging.Level;

@RequiredArgsConstructor
public class RedisImpl implements DatabaseImpl {

    private final URIBuilder uriBuilder;

    private Jedis jedis;

    @Override
    public boolean connect() {

        try {

            this.jedis = new Jedis(uriBuilder.getHost(), uriBuilder.getPort());

        } catch (Exception e) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.DATABASE, "Error while connecting to Redis: " + e.getMessage());
            return false;
        }


        return true;
    }

    @Override
    public boolean disconnect() {
        try {

            this.jedis.close();

        } catch (Exception e) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.DATABASE, "Error while disconnecting from Redis: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean isConnected() {
        return this.jedis != null && this.jedis.isConnected();
    }

    @Override
    public boolean createTable(String name, String... columns) {
        return false; // Redis is a key-value store, no tables
    }

    @Override
    public boolean deleteTable(String name) {
        return false; // Redis is a key-value store, no tables
    }

    @Override
    public boolean insert(String table, String key, Map<String, String> values) {
        return this.update(table, key, values);
    }

    @Override
    public boolean update(String table, String key, Map<String, String> values) {

        // table gets ignored, as Redis is a key-value store

        try {

            this.jedis.hmset(key, values);

        } catch (Exception e) {
            e.printStackTrace(); // todo implement proper logging system
            return false;
        }

        return true;
    }



    @Override
    public boolean delete(String table, String key) {

        // table gets ignored, as Redis is a key-value store

        try {

            this.jedis.del(key);

        } catch (Exception e) {
            e.printStackTrace(); // todo implement proper logging system
            return false;
        }

        return true;
    }

    @Override
    public boolean exists(String table, String key) {

        // table gets ignored, as Redis is a key-value store

        try {

            return this.jedis.exists(key);

        } catch (Exception e) {
            e.printStackTrace(); // todo implement proper logging system
        }

        return false;

    }


    @Override
    public Map<String, String> select(String table, String key) {

        // table gets ignored, as Redis is a key-value store

        try {

            return this.jedis.hgetAll(key);

        } catch (Exception e) {
            e.printStackTrace(); // todo implement proper logging system
        }

        return null;
    }


}

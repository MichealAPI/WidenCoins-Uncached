package it.mikeslab.widencoins.database.redis;

import it.mikeslab.widencoins.database.Repository;

import java.util.Map;
import java.util.Optional;

/**
 * Repository for Redis.
 * NOTE: Redis is a key-value store, so the table is null.
 */
public class RedisRepository implements Repository {

    private final RedisImpl redis;

    public RedisRepository(RedisImpl redis) {
        this.redis = redis;

        // establish connection
        this.redis.connect();
    }

    @Override
    public void upsert(String key, Map<String, String> values) {

        // table is null since Redis is a key-value store
        redis.insert(null, key, values);

    }

    @Override
    public void remove(String key) {
        redis.delete(null, key);
    }

    @Override
    public Optional<Map<String, String>> get(String key) {
        return Optional.ofNullable(redis.select(null, key));
    }


}

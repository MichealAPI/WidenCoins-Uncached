package it.mikeslab.widencoins.database.mongo;

import it.mikeslab.widencoins.database.Repository;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
public class MongoDBRepository implements Repository {

    private final MongoDBImpl mongoDB;
    private final String collection;

    public MongoDBRepository(MongoDBImpl mongoDB, String collection) {
        this.mongoDB = mongoDB;
        this.collection = collection;

        // establish connection
        this.mongoDB.connect();
    }


    @Override
    public void upsert(String key, Map<String, String> values) {

        boolean isExistent = !mongoDB.select(collection, key).isEmpty();

        if (isExistent) {
            mongoDB.update(collection, key, values);
        } else {
            mongoDB.insert(collection, key, values);
        }

    }

    @Override
    public void remove(String key) {
        mongoDB.delete(collection, key);
    }

    @Override
    public Optional<Map<String, String>> get(String key) {
        return Optional.ofNullable(mongoDB.select(collection, key));
    }
}

package it.mikeslab.widencoins.database;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class URIBuilder {

    private final DBConfigHandler.DBType type;

    private final String host,
                        username,
                        password;

    private final int port;

    private String database,
                    table; // table is null if the database is a key-value store, like Redis
                           // In MongoDB, table stands for the collection name
}

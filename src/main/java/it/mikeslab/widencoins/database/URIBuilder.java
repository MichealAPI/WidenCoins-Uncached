package it.mikeslab.widencoins.database;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class URIBuilder {

    private final String host,
                        username,
                        password;

    private final int port;

    private String database,
                    collection;

}

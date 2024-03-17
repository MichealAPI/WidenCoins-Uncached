package it.mikeslab.widencoins.database;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class URIBuilder {

    private final String uri;

    private String database,
                    collection;

}

package com.example.tabletsproducerservice.inspectors;

import com.example.tabletsproducerservice.entity.Icons;

import com.datastax.driver.core.Row;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Map;

public class Inspector extends StringOperations {
    protected Inspector () {
        icons = super.newMap();
    }

    // содержит все типы полицейских
    protected static Map< String, Icons > icons;

    protected final synchronized void save (
            final Row row
    ) {
        icons.put( row.getString( "policeType" ), new Icons().generate( row ) );
    }

    protected final synchronized  <T> Mono< T > convert ( final T o ) {
        return Optional.ofNullable( o ).isPresent() ? Mono.just( o ) : Mono.empty();
    }
}

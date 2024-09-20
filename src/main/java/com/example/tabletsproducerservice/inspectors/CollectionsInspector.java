package com.example.tabletsproducerservice.inspectors;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import java.util.stream.Stream;
import java.util.*;

public class CollectionsInspector extends TimeInspector {
    protected CollectionsInspector() {}

    protected final synchronized <T, V> Map<T, V> newMap () {
        return new HashMap<>();
    }

    /*
    получает ResultSet в котором находиться Row объект с данными из БД
    После чего конвертирует его в Stream
    */
    protected final synchronized Stream< Row > convertRowToStream (
            final ResultSet resultSet
    ) {
        return resultSet.all().stream();
    }

    protected final synchronized <T, U> boolean isCollectionNotEmpty (
            final Map<T, U> collection
    ) {
        return collection != null && !collection.isEmpty();
    }

    protected final synchronized <T, V> void analyze (
            @lombok.NonNull final Map< T, V > someMap,
            @lombok.NonNull final BiConsumer<T, V> someConsumer
    ) {
        someMap.forEach( someConsumer );
    }

    protected final synchronized <T> void analyze (
            @lombok.NonNull final Collection<T> someList,
            @lombok.NonNull final Consumer<T> someConsumer
    ) {
        someList.forEach( someConsumer );
    }
}

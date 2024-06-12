package com.example.tabletsproducerservice.inspectors;

import com.example.tabletsproducerservice.database.cassandraConfigs.CassandraParamsAndOptionsStore;

import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;
import reactor.core.publisher.Flux;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import java.util.List;

/*
хранит все функции для более компактного и удобного хранения всех основных функции WebFlux
*/
public class WebFluxInspector extends CassandraParamsAndOptionsStore {
    protected WebFluxInspector() {}

    protected final synchronized ParallelFlux< Row > convertValuesToParallelFlux (
            final ResultSet resultSet,
            final int parallelsCount
    ) {
        return Flux.fromStream(
                    super.convertRowToStream( resultSet )
                ).parallel( super.checkDifference( parallelsCount ) )
                .runOn( Schedulers.parallel() );
    }

    protected final synchronized <T> ParallelFlux< T > convertValuesToParallelFlux (
            final List< T > collections
    ) {
        return Flux.fromStream(
                        collections.stream()
                ).parallel( super.checkDifference( collections.size() ) )
                .runOn( Schedulers.parallel() );
    }
}

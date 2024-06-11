package com.example.tabletsproducerservice.inspectors;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.BiFunction;

import reactor.core.publisher.Mono;
import com.example.tabletsproducerservice.entity.Icons;

public class Inspector {
    // содержит все типы полицейских
    protected final Map< String, Icons > icons = new HashMap<>();

    protected <T> Mono< T > convert ( final T o ) { return Optional.ofNullable( o ).isPresent() ? Mono.just( o ) : Mono.empty(); }

    protected final Function< Long, Date > getDate = aLong -> aLong > 0L ? new Date( aLong ) : new Date();

    protected final BiFunction< Date, Date, Integer > getTimeDifference = ( date, date2 ) ->
            (int) Math.abs( Duration.between( date.toInstant(), date2.toInstant() ).toDays() );
}

package com.example.tabletsproducerservice.interfaces;

import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.Row;

public interface ObjectCommonMethods< T > {
    default T generate ( final UDTValue udtValue ) {
        return null;
    }

    default T generate () {
        return null;
    }

    T generate ( final Row row );
}

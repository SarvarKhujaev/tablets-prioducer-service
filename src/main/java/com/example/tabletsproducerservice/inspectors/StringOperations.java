package com.example.tabletsproducerservice.inspectors;

import com.example.tabletsproducerservice.entity.patrulDataSet.PatrulFIOData;
import com.example.tabletsproducerservice.constants.CassandraCommands;

import java.util.Date;
import java.util.UUID;

public class StringOperations extends CollectionsInspector {
    protected StringOperations() {}

    protected final synchronized String concatNames (
            final PatrulFIOData patrulFIOData
    ) {
        return String.join(
                " ",
                patrulFIOData.getName(),
                patrulFIOData.getSurname(),
                patrulFIOData.getFatherName()
        );
    }

    /*
    генерируем сообщение с началом Транзакции
     */
    protected final synchronized StringBuilder newStringBuilder () {
        return new StringBuilder( CassandraCommands.BEGIN_BATCH );
    }

    protected final synchronized StringBuilder newStringBuilder ( final String s ) {
        return new StringBuilder( s );
    }

    /*
    принимает параметр для Cassandra, который является типом TEXТ,
    и добавляет в начало и конец апострафы
    */
    protected final synchronized String joinWithAstrix ( final Object value ) {
        return "$$" + value + "$$";
    }

    /*
    принимает параметр для Cassandra, который является типом TIMESTAMP,
    и добавляет в начало и конец апострафы
    */
    protected final synchronized String joinWithAstrix ( final Date date ) {
        return "'" + date.toInstant() + "'";
    }

    protected final synchronized String generateID () {
        return "ID = '%s'".formatted( UUID.randomUUID() );
    }
}

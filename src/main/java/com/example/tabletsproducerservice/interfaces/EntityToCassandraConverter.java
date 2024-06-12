package com.example.tabletsproducerservice.interfaces;

import com.example.tabletsproducerservice.constants.CassandraCommands;
import com.example.tabletsproducerservice.database.CassandraDataControl;

public interface EntityToCassandraConverter {
    default String getEntityInsertCommand () {
        return CassandraCommands.INSERT_INTO;
    };

    default String getEntityDeleteCommand () {
        return CassandraCommands.DELETE;
    }

    default String getEntityUpdateCommand () {
        return CassandraCommands.UPDATE;
    }

    /*
    сохраняет любой объект
    */
    default boolean save () {
        return CassandraDataControl
                .getInstance()
                .getSession()
                .execute( this.getEntityInsertCommand() )
                .wasApplied();
    }

    default boolean delete () {
        return CassandraDataControl
                .getInstance()
                .getSession()
                .execute( this.getEntityDeleteCommand() )
                .wasApplied();
    }

    default boolean updateEntity() {
        return CassandraDataControl
                .getInstance()
                .getSession()
                .execute( this.getEntityUpdateCommand() )
                .wasApplied();
    }
}

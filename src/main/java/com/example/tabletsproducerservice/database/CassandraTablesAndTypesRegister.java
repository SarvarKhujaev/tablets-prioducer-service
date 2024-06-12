package com.example.tabletsproducerservice.database;

import com.datastax.driver.core.Session;

import com.example.tabletsproducerservice.inspectors.CassandraConverter;
import com.example.tabletsproducerservice.database.cassandraConfigs.CassandraCreateKeyspaces;

/*
создает все таблицы, типы, кодеки и пространство ключей
*/
public final class CassandraTablesAndTypesRegister extends CassandraConverter {
    private final Session session;

    private Session getSession() {
        return this.session;
    }

    public static void generate (
            final Session session
    ) {
        new CassandraTablesAndTypesRegister( session );
    }

    private CassandraTablesAndTypesRegister(
            final Session session
    ) {
        this.session = session;

        CassandraCreateKeyspaces.generate( this.getSession() );

        super.logging( this.getClass() );
    }
}

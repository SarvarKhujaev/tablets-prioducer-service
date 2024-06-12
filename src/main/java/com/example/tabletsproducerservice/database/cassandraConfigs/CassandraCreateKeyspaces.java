package com.example.tabletsproducerservice.database.cassandraConfigs;

import com.example.tabletsproducerservice.constants.CassandraCommands;
import com.example.tabletsproducerservice.constants.CassandraTables;
import com.datastax.driver.core.Session;

public final class CassandraCreateKeyspaces {
    private final Session session;

    private Session getSession() {
        return this.session;
    }

    public static void generate (
            final Session session
    ) {
        new CassandraCreateKeyspaces( session );
    }

    private CassandraCreateKeyspaces (
            final Session session
    ) {
        this.session = session;
        this.createAllKeyspace();
        CassandraCreateTypes.generate( this.getSession() );
    }

    /*
    Хранит все данные для создания нового пространства в БД
    */
    private static class KeyspaceRegistration {
        public String getPrefix() {
            return this.prefix;
        }

        /*
        хранит CQL команду для оздания нового пространства
        */
        private final String prefix;

        public static KeyspaceRegistration from(
                final CassandraTables keyspace
        ) {
            return new KeyspaceRegistration( keyspace );
        }

        private KeyspaceRegistration (
                final CassandraTables keyspace
        ) {
            this.prefix = String.format(
                    """
                    %s %s %s
                    WITH REPLICATION = {
                        'class' : 'SimpleStrategy',
                        'replication_factor': 1
                        }
                    AND DURABLE_WRITES = false;
                    """,
                    CassandraCommands.CREATE_KEYSPACE,
                    CassandraCommands.IF_NOT_EXISTS.replaceAll( ";", "" ),
                    keyspace
            );
        }
    }

    private void createAllKeyspace () {
        /*
        создаем все пространства в БД
         */
        this.createKeyspace( KeyspaceRegistration.from( CassandraTables.TABLETS ) );
        this.createKeyspace( KeyspaceRegistration.from( CassandraTables.ESCORT ) );
    }

    /*
    функция создает новые пространства в БД
    */
    private void createKeyspace (
            final KeyspaceRegistration keyspaceRegistration
    ) {
        this.getSession().execute( keyspaceRegistration.getPrefix() );
    }
}

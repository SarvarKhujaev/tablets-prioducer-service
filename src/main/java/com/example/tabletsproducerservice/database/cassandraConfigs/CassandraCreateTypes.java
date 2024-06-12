package com.example.tabletsproducerservice.database.cassandraConfigs;

import com.example.tabletsproducerservice.inspectors.CassandraConverter;
import com.example.tabletsproducerservice.constants.CassandraDataTypes;
import com.example.tabletsproducerservice.constants.CassandraCommands;
import com.example.tabletsproducerservice.constants.CassandraTables;
import com.example.tabletsproducerservice.entity.patrulDataSet.*;

import com.datastax.driver.core.Session;
import java.text.MessageFormat;

public final class CassandraCreateTypes {
    private final Session session;

    private Session getSession() {
        return this.session;
    }

    public static void generate (
            final Session session
    ) {
        new CassandraCreateTypes( session );
    }

    private CassandraCreateTypes (
            final Session session
    ) {
        this.session = session;
        this.createAllTypes();
        CassandraCreateTables.generate( this.getSession() );
    }

    /*
    Хранит все данные для создания нового типа
    и сохранения в БД
    */
    private static class TypeRegistration extends CassandraConverter {
        public String getConvertedValues() {
            return this.convertedValues;
        }

        public CassandraTables getKeyspace() {
            return this.keyspace;
        }

        public CassandraTables getTable() {
            return this.table;
        }

        /*
        навзвание пространства в котором находиться таблица
        */
        private final CassandraTables keyspace;

        /*
        навзвание таблицы
         */
        private final CassandraTables table;

        /*
        сконвертированное значение объекта в CQL понятный язык
        */
        private final String convertedValues;

        public static TypeRegistration from (
                final CassandraTables keyspace,
                final CassandraTables table,
                final String prefix,
                final Class<?> object
        ) {
            return new TypeRegistration( keyspace, table, prefix, object );
        }

        private TypeRegistration (
                final CassandraTables keyspace,
                final CassandraTables table,
                final String prefix,
                final Class<?> object
        ) {
            this.convertedValues = super.convertClassToCassandra.apply( object ) + prefix;
            this.keyspace = keyspace;
            this.table = table;
        }
    }

    /*
    функция создает новые типы в БД
    */
    private void createType (
            final TypeRegistration typeRegistration
    ) {
        this.getSession().execute(
                MessageFormat.format(
                        """
                        {0} {1}.{2} {3} );
                        """,
                        CassandraCommands.CREATE_TYPE,

                        typeRegistration.getKeyspace(),
                        typeRegistration.getTable(),
                        typeRegistration.getConvertedValues()
                )
        );
    }

    private void createAllTypes () {
        /*
        создаем все UDT в БД
        */
        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_CAR_DATA, "", PatrulCarInfo.class ) );
        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_FIO_DATA, "", PatrulFIOData.class ) );

        this.createType(
                TypeRegistration.from(
                        CassandraTables.TABLETS,
                        CassandraTables.PATRUL_TASK_DATA,
                        MessageFormat.format(
                                """
                                , listOfTasks {2}< {1}< {0}, {0} > >
                                """,
                                CassandraDataTypes.TEXT,
                                CassandraDataTypes.MAP,
                                CassandraDataTypes.FROZEN
                        ),
                        PatrulTaskInfo.class
                )
        );

        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_DATE_DATA, "", PatrulDateData.class ) );
        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_AUTH_DATA, "", PatrulAuthData.class ) );
        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_TOKEN_DATA, "", PatrulTokenInfo.class ) );
        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_REGION_DATA, "", PatrulRegionData.class ) );
        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_UNIQUE_DATA, "", PatrulUniqueValues.class ) );
        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_MOBILE_DATA, "", PatrulMobileAppInfo.class ) );
        this.createType( TypeRegistration.from( CassandraTables.TABLETS, CassandraTables.PATRUL_LOCATION_DATA, "", PatrulLocationData.class ) );
    }
}

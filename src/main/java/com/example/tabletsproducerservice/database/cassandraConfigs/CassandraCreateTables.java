package com.example.tabletsproducerservice.database.cassandraConfigs;

import com.example.tabletsproducerservice.inspectors.CassandraConverter;
import com.example.tabletsproducerservice.entity.patrulDataSet.Patrul;
import com.example.tabletsproducerservice.inspectors.TimeInspector;
import com.example.tabletsproducerservice.constants.*;

import com.datastax.driver.core.Session;
import java.text.MessageFormat;

public final class CassandraCreateTables extends CassandraConverter {
    private final Session session;

    private Session getSession() {
        return this.session;
    }

    public static void generate (
            final Session session
    ) {
        new CassandraCreateTables( session );
    }

    private CassandraCreateTables (
            final Session session
    ) {
        this.session = session;
        this.createAllTables();
    }

    /*
    Хранит все данные для создания новой таблицы в БД
    */
    private static class TableRegistration extends CassandraConverter {
        public CassandraTables getTableName() {
            return this.tableName;
        }

        public CassandraTables getKeyspace() {
            return this.keyspace;
        }

        public String getConvertedValue() {
            return convertedValue;
        }

        public String getPrefix() {
            return this.prefix;
        }

        /*
        название таблицы
        */
        private final CassandraTables tableName;
        /*
        навзвание пространства в котором находиться таблица
        */
        private final CassandraTables keyspace;

        /*
        сконвертированное значение объекта в CQL понятный язык
        */
        private final String convertedValue;
        /*
        хранит дом значение для CQL
        в особенности Primary key
         */
        private final String prefix;

        public static TableRegistration from (
                final CassandraTables keyspace,
                final CassandraTables tableName,
                final Class<?> object,
                final String prefix
        ) {
            return new TableRegistration( keyspace, tableName, object, prefix );
        }

        private TableRegistration (
                final CassandraTables keyspace,
                final CassandraTables tableName,
                final Class<?> object,
                final String prefix
        ) {
            this.convertedValue = super.convertClassToCassandra.apply( object );
            this.tableName = tableName;
            this.keyspace = keyspace;
            this.prefix = prefix;
        }
    }

    /*
    функция создает новые таблицы в БД
    */
    private void createTable (
            final TableRegistration tableRegistration
    ) {
        this.getSession().execute(
                MessageFormat.format(
                        """
                        {0} {1}.{2} {3} {4};
                        """,
                        CassandraCommands.CREATE_TABLE,
                        tableRegistration.getKeyspace(),
                        tableRegistration.getTableName(),
                        tableRegistration.getConvertedValue(),
                        tableRegistration.getPrefix()
                )
        );
    }


    private void createAllTables () {
        /*
        создаем все таблицы в БД
         */

        this.createTable(
                TableRegistration.from(
                        CassandraTables.TABLETS,
                        CassandraTables.PATRULS,
                        Patrul.class,
                        MessageFormat.format(
                                """
                                , listOfTasks {11}< {10}, {10} >,       -- хранит список всех задач которые выполнил патрульный
                                patrulFIOData {0},                      -- данные о ФИО
                                patrulCarInfo {1},                      -- данные о машине патрульного
                                patrulDateData {2},                     -- данные обо всех параметрах связанных с датами
                                patrulAuthData {3},                     -- данные о аутентификации патрульного
                                patrulTaskInfo {4},                     -- данные касающиеся задач патрульного
                                patrulTokenInfo {5},                    -- данные о токенах
                                patrulRegionData {6},                   -- данные о регионе службы патрульного
                                patrulLocationData {7},                 -- данные о локации патрульного
                                patrulUniqueValues {8},                 -- все уникальные параметры патрульного
                                patrulMobileAppInfo {9},                -- мобильные данные патрульного
                                PRIMARY KEY ( (uuid), passportNumber ) ) {12};
                                """,
                                CassandraTables.PATRUL_FIO_DATA,
                                CassandraTables.PATRUL_CAR_DATA,
                                CassandraTables.PATRUL_DATE_DATA,
                                CassandraTables.PATRUL_AUTH_DATA,
                                CassandraTables.PATRUL_TASK_DATA,
                                CassandraTables.PATRUL_TOKEN_DATA,
                                CassandraTables.PATRUL_REGION_DATA,
                                CassandraTables.PATRUL_LOCATION_DATA,
                                CassandraTables.PATRUL_UNIQUE_DATA,
                                CassandraTables.PATRUL_MOBILE_DATA,

                                CassandraDataTypes.TEXT,
                                CassandraDataTypes.MAP,

                                """
                                %s AND %s AND %s AND %s AND %s AND %s AND %s
                                """.formatted(
                                        CassandraCommands.WITH_CLUSTERING_ORDER.formatted( "passportNumber ASC" ),
                                        CassandraCommands.WITH_COMPACTION.formatted( CassandraCompactionTypes.LEVELED_COMPACTION ),
                                        CassandraCommands.WITH_COMMENT.formatted(
                                                """
                                                хранит данные, обо всех патрульных, является основной таблицой
                                                """
                                        ),
                                        CassandraCommands.WITH_COMPRESSION.formatted( CassandraCompressionTypes.LZ4, 64 ),
                                        CassandraCommands.WITH_TTL.formatted( TimeInspector.DAY_IN_SECOND * 365 * 8 ),
                                        super.generateID(),
                                        CassandraCommands.WITH_CACHING.formatted(
                                                CassandraDataTypes.ALL, CassandraDataTypes.NONE
                                        )
                                )
                        )
                )
        );

        this.getSession().execute(
                MessageFormat.format(
                        """
                        {0} {1}.{2}
                        (
                        login       {3},        -- уникальный логин для каждого пользователей
                        password    {3},        -- пароль
                        uuid        {4},        -- уникальный ID пользователей
                        {5}
                        """,
                        CassandraCommands.CREATE_TABLE,

                        CassandraTables.TABLETS,
                        CassandraTables.PATRULS_LOGIN_TABLE,

                        CassandraDataTypes.TEXT,
                        CassandraDataTypes.UUID,

                        """
                        PRIMARY KEY ( (login), uuid ) ) WITH %s AND %s AND %s AND %s AND %s;
                        """.formatted(
                                CassandraCommands.WITH_COMPACTION.formatted( CassandraCompactionTypes.SIZE_TIERED_COMPACTION ),
                                CassandraCommands.WITH_COMMENT.formatted(
                                        """
                                        хранит данные, о том логине и паролях для входа в аккаунт патрульного
                                        """
                                ),
                                CassandraCommands.WITH_COMPRESSION.formatted( CassandraCompressionTypes.LZ4, 64 ),
                                CassandraCommands.WITH_CACHING.formatted( CassandraDataTypes.NONE, CassandraDataTypes.NONE ),
                                super.generateID()
                        )
                )
        );
    }
}

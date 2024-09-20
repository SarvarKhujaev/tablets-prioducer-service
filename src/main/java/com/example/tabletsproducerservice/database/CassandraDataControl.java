package com.example.tabletsproducerservice.database;

import java.util.Map;
import java.util.List;
import java.text.MessageFormat;

import java.util.function.Consumer;
import java.util.function.Function;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import com.datastax.driver.core.*;

import com.example.tabletsproducerservice.entity.*;
import com.example.tabletsproducerservice.controller.Request;
import com.example.tabletsproducerservice.constants.CassandraTables;
import com.example.tabletsproducerservice.inspectors.WebFluxInspector;
import com.example.tabletsproducerservice.payload.ReqExchangeLocation;
import com.example.tabletsproducerservice.entity.patrulDataSet.Patrul;
import com.example.tabletsproducerservice.constants.CassandraCommands;
import com.example.tabletsproducerservice.subscribers.CustomSubscriber;
import com.example.tabletsproducerservice.kafkaDataSet.KafkaDataControl;
import com.example.tabletsproducerservice.interfaces.ServiceCommonMethods;
import com.example.tabletsproducerservice.interfaces.DatabaseCommonMethods;
import com.example.tabletsproducerservice.database.cassandraConfigs.CassandraParamsAndOptionsStore;

public final class CassandraDataControl
        extends WebFluxInspector
        implements ServiceCommonMethods, DatabaseCommonMethods {
    private final Cluster cluster;
    private final Session session;

    private static CassandraDataControl cassandraDataControl = new CassandraDataControl();

    public static CassandraDataControl getInstance() {
        return cassandraDataControl != null
                ? cassandraDataControl
                : ( cassandraDataControl = new CassandraDataControl() );
    }

    public Cluster getCluster() {
        return this.cluster;
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    public void register () {
        super.convertValuesToParallelFlux(
                this.getListOfEntities( CassandraTables.POLICE_TYPE )
        ).subscribe( new CustomSubscriber<>( super::save ) );
    }

    private CassandraDataControl () {
        this.session = (
                this.cluster = Cluster
                        .builder()
                        .withPort( super.getPort() )
                        .addContactPoint( CassandraParamsAndOptionsStore.HOST )
                        .withClusterName( CassandraParamsAndOptionsStore.CLUSTER_NAME )
                        .withProtocolVersion( ProtocolVersion.V4 )
                        .withRetryPolicy( super.getCustomRetryPolicy() )
                        .withQueryOptions(
                                new QueryOptions()
                                        .setDefaultIdempotence( true )
                                        .setConsistencyLevel( ConsistencyLevel.ONE )
                        ).withSocketOptions( super.getSocketOptions() )
                        .withPoolingOptions( super.getPoolingOptions() )
                        .withCompression( ProtocolOptions.Compression.LZ4 )
                        .build()
        ).connect();

        super.logging( this.getClass() );

        /*
        создаем, регистрируем и сохраняем все таблицы, типы и кодеки
        */
        CassandraTablesAndTypesRegister.generate(
                this.getSession()
        );
    }

    /*
    возвращает ROW из БД для любой таблицы внутри TABLETS
    */
    @Override
    public Row getRowFromTabletsKeyspace (
            // название таблицы внутри Tablets
            final CassandraTables cassandraTableName,
            // название колонки
            final String columnName,
            // параметр по которому введется поиск
            final String paramName
    ) {
        return this.getSession().execute(
                MessageFormat.format(
                        """
                        {0} {1}.{2} WHERE {3} = {4};
                        """,
                        CassandraCommands.SELECT_ALL,

                        CassandraTables.TABLETS,

                        cassandraTableName,
                        columnName,
                        paramName
                )
        ).one();
    }

    @Override
    public List< Row > getListOfEntities (
            // название таблицы внутри Tablets
            final CassandraTables cassandraTableName
    ) {
        return this.getSession().execute(
                MessageFormat.format(
                        """
                        {0} {1}.{2};
                        """,
                        CassandraCommands.SELECT_ALL,

                        CassandraTables.TABLETS,
                        cassandraTableName
                )
        ).all();
    }

    public final Consumer< ReqExchangeLocation > addNewReqExchangeLocation = reqExchangeLocation -> {
            final Patrul patrul = Patrul.empty().generate(
                    this.getRowFromTabletsKeyspace(
                            CassandraTables.PATRULS,
                            "uuid",
                            reqExchangeLocation.getPassportSeries()
                    )
            );

            final Icons icons = new Icons().generate(
                    this.getRowFromTabletsKeyspace(
                            CassandraTables.POLICE_TYPE,
                            patrul.getPoliceType(),
                            "policeType"
                    )
            );

            final StringBuilder stringBuilder = super.newStringBuilder();

            KafkaDataControl
                    .getINSTANCE()
                    .writeToKafka
                    .accept(
                            super.convertValuesToParallelFlux(
                                    reqExchangeLocation.getReqLocationExchanges()
                            ).map( reqLocationExchange -> {
                                stringBuilder.append(
                                        patrul
                                                .update( reqLocationExchange )
                                                .update(
                                                        patrul,
                                                        icons
                                                ).getEntityUpdateCommand()
                                );

                                return reqLocationExchange;
                            } )
                    );

            this.getSession().execute( stringBuilder.append( CassandraCommands.APPLY_BATCH ).toString() );
    };

    public final Function< Map< String, Long >, Flux< LastPosition > > getLastPositions = params -> super.convertValuesToParallelFlux(
                    this.getListOfEntities(
                            CassandraTables.PATRULS
                    )
            ).filter( super::checkLocation )
            .filter( row -> !super.isCollectionNotEmpty( params ) || super.checkParams( row, params ) )
            .map( row -> new LastPosition().generate( row ) )
            .doOnError( this::close )
            .sequential()
            .publishOn( Schedulers.single() );

    public final Function< Request, Flux< PositionInfo > > getHistory = request -> super.checkRequest( request )
            ? super.convertValuesToParallelFlux(
                    this.getSession().execute(
                            MessageFormat.format(
                                    """
                                       {0} {1}.{2}
                                       WHERE userId = {3}
                                       AND date >= {4}
                                       AND date <= {5}
                                       ORDER BY date;
                                       """,
                                    CassandraCommands.SELECT_ALL,

                                    CassandraTables.GPSTABLETS,
                                    CassandraTables.TABLETS_LOCATION_TABLE,

                                    super.joinWithAstrix( request.getPassportSeries() ),
                                    super.joinWithAstrix( request.getStartDate() ),
                                    super.joinWithAstrix( request.getEndDate() )
                            )
                    ),
                    super.checkDifference( super.getTimeDifference( request.getStartDate(), request.getEndDate() ) )
            ).map( row -> new PositionInfo().generate( row ) )
            .sequential()
            .publishOn( Schedulers.single() )
            : Flux.empty();

    public final Function< String, Mono< ApiResponseModel > > checkToken = token -> {
        final Patrul patrul = Patrul.empty().generate(
                this.getRowFromTabletsKeyspace(
                        CassandraTables.PATRULS,
                        "uuid",
                        super.decode( token ).toString()
                )
        );
        return super.convert(
                ApiResponseModel
                        .builder()
                        .data(
                                Data
                                        .builder()
                                        .data( patrul )
                                        .build()
                        ).status(
                                Status
                                        .builder()
                                        .message( patrul.getUuid().toString() )
                                        .code( 200 )
                                        .build()
                        ).success( true )
                        .build()
        );
    };

    @Override
    public void close( final Throwable throwable ) {
        super.logging( throwable );
        super.logging( this );
        this.close();
    }

    @Override
    public void close() {
        cassandraDataControl = null;
        super.logging( this );
        this.getCluster().close();
        this.getSession().close();
        KafkaDataControl.getINSTANCE().close();
    }
}
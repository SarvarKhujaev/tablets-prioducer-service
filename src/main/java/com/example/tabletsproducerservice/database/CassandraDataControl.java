package com.example.tabletsproducerservice.database;

import java.util.Map;
import java.util.UUID;
import java.util.Base64;

import java.util.function.Consumer;
import java.util.function.Function;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;

import com.example.tabletsproducerservice.entity.*;
import com.example.tabletsproducerservice.controller.Request;
import com.example.tabletsproducerservice.constants.CassandraTables;
import com.example.tabletsproducerservice.payload.ReqExchangeLocation;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;
import com.example.tabletsproducerservice.TabletsProducerServiceApplication;

@lombok.Data
public final class CassandraDataControl extends DataValidateInspector {
    private final Cluster cluster;
    private final Session session;

    private static CassandraDataControl cassandraDataControl = new CassandraDataControl();

    public static CassandraDataControl getInstance() { return cassandraDataControl != null ? cassandraDataControl
            : ( cassandraDataControl = new CassandraDataControl() ); }

    public void register () {
        Flux.fromStream( this.getSession().execute(
                "SELECT * FROM "
                        + CassandraTables.TABLETS + "."
                        + CassandraTables.POLICE_TYPE + ";" )
                .all()
                .stream() )
                .subscribe( row -> super.icons.put( row.getString( "policeType" ), new Icons( row ) ) ); }

    private CassandraDataControl () {
        final SocketOptions options = new SocketOptions();
        options.setConnectTimeoutMillis( 30000 );
        options.setReadTimeoutMillis( 300000 );
        options.setTcpNoDelay( true );
        options.setKeepAlive( true );
        ( this.session = ( this.cluster = Cluster
                .builder()
                .withClusterName( "GpsTablets" )
                .addContactPoints( "10.254.5.1, 10.254.5.2, 10.254.5.3".split( ", " ) )
                .withPort( Integer.parseInt( TabletsProducerServiceApplication
                        .context
                        .getEnvironment()
                        .getProperty( "variables.CASSANDRA_PORT" ) ) )
                .withQueryOptions( new QueryOptions()
                        .setDefaultIdempotence( true )
                        .setConsistencyLevel( ConsistencyLevel.QUORUM ) )
                .withRetryPolicy( new CustomRetryPolicy( 3, 3, 3 ) )
                .withProtocolVersion( ProtocolVersion.V4 )
                .withSocketOptions( options )
                .withLoadBalancingPolicy( new TokenAwarePolicy( DCAwareRoundRobinPolicy.builder().build() ) )
                .withPoolingOptions( new PoolingOptions()
                        .setCoreConnectionsPerHost( HostDistance.REMOTE, Integer.parseInt(
                                TabletsProducerServiceApplication
                                    .context
                                    .getEnvironment()
                                    .getProperty( "variables.CASSANDRA_CORE_CONN_REMOTE" ) ) )
                        .setCoreConnectionsPerHost( HostDistance.LOCAL, Integer.parseInt(
                                TabletsProducerServiceApplication
                                    .context
                                    .getEnvironment()
                                    .getProperty( "variables.CASSANDRA_CORE_CONN_LOCAL" ) ) )
                        .setMaxConnectionsPerHost( HostDistance.REMOTE, Integer.parseInt(
                                TabletsProducerServiceApplication
                                    .context
                                    .getEnvironment()
                                    .getProperty( "variables.CASSANDRA_MAX_CONN_REMOTE" ) ) )
                        .setMaxConnectionsPerHost( HostDistance.LOCAL, Integer.parseInt(
                                TabletsProducerServiceApplication
                                    .context
                                    .getEnvironment()
                                    .getProperty( "variables.CASSANDRA_MAX_CONN_LOCAL" ) ) )
                        .setMaxRequestsPerConnection( HostDistance.REMOTE, 1024 )
                        .setMaxRequestsPerConnection( HostDistance.LOCAL, 1024 )
                        .setPoolTimeoutMillis( 60000 ) )
                .build() )
                .connect() )
                .execute( "CREATE KEYSPACE IF NOT EXISTS "
                        + CassandraTables.GPSTABLETS
                        + " WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy'," +
                        "'datacenter1':3 } AND DURABLE_WRITES = false;" );

        this.getSession().execute( "CREATE TABLE IF NOT EXISTS "
                + CassandraTables.GPSTABLETS + "."
                + CassandraTables.TABLETS_LOCATION_TABLE
                + " ( userId text, " +
                "date timestamp, " +
                "latitude double, " +
                "longitude double, " +
                "PRIMARY KEY ( (userId), date ) );" );
        super.logging( "Cassandra is ready" ); }

    private final Function< Patrul, Boolean > updatePatrulLocation = patrul -> this.getSession().execute(
            "UPDATE "
                    + CassandraTables.TABLETS + "."
                    + CassandraTables.PATRULS
                    + " SET latitude = " + patrul.getLatitude()
                    + ", longitude = " + patrul.getLongitude()
                    + ", lastActiveDate = '" + super.getDate.apply( 0L ).toInstant()
                    + "', batteryLevel = " + patrul.getBatteryLevel()
                    + " WHERE uuid = " + patrul.getUuid() + ";" )
            .wasApplied();

    private final Consumer< ReqExchangeLocation > addNewReqExchangeLocation = reqExchangeLocation ->
            KafkaDataControl
                    .getINSTANCE()
                    .getWriteToKafka()
                    .accept( Flux.fromStream( reqExchangeLocation
                                    .getReqLocationExchanges()
                                    .stream()
                                    .parallel() )
                            .parallel( super.checkDifference.apply( reqExchangeLocation.getReqLocationExchanges().size() ) )
                            .runOn( Schedulers.parallel() )
                            .flatMap( reqLocationExchange -> this.getGetPatrul().apply( reqExchangeLocation.getPassportSeries() )
                                    .flatMap( patrul -> {
                                        patrul.update( reqLocationExchange );
                                        reqLocationExchange.update( patrul,
                                                super.checkSosTable.test( patrul.getUuid() ),
                                                super.icons.getOrDefault( patrul.getPoliceType(), this.getGetPoliceType().apply( patrul.getPoliceType() ) ) );

                                        super.logging( "Cassandra got "
                                                + " patrulUUID: " + patrul.getUuid()
                                                + " passport: " + reqLocationExchange.getPatrulPassportSeries()
                                                + " at: " + super.getDate.apply( reqLocationExchange.getDate() )
                                                + " location: " + reqLocationExchange.getLongitude()
                                                + " location: " + reqLocationExchange.getLatitude()
                                                + " was applied: " + this.getUpdatePatrulLocation().apply( patrul ) );

                                        this.getSession().execute( "INSERT INTO "
                                                + CassandraTables.GPSTABLETS + "."
                                                + CassandraTables.TABLETS_LOCATION_TABLE
                                                + " ( userId, date, latitude, longitude, speed, address ) VALUES ('"
                                                + reqLocationExchange.getPatrulPassportSeries() + "', '"
                                                + super.getDate.apply( reqLocationExchange.getDate() ).toInstant() + "', "
                                                + reqLocationExchange.getLatitude() + ", "
                                                + reqLocationExchange.getLongitude() + ", "
                                                + ( super.checkParam.test( reqLocationExchange.getSpeed() )
                                                ? reqLocationExchange.getSpeed() : 0 ) + ", '' );" );

                                        return super.convert( reqLocationExchange ); } ) )
                            .sequential()
                            .publishOn( Schedulers.single() ) );

    private final Function< Map< String, Long >, Flux< LastPosition > > getLastPositions = params -> Flux.fromStream (
            this.getSession().execute( "SELECT * FROM "
                    + CassandraTables.TABLETS + "."
                    + CassandraTables.PATRULS )
                    .all()
                    .stream()
                    .parallel() )
            .parallel( 30 )
            .runOn( Schedulers.parallel() )
            .filter( super.checkLocation )
            .filter( row -> super.checkParam.test( params ) && !params.isEmpty()
                    ? super.checkParams.apply( row, params )
                    : true )
            .map( row -> new LastPosition (
                    row,
                    super.icons.getOrDefault( row.getString( "policeType" ),
                            this.getGetPoliceType().apply( row.getString( "policeType" ) ) ),
                    super.checkSosTable.test( row.getUUID( "uuid" ) ) ) )
            .doOnError( this::delete )
            .sequential()
            .publishOn( Schedulers.single() );

    private final Function< String, Icons > getPoliceType = policeType -> new Icons(
            this.getSession().execute( "SELECT icon, icon2 FROM "
                    + CassandraTables.TABLETS + "."
                    + CassandraTables.POLICE_TYPE
                    + " WHERE policeType = '" + policeType + "';" ).one() );

    private final Function< Request, Flux< PositionInfo > > getHistory = request -> super.checkRequest.test( request )
            ? Flux.fromStream( this.getSession().execute( "SELECT * FROM "
                        + CassandraTables.GPSTABLETS + "."
                        + CassandraTables.TABLETS_LOCATION_TABLE
                        + " WHERE userId = '" + request.getPassportSeries()
                        + "' AND date >= '" + request.getStartDate().toInstant()
                        + "' AND date <= '" + request.getEndDate().toInstant() + "' ORDER BY date;" )
                    .all()
                    .stream()
                    .parallel() )
                    .parallel( super.checkDifference.apply( super.getTimeDifference.apply( request.getStartDate(), request.getEndDate() ) ) )
                    .runOn( Schedulers.parallel() )
                    .map( PositionInfo::new )
                    .sequential()
                    .publishOn( Schedulers.single() )
            : Flux.empty();

    private final Function< String, Mono< Patrul > > getPatrul = passportNumber -> super.convert(
            new Patrul ( this.getSession().execute( "SELECT * FROM "
                    + CassandraTables.TABLETS + "."
                    + CassandraTables.PATRULS
                    + " WHERE uuid = " + UUID.fromString( passportNumber ) + ";" ).one() ) );

    private final Function< String, Mono< ApiResponseModel > > checkToken = token -> this.getGetPatrul()
            .apply( this.getDecode().apply( token ) )
            .flatMap( patrul -> super.convert( ApiResponseModel
                    .builder()
                    .data( Data.builder().data( patrul ).build() )
                    .status( Status
                            .builder()
                            .message( patrul.getUuid().toString() )
                            .code( 200 )
                            .build() )
                    .success( true )
                    .build() ) );

    private final Function< String, String > decode = token -> new String( Base64.getDecoder().decode( token ) ).split( "@" )[ 0 ];

    public void delete ( final Throwable throwable ) {
        this.getSession().close();
        this.getCluster().close();
        super.logging( throwable );
        cassandraDataControl = null;
        super.logging( "Cassandra is closed!!!" ); }
}
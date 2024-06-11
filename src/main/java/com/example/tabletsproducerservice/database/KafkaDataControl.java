package com.example.tabletsproducerservice.database;

import com.example.tabletsproducerservice.TabletsProducerServiceApplication;
import com.example.tabletsproducerservice.payload.ReqLocationExchange;
import com.example.tabletsproducerservice.inspectors.LogInspector;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.KafkaSender;

import reactor.core.scheduler.Schedulers;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.gson.Gson;
import java.util.*;

@lombok.Data
public final class KafkaDataControl extends LogInspector {
    private final Gson gson = new Gson();
    private static KafkaDataControl INSTANCE = new KafkaDataControl();

    public final String KAFKA_BROKER = TabletsProducerServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.KAFKA_BROKER" );

    public final String GROUP_ID_FOR_KAFKA = TabletsProducerServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GROUP_ID_FOR_KAFKA" );

    public final String TABLETS_GPS_DATA = TabletsProducerServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.TABLETS_GPS_DATA" );

    public static KafkaDataControl getINSTANCE() { return INSTANCE != null ? INSTANCE : ( INSTANCE = new KafkaDataControl() ); }

    private final Supplier< Map< String, Object > > getKafkaSenderOptions = () -> Map.of(
            ProducerConfig.ACKS_CONFIG, "1",
            ProducerConfig.CLIENT_ID_CONFIG, this.getGROUP_ID_FOR_KAFKA(),
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getKAFKA_BROKER(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );

    private final KafkaSender< String, String > kafkaSender = KafkaSender.create(
            SenderOptions.< String, String >create( this.getGetKafkaSenderOptions().get() )
                    .maxInFlight( 1024 ) );

    private KafkaDataControl () { super.logging( "KafkaDataControl was created" ); }

    private final Consumer< Flux< ReqLocationExchange > > writeToKafka = reqLocationExchangeFlux ->
            this.getKafkaSender()
                    .createOutbound()
                    .send( reqLocationExchangeFlux
                            .parallel()
                            .runOn( Schedulers.parallel() )
                            .map( reqLocationExchange -> new ProducerRecord<> (
                                    this.getTABLETS_GPS_DATA(),
                                    this.getGson().toJson( reqLocationExchange ) ) ) )
                    .then()
                    .doOnError( super::logging )
                    .doOnSuccess( success -> super.logging( "All locations were sent" ) )
                    .subscribe();
}

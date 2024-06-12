package com.example.tabletsproducerservice.database;

import com.example.tabletsproducerservice.inspectors.DataValidateInspector;
import com.example.tabletsproducerservice.interfaces.ServiceCommonMethods;
import com.example.tabletsproducerservice.subscribers.CustomSubscriber;
import com.example.tabletsproducerservice.payload.ReqLocationExchange;
import com.example.tabletsproducerservice.publisher.CustomPublisher;
import com.example.tabletsproducerservice.constants.Errors;

import org.apache.kafka.clients.producer.ProducerConfig;

import reactor.core.publisher.ParallelFlux;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.KafkaSender;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.gson.Gson;
import java.util.*;

public final class KafkaDataControl extends DataValidateInspector implements ServiceCommonMethods {
    private final Gson gson = new Gson();
    private static KafkaDataControl INSTANCE = new KafkaDataControl();

    private final String TABLETS_GPS_DATA = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_TOPICS.TABLETS_GPS_DATA",
            Errors.DATA_NOT_FOUND.translate( "en" )
    );

    public static KafkaDataControl getINSTANCE() {
        return INSTANCE != null ? INSTANCE : ( INSTANCE = new KafkaDataControl() );
    }

    private final Supplier< Map< String, Object > > getKafkaSenderOptions = () -> Map.of(
            ProducerConfig.ACKS_CONFIG, super.checkContextOrReturnDefaultValue(
                    "variables.KAFKA_VARIABLES.KAFKA_ACKS_CONFIG",
                    "1"
            ),
            ProducerConfig.CLIENT_ID_CONFIG, super.checkContextOrReturnDefaultValue(
                    "variables.KAFKA_VARIABLES.GROUP_ID_FOR_KAFKA",
                    this.getClass().getName()
            ),
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, super.checkContextOrReturnDefaultValue(
                    "variables.KAFKA_VARIABLES.KAFKA_BROKER",
                    "localhost:9092"
            ),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class
    );

    private final KafkaSender< String, String > kafkaSender = KafkaSender.create(
            SenderOptions.< String, String >create( this.getKafkaSenderOptions.get() )
                    .maxInFlight(
                            super.checkContextOrReturnDefaultValue(
                                    "variables.KAFKA_VARIABLES.KAFKA_SENDER_MAX_IN_FLIGHT",
                                    1024
                            )
                    )
    );

    private KafkaDataControl () {
        super.logging( this.getClass() );
    }

    public final Consumer< ParallelFlux< ReqLocationExchange > > writeToKafka = reqLocationExchangeFlux ->
            reqLocationExchangeFlux.map(
                    reqLocationExchange -> CustomPublisher.from(
                            this.TABLETS_GPS_DATA,
                            this.gson.toJson( reqLocationExchange ),
                            reqLocationExchange.getPatrulUUID().toString()
                    )
            ).map( customPublisher -> this.kafkaSender
                    .createOutbound()
                    .send( customPublisher )
                    .then()
                    .doOnError( super::logging )
                    .doOnSuccess(
                            success -> super.logging(
                                String.join(
                                        " ",
                                        "Location from: ",
                                        customPublisher.getResponse(),
                                        "was received"
                                )
                            )
                    )
            ).subscribe(
                    new CustomSubscriber<>(
                            success -> super.logging( "All locations were sent" )
                    )
            );

    @Override
    public void close() {
        super.logging( this );
        this.kafkaSender.close();
        INSTANCE = null;
    }
}

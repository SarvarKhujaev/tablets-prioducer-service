package com.example.tabletsproducerservice.kafkaDataSet;

import com.example.tabletsproducerservice.interfaces.ServiceCommonMethods;
import com.example.tabletsproducerservice.subscribers.CustomSubscriber;
import com.example.tabletsproducerservice.payload.ReqLocationExchange;
import com.example.tabletsproducerservice.publisher.CustomPublisher;
import com.example.tabletsproducerservice.constants.Errors;

import org.apache.kafka.clients.producer.ProducerConfig;

import org.apache.kafka.common.record.CompressionType;
import reactor.core.publisher.ParallelFlux;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.KafkaSender;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.gson.Gson;
import java.util.*;

public final class KafkaDataControl extends KafkaOptionsAndParams implements ServiceCommonMethods {
    private final Gson gson = new Gson();
    private static KafkaDataControl INSTANCE = new KafkaDataControl();

    private final String TABLETS_GPS_DATA = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_TOPICS.TABLETS_GPS_DATA",
            Errors.DATA_NOT_FOUND.translate( "en" )
    );

    public static KafkaDataControl getINSTANCE() {
        return INSTANCE != null ? INSTANCE : ( INSTANCE = new KafkaDataControl() );
    }

    private final Supplier< Map< String, Object > > getKafkaSenderOptions = () -> {
        final Map< String, Object > options = super.newMap();

//        options.put( ProducerConfig.ACKS_CONFIG, KAFKA_ACKS_CONFIG );
//
//        // The number of times to retry sending a message if it fails.
//        options.put( ProducerConfig.RETRIES_CONFIG, RETRIES_CONFIG );
//
//        // The maximum time to wait before sending a batch to the broker
//        options.put( ProducerConfig.LINGER_MS_CONFIG, LINGER_MS_CONFIG );
//
//        // The maximum size of the batch to send to the broker
//        options.put( ProducerConfig.BATCH_SIZE_CONFIG, BATCH_SIZE_CONFIG );
//
//        // The maximum amount of memory to use for buffering messages
//        options.put( ProducerConfig.BUFFER_MEMORY_CONFIG, BUFFER_MEMORY_CONFIG );
//
//        // The maximum time to wait for a response from the broker
//        options.put( ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS_CONFIG );
//
//        // The maximum number of outstanding requests to send to the broker
//        options.put( ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION );
//
//        options.put( ProducerConfig.CLIENT_ID_CONFIG, GROUP_ID_FOR_KAFKA );
//
//        // The compression algorithm to use for messages
//        options.put(
//                ProducerConfig.COMPRESSION_TYPE_CONFIG,
//                checkContextOrReturnDefaultValue(
//                        "variables.KAFKA_VARIABLES.COMPRESSION_TYPE_CONFIG",
//                        CompressionType.LZ4.name
//                )
//        );
//
//        options.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER );
//
//        // The maximum age of metadata in milliseconds
//        options.put( ProducerConfig.METADATA_MAX_AGE_CONFIG, METADATA_MAX_AGE_CONFIG );
//
//        options.put( ProducerConfig.TRANSACTIONAL_ID_CONFIG, generateTimeBased() );
//        options.put( ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, TRANSACTION_TIMEOUT_CONFIG );

//        options.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );
//        options.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class );

        /*
        Kafka Producer Buffer
        Kafka Transaction
        Kafka Schema Registry
        Kafka Avro
        Kafka Log Compaction
        Kafka Retention
        Kafka Producer Interceptor
        Kafka Configuration
        Kafka Metrics
        Kafka Security
        Kafka Authentication
        Kafka Authorization
        Kafka SSL/TLS: If you need to encrypt your Kafka messages, consider using a Kafka SSL/TLS. It provides a simple and efficient way to encrypt your Kafka messages.
        Kafka SASL
         */
        return options;
    };

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

    public final Consumer< ParallelFlux< ReqLocationExchange > > writeToKafka = reqLocationExchangeFlux -> {
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
    };

    @Override
    public void close() {
        super.logging( this );
        this.kafkaSender.close();
        INSTANCE = null;
    }
}

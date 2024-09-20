package com.example.tabletsproducerservice.kafkaDataSet;

import org.apache.kafka.clients.producer.KafkaProducer;

@SuppressWarnings(
        value = """
                собирает и выдает всю статистику о работе Kafka
                """
)
public sealed class KafkaMetrics extends KafkaOptionsAndParams permits KafkaDataControl {
    protected final synchronized void collectMetrics (
            final KafkaProducer< String, String > kafkaProducer
    ) {
        super.analyze(
                kafkaProducer.metrics(),
                ( key, value ) -> super.logging(
                        key + " : " + value
                )
        );
    }
}

package com.example.tabletsproducerservice.kafkaDataSet;

import com.example.tabletsproducerservice.constants.Errors;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;

public sealed class KafkaOptionsAndParams extends DataValidateInspector permits KafkaMetrics {
    protected final String KAFKA_ACKS_CONFIG = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_ACKS_CONFIG",
            KafkaOptionsAndParams.class.getDeclaredFields()[1].getName()
    );

    protected final String GROUP_ID_FOR_KAFKA = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.GROUP_ID_FOR_KAFKA",
            KafkaOptionsAndParams.class.getDeclaredFields()[2].getName()
    );

    protected final String KAFKA_BROKER = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_BROKER",
            "localhost:9092"
    );

    protected final static int KAFKA_SENDER_MAX_IN_FLIGHT = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_SENDER_MAX_IN_FLIGHT",
            1024
    );

    protected final static int RETRIES_CONFIG = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.RETRIES_CONFIG",
            3
    );

    protected final static int LINGER_MS_CONFIG = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.LINGER_MS_CONFIG",
            DAY_IN_SECOND / 10000
    );

    protected final static int BATCH_SIZE_CONFIG = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.BATCH_SIZE_CONFIG",
            DAY_IN_SECOND / 4 // 21 Kb
    );

    /*
    The buffer.memory property sets the total memory allocated for the producer buffer.
     */
    protected final static int BUFFER_MEMORY_CONFIG = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.BUFFER_MEMORY_CONFIG",
            DAY_IN_SECOND * 40 // 32MB buffer size
    );

    protected final static int REQUEST_TIMEOUT_MS_CONFIG = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.REQUEST_TIMEOUT_MS_CONFIG",
            DAY_IN_SECOND / 100
    );

    protected final static int MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION",
            3
    );

    protected final static int METADATA_MAX_AGE_CONFIG = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.METADATA_MAX_AGE_CONFIG",
            DAY_IN_SECOND
    );

    protected final static int TRANSACTION_TIMEOUT_CONFIG = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.TRANSACTION_TIMEOUT_CONFIG",
            DAY_IN_SECOND / 10000
    );

    protected final static String TABLETS_GPS_DATA = checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_TOPICS.TABLETS_GPS_DATA",
            Errors.DATA_NOT_FOUND.translate( "en" )
    );
}

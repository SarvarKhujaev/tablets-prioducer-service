package com.example.tabletsproducerservice.kafkaDataSet;

import com.example.tabletsproducerservice.inspectors.DataValidateInspector;

public sealed class KafkaOptionsAndParams extends DataValidateInspector permits KafkaDataControl {
//    protected final String KAFKA_ACKS_CONFIG = checkContextOrReturnDefaultValue(
//            "KAFKA_ACKS_CONFIG",
//            KafkaOptionsAndParams.class.getDeclaredFields()[1].getName()
//    );
//
//    protected final String GROUP_ID_FOR_KAFKA = checkContextOrReturnDefaultValue(
//            "GROUP_ID_FOR_KAFKA",
//            KafkaOptionsAndParams.class.getDeclaredFields()[2].getName()
//    );
//
//    protected final String KAFKA_BROKER = checkContextOrReturnDefaultValue(
//            "KAFKA_BROKER",
//            KafkaOptionsAndParams.class.getDeclaredFields()[3].getName()
//    );
//
//    protected final static int KAFKA_SENDER_MAX_IN_FLIGHT = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[4].getName()
//            ),
//            1024
//    );
//
//    protected final static int RETRIES_CONFIG = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[5].getName()
//            ),
//            3
//    );
//
//    protected final static int LINGER_MS_CONFIG = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[6].getName()
//            ),
//            DAY_IN_SECOND / 10000
//    );
//
//    protected final static int BATCH_SIZE_CONFIG = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[7].getName()
//            ),
//            DAY_IN_SECOND / 4 // 21 Kb
//    );
//
//    /*
//    The buffer.memory property sets the total memory allocated for the producer buffer.
//     */
//    protected final static int BUFFER_MEMORY_CONFIG = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[8].getName()
//            ),
//            DAY_IN_SECOND * 40 // 32MB buffer size
//    );
//
//    protected final static int REQUEST_TIMEOUT_MS_CONFIG = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[9].getName()
//            ),
//            DAY_IN_SECOND / 100
//    );
//
//    protected final static int MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[10].getName()
//            ),
//            3
//    );
//
//    protected final static int METADATA_MAX_AGE_CONFIG = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[11].getName()
//            ),
//            DAY_IN_SECOND
//    );
//
//    protected final static int TRANSACTION_TIMEOUT_CONFIG = checkContextOrReturnDefaultValue(
//            AnnotationInspector.getVariable(
//                    KafkaOptionsAndParams.class,
//                    KafkaOptionsAndParams.class.getDeclaredFields()[11].getName()
//            ),
//            DAY_IN_SECOND / 10000
//    );
}

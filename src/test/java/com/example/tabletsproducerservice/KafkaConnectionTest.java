package com.example.tabletsproducerservice;

import com.example.tabletsproducerservice.database.KafkaDataControl;
import junit.framework.TestCase;

public final class KafkaConnectionTest extends TestCase {
    @Override
    public void setUp () {
        super.setName( KafkaDataControl.getINSTANCE().getClass().getName() );
    }

    @Override
    public void tearDown () {
        /*
        closing connection to Kafka
        */
        KafkaDataControl.getINSTANCE().close();
    }

    public void testKafkaConnection () {
        assertNotNull( KafkaDataControl.getINSTANCE() );
    }
}

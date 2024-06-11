package com.example.tabletsproducerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.tabletsproducerservice.database.CassandraDataControl;

@SpringBootApplication
public class TabletsProducerServiceApplication {
    public static ApplicationContext context;

    public static void main( final String[] args ) {
        context = SpringApplication.run( TabletsProducerServiceApplication.class, args );
        CassandraDataControl.getInstance().register();
    }
}

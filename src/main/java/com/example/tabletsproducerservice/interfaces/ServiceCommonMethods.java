package com.example.tabletsproducerservice.interfaces;

public interface ServiceCommonMethods {
    default void close( final Throwable throwable ) {}

    void close();
}

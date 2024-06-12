package com.example.tabletsproducerservice.entity;

@lombok.Data
@lombok.Builder
public final class ApiResponseModel {
    private boolean success;
    private Status status;
    private Data data;
}

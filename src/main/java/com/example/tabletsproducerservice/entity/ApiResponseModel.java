package com.example.tabletsproducerservice.entity;

@lombok.Data
@lombok.Builder
public final class ApiResponseModel {
    private Boolean success;
    private Status status;
    private Data data;
}

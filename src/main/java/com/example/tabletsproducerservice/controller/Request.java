package com.example.tabletsproducerservice.controller;

import java.util.Date;

@lombok.Data
public final class Request {
    private Date endDate;
    private Date startDate;
    private String passportSeries;
}

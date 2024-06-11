package com.example.tabletsproducerservice.payload;

import java.util.List;

@lombok.Data
public final class ReqExchangeLocation {
    private String passportSeries;
    private List< ReqLocationExchange >  reqLocationExchanges;
}

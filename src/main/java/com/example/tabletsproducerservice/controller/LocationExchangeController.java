package com.example.tabletsproducerservice.controller;

import com.example.tabletsproducerservice.inspectors.DataValidateInspector;
import com.example.tabletsproducerservice.database.CassandraDataControl;
import com.example.tabletsproducerservice.payload.ReqExchangeLocation;
import com.example.tabletsproducerservice.entity.ApiResponseModel;
import com.example.tabletsproducerservice.entity.LastPosition;
import com.example.tabletsproducerservice.entity.PositionInfo;
import com.example.tabletsproducerservice.entity.Status;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Comparator;
import java.util.Map;

@RestController
public final class LocationExchangeController extends DataValidateInspector {
    @MessageMapping( value = "EXCHANGE" )
    public Mono< ApiResponseModel > exchange ( final ReqExchangeLocation reqExchangeLocation ) {
        reqExchangeLocation.setPassportSeries( CassandraDataControl
                .getInstance()
                .getDecode()
                .apply( reqExchangeLocation.getPassportSeries() ) ); // changing token to passport series

        CassandraDataControl
                .getInstance()
                .getAddNewReqExchangeLocation()
                .accept( reqExchangeLocation ); // saving to Cassandra

        return super.convert( ApiResponseModel
                .builder()
                .success( true )
                .status( Status
                        .builder()
                        .code( 200 )
                        .message( "success" )
                        .build() )
                .build() )
                .onErrorContinue( super::logging ); }

    @MessageMapping ( value = "GET_LAST_POSITIONS" )
    public Flux< LastPosition > getLastPositions ( final Map< String, Long > params ) { return CassandraDataControl
            .getInstance()
            .getGetLastPositions()
            .apply( params )
            .onErrorContinue( super::logging ); }

    @MessageMapping ( value = "CHECK_TOKEN" )
    public Mono< ApiResponseModel > checkToken ( final String token ) { return CassandraDataControl
            .getInstance()
            .getCheckToken()
            .apply( token )
            .onErrorContinue( super::logging ); }

    @MessageMapping ( value = "HISTORY" ) // uses for monitoring of all trackers lan
    public Flux< PositionInfo > history ( final Request request ) { return CassandraDataControl
            .getInstance()
            .getGetHistory()
            .apply( request )
            .sort( Comparator.comparing( PositionInfo::getPositionWasSavedDate ) )
            .onErrorContinue( super::logging ); }

    @MessageMapping ( value = "PING" )
    public Mono< Boolean > ping () { return super.convert( true ); }
}

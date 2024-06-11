package com.example.tabletsproducerservice.entity;

import com.example.tabletsproducerservice.inspectors.DataValidateInspector;
import com.example.tabletsproducerservice.constants.Status;
import com.datastax.driver.core.Row;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@lombok.Data
public final class LastPosition {
    private String icon;
    private String icon2;
    private String taskId;
    private Status status;
    private String carType;
    private String patrulName;
    private String policeType;
    private String carGosNumber;
    private String patrulpassportSeries;

    private UUID patrulUUID;
    private Date lastActiveDate; // current time when patrul sent signal lastly

    private Boolean active;
    private Boolean sosActivated; // показывает отправлял ли патрульный сос сигнал

    private Double lastLatitude;
    private Double lastLongitude;

    public LastPosition ( final Row row,
                          final Icons icons,
                          final Boolean sosActivated ) {
        this.setLastLatitude( row.getDouble( "latitude" ) );
        this.setLastLongitude( row.getDouble( "longitude" ) );

        this.setTaskId( row.getString( "taskId" ) );
        this.setPatrulUUID( row.getUUID( "uuid" ) );
        this.setStatus( Status.valueOf( row.getString( "status" ) ) );

        this.setIcon( icons.getIcon1() );
        this.setIcon2( icons.getIcon2() );
        this.setPatrulName( row.getString( "name" ) );
        this.setCarType( row.getString( "carType" ) );
        this.setPoliceType( row.getString( "policeType" ) );
        this.setCarGosNumber( row.getString( "carNumber" ) );
        this.setPatrulpassportSeries( row.getString( "passportNumber" ) );
        this.setLastActiveDate( row != null ? row.getTimestamp( "lastActiveDate" ) : null );

        this.setActive( DataValidateInspector
                .getInstance()
                .checkParam
                .test( this.getLastActiveDate() )
                && Math.abs( Duration.between(
                        Instant.now(),
                        this.getLastActiveDate().toInstant() ).toHours() ) < 24 );
        this.setSosActivated( sosActivated ); }
}

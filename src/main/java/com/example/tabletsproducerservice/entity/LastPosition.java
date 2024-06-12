package com.example.tabletsproducerservice.entity;

import com.example.tabletsproducerservice.inspectors.DataValidateInspector;
import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;
import com.example.tabletsproducerservice.database.CassandraDataControl;
import com.example.tabletsproducerservice.constants.CassandraTables;
import com.example.tabletsproducerservice.inspectors.Inspector;
import com.example.tabletsproducerservice.constants.Status;

import com.datastax.driver.core.Row;

import java.util.Date;
import java.util.UUID;

public final class LastPosition extends DataValidateInspector implements ObjectCommonMethods< LastPosition > {
    public void setIcon( final String icon ) {
        this.icon = icon;
    }

    public void setIcon2( final String icon2 ) {
        this.icon2 = icon2;
    }

    public void setTaskId( final String taskId ) {
        this.taskId = taskId;
    }

    public void setStatus( final Status status ) {
        this.status = status;
    }

    public void setCarType( final String carType ) {
        this.carType = carType;
    }

    public void setPatrulName( final String patrulName ) {
        this.patrulName = patrulName;
    }

    public void setPoliceType( final String policeType ) {
        this.policeType = policeType;
    }

    public void setCarGosNumber( final String carGosNumber ) {
        this.carGosNumber = carGosNumber;
    }

    public void setPatrulpassportSeries( final String patrulpassportSeries ) {
        this.patrulpassportSeries = patrulpassportSeries;
    }

    public void setPatrulUUID( final UUID patrulUUID ) {
        this.patrulUUID = patrulUUID;
    }

    public void setLastActiveDate( final Date lastActiveDate ) {
        this.lastActiveDate = lastActiveDate;
    }

    public void setActive( final boolean active ) {
        this.active = active;
    }

    public void setSosActivated( final boolean sosActivated ) {
        this.sosActivated = sosActivated;
    }

    public void setLastLatitude( final double lastLatitude ) {
        this.lastLatitude = lastLatitude;
    }

    public void setLastLongitude( final double lastLongitude ) {
        this.lastLongitude = lastLongitude;
    }

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

    private boolean active;
    private boolean sosActivated; // показывает отправлял ли патрульный сос сигнал

    private double lastLatitude;
    private double lastLongitude;

    public LastPosition () {}

    @Override
    public LastPosition generate(
            final Row row
    ) {
        super.checkAndSetParams(
                row,
                row1 -> {
                    this.setLastLatitude( row.getDouble( "latitude" ) );
                    this.setLastLongitude( row.getDouble( "longitude" ) );

                    final Icons icons = Inspector.icons.getOrDefault(
                            row.getString( "policeType" ),
                            new Icons().generate(
                                    CassandraDataControl
                                            .getInstance()
                                            .getRowFromTabletsKeyspace(
                                                    CassandraTables.POLICE_TYPE,
                                                    row.getString( "policeType" ),
                                                    "policeType"
                                            )
                            )
                    );

                    this.setTaskId( row.getString( "taskId" ) );
                    this.setPatrulUUID( row.getUUID( "uuid" ) );
                    this.setStatus( Status.valueOf( row.getString( "status" ) ) );

                    this.setIcon( icons.getIcon1() );
                    this.setIcon2( icons.getIcon2() );
                    this.setPatrulName( row.getString( "name" ) );
                    this.setCarType( row.getString( "carType" ) );
                    this.setPoliceType( row.getString( "policeType" ) );
                    this.setCarGosNumber( row.getString( "carNumber" ) );
                    this.setLastActiveDate( row.getTimestamp( "lastActiveDate" ) );
                    this.setPatrulpassportSeries( row.getString( "passportNumber" ) );

                    this.setActive(
                            super.objectIsNotNull( this.lastActiveDate )
                                    && super.getTimeDifference( this.lastActiveDate ) < 24
                    );

                    this.setSosActivated( super.checkSosTable( row.getUUID( "uuid" ) ) );
                }
        );

        return this;
    }
}

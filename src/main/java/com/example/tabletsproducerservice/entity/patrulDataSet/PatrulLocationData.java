package com.example.tabletsproducerservice.entity.patrulDataSet;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;

import com.example.tabletsproducerservice.payload.ReqLocationExchange;
import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;

public final class PatrulLocationData extends DataValidateInspector implements ObjectCommonMethods< PatrulLocationData > {
    public double getDistance() {
        return this.distance;
    }

    public void setDistance( final double distance ) {
        this.distance = distance;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude( final double latitude ) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude( final double longitude ) {
        this.longitude = longitude;
    }

    public double getLatitudeOfTask() {
        return this.latitudeOfTask;
    }

    public void setLatitudeOfTask( final double latitudeOfTask ) {
        this.latitudeOfTask = latitudeOfTask;
    }

    public double getLongitudeOfTask() {
        return this.longitudeOfTask;
    }

    public void setLongitudeOfTask( final double longitudeOfTask ) {
        this.longitudeOfTask = longitudeOfTask;
    }

    private double distance;
    // текущее местоположение патрульного по Х
    private double latitude;
    // текущее местоположение патрульного по Y
    private double longitude;
    // локация заданной задачи по Х
    private double latitudeOfTask;
    // локация заданной задачи по Y
    private double longitudeOfTask;

    public static PatrulLocationData empty () {
        return new PatrulLocationData();
    }

    private PatrulLocationData () {}

    public void update ( final ReqLocationExchange reqLocationExchange ) {
        this.setLongitude( reqLocationExchange.getLongitude() );
        this.setLatitude( reqLocationExchange.getLatitude() );
    }

    @Override
    public PatrulLocationData generate( final Row row ) {
        this.setLongitudeOfTask( row.getDouble( "longitudeOfTask" ) );
        this.setLatitudeOfTask( row.getDouble( "latitudeOfTask" ) );
        this.setLongitude( row.getDouble( "longitude" ) );
        this.setLatitude( row.getDouble( "latitude" ) );
        this.setDistance( row.getDouble( "distance" ) );

        return this;
    }

    @Override
    public PatrulLocationData generate( final UDTValue udtValue ) {
        super.checkAndSetParams(
                udtValue,
                udtValue1 -> {
                    this.setDistance( udtValue.getDouble( "distance" ) );
                    this.setLatitude( udtValue.getDouble( "latitude" ) );
                    this.setLongitude( udtValue.getDouble( "longitude" ) );
                    this.setLatitudeOfTask( udtValue.getDouble( "latitudeOfTask" ) );
                    this.setLongitudeOfTask( udtValue.getDouble( "longitudeOfTask" ) );
                }
        );

        return this;
    }

    @Override
    public UDTValue fillUdtByEntityParams( final UDTValue udtValue ) {
        return udtValue
                .setDouble( "distance", this.getDistance() )
                .setDouble( "latitude", this.getLatitude() )
                .setDouble( "longitude", this.getLongitude() );
    }
}

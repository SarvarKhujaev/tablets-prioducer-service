package com.example.tabletsproducerservice.entity.patrulDataSet;

import com.datastax.driver.core.Row;
import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;

public final class PatrulMobileAppInfo
        extends DataValidateInspector
        implements ObjectCommonMethods< PatrulMobileAppInfo > {
    public void setPhoneNumber ( final String phoneNumber ) {
        this.phoneNumber = phoneNumber;
    }

    public void setSimCardNumber ( final String simCardNumber ) {
        this.simCardNumber = simCardNumber;
    }

    public byte getBatteryLevel() {
        return this.batteryLevel;
    }

    public void setBatteryLevel ( final int batteryLevel ) {
        this.batteryLevel = (byte) batteryLevel;
    }

    private String phoneNumber;
    private String simCardNumber;
    private byte batteryLevel;

    public static PatrulMobileAppInfo empty() {
        return new PatrulMobileAppInfo();
    }

    private PatrulMobileAppInfo () {}

    @Override
    public PatrulMobileAppInfo generate( final Row row ) {
        this.setSimCardNumber( row.getString( "simCardNumber" ) );
        this.setBatteryLevel( row.getByte( "batteryLevel" ) );
        this.setPhoneNumber( row.getString( "phoneNumber" ) );

        return this;
    }
}

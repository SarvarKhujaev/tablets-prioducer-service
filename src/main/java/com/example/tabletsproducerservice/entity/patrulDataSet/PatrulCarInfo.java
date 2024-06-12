package com.example.tabletsproducerservice.entity.patrulDataSet;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;

import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;

public final class PatrulCarInfo extends DataValidateInspector implements ObjectCommonMethods< PatrulCarInfo > {
    public String getCarType() {
        return this.carType;
    }

    public void setCarType( final String carType ) {
        this.carType = carType;
    }

    public String getCarNumber() {
        return this.carNumber;
    }

    public void setCarNumber( final String carNumber ) {
        this.carNumber = carNumber;
    }

    private String carType; // модель машины
    private String carNumber;

    public static PatrulCarInfo empty () {
        return new PatrulCarInfo();
    }

    private PatrulCarInfo () {}

    @Override
    public PatrulCarInfo generate( final Row row ) {
        this.setCarType( row.getString( "carType" ) );
        this.setCarNumber( row.getString( "carNumber" ) );
        return this;
    }

    @Override
    public PatrulCarInfo generate( final UDTValue udtValue ) {
        super.checkAndSetParams(
                udtValue,
                udtValue1 -> {
                    this.setCarType( udtValue.getString( "carType" ) );
                    this.setCarNumber( udtValue.getString( "carNumber" ) );
                }
        );
        return this;
    }

    @Override
    public UDTValue fillUdtByEntityParams( final UDTValue udtValue ) {
        return udtValue
                .setString( "carType", this.getCarType() )
                .setString( "carNumber", this.getCarNumber() );
    }
}

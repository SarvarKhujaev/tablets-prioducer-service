package com.example.tabletsproducerservice.entity.patrulDataSet;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;

import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;

public final class PatrulAuthData extends DataValidateInspector implements ObjectCommonMethods< PatrulAuthData > {
    public void setLogin( final String login ) {
        this.login = login;
    }

    public void setPassword( final String password ) {
        this.password = password;
    }

    private String login;
    private String password;

    public static PatrulAuthData empty () {
        return new PatrulAuthData();
    }

    private PatrulAuthData () {}

    @Override
    public PatrulAuthData generate( final Row row ) {
        this.setPassword( row.getString( "password" ) );
        this.setLogin( row.getString( "login" ) );

        return this;
    }

    @Override
    public PatrulAuthData generate( final UDTValue udtValue ) {
        super.checkAndSetParams(
                udtValue,
                udtValue1 -> {
                    this.setLogin( udtValue.getString( "login" ) );
                    this.setPassword( udtValue.getString( "password" ) );
                }
        );

        return this;
    }
}

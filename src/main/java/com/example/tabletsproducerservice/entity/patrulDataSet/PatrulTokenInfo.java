package com.example.tabletsproducerservice.entity.patrulDataSet;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;

import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;

public final class PatrulTokenInfo extends DataValidateInspector implements ObjectCommonMethods< PatrulTokenInfo > {
    public String getSpecialToken() {
        return this.specialToken;
    }

    public void setSpecialToken( final String specialToken ) {
        this.specialToken = specialToken;
    }

    public String getTokenForLogin() {
        return this.tokenForLogin;
    }

    public void setTokenForLogin( final String tokenForLogin) {
        this.tokenForLogin = tokenForLogin;
    }

    private String specialToken;
    private String tokenForLogin;

    public PatrulTokenInfo setInitialValues () {
        this.setTokenForLogin( "" );
        this.setSpecialToken( "" );
        return this;
    }

    private PatrulTokenInfo () {}

    public static PatrulTokenInfo empty () {
        return new PatrulTokenInfo();
    }

    @Override
    public PatrulTokenInfo generate ( final Row row ) {
        this.setTokenForLogin( row.getString( "tokenForLogin" ) );
        this.setSpecialToken( row.getString( "specialToken" ) );

        return this;
    }

    @Override
    public PatrulTokenInfo generate ( final UDTValue udtValue ) {
        super.checkAndSetParams(
                udtValue,
                udtValue1 -> {
                    this.setSpecialToken( udtValue.getString( "specialToken" ) );
                    this.setTokenForLogin( udtValue.getString( "tokenForLogin" ) );
                }
        );

        return this;
    }

    @Override
    public UDTValue fillUdtByEntityParams( final UDTValue udtValue ) {
        return udtValue
                .setString( "specialToken", this.getSpecialToken() )
                .setString( "tokenForLogin", this.getTokenForLogin() );
    }
}

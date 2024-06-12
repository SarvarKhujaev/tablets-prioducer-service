package com.example.tabletsproducerservice.entity;

import com.datastax.driver.core.Row;
import com.example.tabletsproducerservice.constants.Errors;
import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;

public final class Icons extends DataValidateInspector implements ObjectCommonMethods< Icons > {
    public String getIcon1() {
        return this.icon1;
    }

    public void setIcon1( final String icon1 ) {
        this.icon1 = icon1;
    }

    public String getIcon2() {
        return this.icon2;
    }

    public void setIcon2( final String icon2 ) {
        this.icon2 = icon2;
    }

    private String icon1 = Errors.DATA_NOT_FOUND.translate( "ru" );
    private String icon2 = Errors.DATA_NOT_FOUND.translate( "ru" );

    public Icons () {}

    private Icons ( final Row row ) {
        super.checkAndSetParams(
                row,
                row1 -> {
                    this.setIcon1( row.getString( "icon" ) );
                    this.setIcon2( row.getString( "icon2" ) ); }
        );
    }

    @Override
    public Icons generate(
            final Row row
    ) {
        return new Icons( row );
    }
}

package com.example.tabletsproducerservice.entity;

import com.datastax.driver.core.Row;
import java.util.Optional;

@lombok.Data
public final class Icons {
    private String icon1 = "not found";
    private String icon2 = "not found";

    public Icons ( final Row row ) { Optional.ofNullable( row ).ifPresent( row1 -> {
            this.setIcon1( row.getString( "icon" ) );
            this.setIcon2( row.getString( "icon2" ) ); } ); }
}

package com.example.tabletsproducerservice.entity;

import com.example.tabletsproducerservice.inspectors.DataValidateInspector;
import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;

import com.datastax.driver.core.Row;
import java.util.Date;

public final class PositionInfo extends DataValidateInspector implements ObjectCommonMethods< PositionInfo > {
    public void setLat( final double lat ) {
        this.lat = lat;
    }

    public void setLng( final double lng ) {
        this.lng = lng;
    }

    public void setSpeed( final int speed ) {
        this.speed = speed;
    }

    public void setAddress( final String address ) {
        this.address = address;
    }

    public void setPositionWasSavedDate( final Date positionWasSavedDate ) {
        this.positionWasSavedDate = positionWasSavedDate;
    }

    public Date getPositionWasSavedDate() {
        return this.positionWasSavedDate;
    }

    private double lat;

    private double lng;
    private int speed; // скорось передвижения патрулього

    private String address;

    private Date positionWasSavedDate;

    public PositionInfo () {}

    @Override
    public PositionInfo generate(
            final Row row
    ) {
        super.checkAndSetParams(
                row,
                row1 -> {
                    this.setSpeed( row.getInt( "speed" ) );
                    this.setLat( row.getDouble( "latitude" ) );
                    this.setLng( row.getDouble( "longitude" ) );
                    this.setAddress( row.getString( "address" ) );
                    this.setPositionWasSavedDate( row.getTimestamp( "date" ) );
                }
        );

        return this;
    }
}

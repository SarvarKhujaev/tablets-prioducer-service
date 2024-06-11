package com.example.tabletsproducerservice.entity;

import com.datastax.driver.core.Row;
import java.util.Optional;
import java.util.Date;

@lombok.Data // used in case of historical request for some time duration
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class PositionInfo {
    private Double lat;
    private Double lng;

    private Integer speed; // скорось передвижения патрулього
    private String address;
    private Date positionWasSavedDate;

    public PositionInfo ( final Row row ) { Optional.ofNullable( row ).ifPresent( row1 -> {
            this.setSpeed( row.getInt( "speed" ) );
            this.setLat( row.getDouble( "latitude" ) );
            this.setLng( row.getDouble( "longitude" ) );
            this.setAddress( row.getString( "address" ) );
            this.setPositionWasSavedDate( row.getTimestamp( "date" ) ); } ); }
}

package com.example.tabletsproducerservice.payload;

import com.example.tabletsproducerservice.interfaces.EntityToCassandraConverter;
import com.example.tabletsproducerservice.inspectors.DataValidateInspector;
import com.example.tabletsproducerservice.entity.patrulDataSet.Patrul;
import com.example.tabletsproducerservice.constants.CassandraCommands;
import com.example.tabletsproducerservice.constants.CassandraTables;
import com.example.tabletsproducerservice.constants.TaskTypes;
import com.example.tabletsproducerservice.constants.Status;
import com.example.tabletsproducerservice.entity.Icons;

import java.text.MessageFormat;
import java.util.UUID;

public final class ReqLocationExchange extends DataValidateInspector implements EntityToCassandraConverter {
    public void setStatus( final Status status ) {
        this.status = status;
    }

    public void setPatrulUUID( final UUID patrulUUID ) {
        this.patrulUUID = patrulUUID;
    }

    public UUID getPatrulUUID() {
        return this.patrulUUID;
    }

    public void setSosStatus( final boolean sosStatus ) {
        this.sosStatus = sosStatus;
    }

    public void setTaskTypes( final TaskTypes taskTypes ) {
        this.taskTypes = taskTypes;
    }

    public void setCard( final String card ) {
        this.card = card;
    }

    public void setIcon( final String icon ) {
        this.icon = icon;
    }

    public void setIcon2( final String icon2 ) {
        this.icon2 = icon2;
    }

    public void setPatrulName( final String patrulName ) {
        this.patrulName = patrulName;
    }

    public void setPoliceType( final String policeType ) {
        this.policeType = policeType;
    }

    public String getPatrulPassportSeries() {
        return this.patrulPassportSeries;
    }

    public void setPatrulPassportSeries( final String patrulPassportSeries ) {
        this.patrulPassportSeries = patrulPassportSeries;
    }

    public void setLatitudeOfTask( final double latitudeOfTask ) {
        this.latitudeOfTask = latitudeOfTask;
    }

    public void setLongitudeOfTask( final double longitudeOfTask ) {
        this.longitudeOfTask = longitudeOfTask;
    }

    public void setRegionId( final long regionId ) {
        this.regionId = regionId;
    }

    public void setMahallaId( final long mahallaId ) {
        this.mahallaId = mahallaId;
    }

    public void setDistrictId( final long districtId ) {
        this.districtId = districtId;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate( final Long date ) {
        this.date = date;
    }

    public int getSpeed() {
        return this.speed;
    }

    public int getBatteryLevel() {
        return this.batteryLevel;
    }

    // all Patrul params
    private Status status;

    private UUID patrulUUID;
    private boolean sosStatus; // показывает послал ли патрульный сос сигнал
    private TaskTypes taskTypes;

    private String card;
    private String icon;
    private String icon2;
    private String patrulName;
    private String policeType;
    private String patrulPassportSeries;

    private double latitudeOfTask;
    private double longitudeOfTask;

    private long regionId;
    private long mahallaId;
    private long districtId; // choosing from dictionary

    // original values from each Tablet
    private double longitude;
    private double latitude;

    private long date;
    private int speed; // скоросmь передвижения патрульного
    private int batteryLevel;

    public ReqLocationExchange update (
            final Patrul patrul,
            final Icons icons
    ) {
        this.setIcon( icons.getIcon1() );
        this.setIcon2( icons.getIcon2() );
        this.setSosStatus( super.checkSosTable( patrul.getUuid() ) );

        this.setPatrulUUID( patrul.getUuid() );
        this.setPoliceType( patrul.getPoliceType() );

        this.setPatrulName( patrul.getPatrulFIOData().getName() );
        this.setPatrulPassportSeries( patrul.getPassportNumber() );

        this.setCard( patrul.getPatrulTaskInfo().getTaskId() );
        this.setStatus( patrul.getPatrulTaskInfo().getStatus() );
        this.setTaskTypes( patrul.getPatrulTaskInfo().getTaskTypes() );

        this.setRegionId( patrul.getPatrulRegionData().getRegionId() );
        this.setMahallaId( patrul.getPatrulRegionData().getMahallaId() );
        this.setDistrictId( patrul.getPatrulRegionData().getDistrictId() );

        this.setLatitudeOfTask( patrul.getPatrulLocationData().getLatitudeOfTask() );
        this.setLongitudeOfTask( patrul.getPatrulLocationData().getLongitudeOfTask() );

        return this;
    }

    @Override
    public String getEntityUpdateCommand () {
        super.logging(
                String.join(
                        "",
                        "Cassandra got ",
                        " patrulUUID: ",
                        this.getPatrulUUID().toString(),
                        " passport: ",
                        this.getPatrulPassportSeries(),
                        " at: ",
                        super.getDate( 0L ).toString(),
                        " location longitude: ",
                        String.valueOf( this.getLongitude() ),
                        " location latitude: ",
                        String.valueOf( this.getLatitude() )
                )
        );

        return MessageFormat.format(
                """
                    {0} {1}.{2}
                    ( userId, date, latitude, longitude, speed, address )
                    VALUES ( {3}, {4}, {5}, {6}, {7}, '' );
                    """,
                CassandraCommands.INSERT_INTO,

                CassandraTables.GPSTABLETS ,
                CassandraTables.TABLETS_LOCATION_TABLE,

                super.joinWithAstrix( this.getPatrulPassportSeries() ),

                super.joinWithAstrix( super.getDate( this.getDate() ) ),

                this.getLatitude(),
                this.getLongitude(),
                this.getSpeed()
        );
    }
}

package com.example.tabletsproducerservice.entity.patrulDataSet;

import com.example.tabletsproducerservice.interfaces.EntityToCassandraConverter;
import com.example.tabletsproducerservice.interfaces.ObjectCommonMethods;
import com.example.tabletsproducerservice.inspectors.CassandraConverter;
import com.example.tabletsproducerservice.constants.CassandraCommands;
import com.example.tabletsproducerservice.payload.ReqLocationExchange;
import com.example.tabletsproducerservice.constants.CassandraTables;

import com.datastax.driver.core.Row;
import java.text.MessageFormat;
import java.util.UUID;

public final class Patrul
        extends CassandraConverter
        implements ObjectCommonMethods< Patrul >, EntityToCassandraConverter {
    public UUID getUuid () {
        return this.uuid;
    }

    public void setUuid ( final UUID uuid ) {
        this.uuid = uuid;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPoliceType() {
        return this.policeType;
    }

    public String getPassportNumber() {
        return this.passportNumber;
    }

    public PatrulFIOData getPatrulFIOData() {
        return this.patrulFIOData;
    }

    public void setPatrulFIOData( final PatrulFIOData patrulFIOData ) {
        this.patrulFIOData = patrulFIOData;
    }

    public PatrulDateData getPatrulDateData() {
        return this.patrulDateData;
    }

    public void setPatrulDateData( final PatrulDateData patrulDateData ) {
        this.patrulDateData = patrulDateData;
    }

    public PatrulTaskInfo getPatrulTaskInfo() {
        return this.patrulTaskInfo;
    }

    public void setPatrulTaskInfo( final PatrulTaskInfo patrulTaskInfo ) {
        this.patrulTaskInfo = patrulTaskInfo;
    }

    public void setPatrulTokenInfo( final PatrulTokenInfo patrulTokenInfo ) {
        this.patrulTokenInfo = patrulTokenInfo;
    }

    public PatrulRegionData getPatrulRegionData() {
        return this.patrulRegionData;
    }

    public void setPatrulRegionData( final PatrulRegionData patrulRegionData ) {
        this.patrulRegionData = patrulRegionData;
    }

    public PatrulLocationData getPatrulLocationData() {
        return this.patrulLocationData;
    }

    public void setPatrulLocationData( final PatrulLocationData patrulLocationData ) {
        this.patrulLocationData = patrulLocationData;
    }

    public PatrulMobileAppInfo getPatrulMobileAppInfo() {
        return this.patrulMobileAppInfo;
    }

    public void setPatrulMobileAppInfo(
            final PatrulMobileAppInfo patrulMobileAppInfo
    ) {
        this.patrulMobileAppInfo = patrulMobileAppInfo;
    }

    // уникальное ID патрульного
    private UUID uuid;

    private String email;
    private String policeType;
    private String passportNumber;

    private PatrulFIOData patrulFIOData;
    private PatrulDateData patrulDateData;
    private PatrulTaskInfo patrulTaskInfo;
    private PatrulTokenInfo patrulTokenInfo;
    private PatrulRegionData patrulRegionData;
    private PatrulLocationData patrulLocationData;
    private PatrulMobileAppInfo patrulMobileAppInfo;

    public ReqLocationExchange update ( final ReqLocationExchange reqLocationExchange ) {
        this.getPatrulMobileAppInfo().setBatteryLevel( reqLocationExchange.getBatteryLevel() );
        this.getPatrulLocationData().update( reqLocationExchange );
        return reqLocationExchange;
    }

    public static Patrul empty () {
        return new Patrul();
    }

    private Patrul () {}

    @Override
    public Patrul generate( final Row row ) {
        super.checkAndSetParams(
                row,
                row1 -> {
                    this.setUuid( row.getUUID( "uuid" ) );

                    this.setPoliceType( row.getString( "policeType" ) );
                    this.setPassportNumber( row.getString( "passportNumber" ) );

                    this.setPatrulFIOData( PatrulFIOData.empty().generate( row.getUDTValue( "patrulFIOData" ) ) );
                    this.setPatrulDateData( PatrulDateData.empty().generate( row.getUDTValue( "patrulDateData" ) ) );
                    this.setPatrulTaskInfo( PatrulTaskInfo.empty().generate( row.getUDTValue( "patrulTaskInfo" ) ) );
                    this.setPatrulTokenInfo( PatrulTokenInfo.empty().generate( row.getUDTValue( "patrulTokenInfo" ) ) );
                    this.setPatrulRegionData( PatrulRegionData.empty().generate( row.getUDTValue( "patrulRegionData" ) ) );
                    this.setPatrulLocationData( PatrulLocationData.empty().generate( row.getUDTValue( "patrulLocationData" ) ) );
                    this.setPatrulMobileAppInfo( PatrulMobileAppInfo.empty().generate( row.getUDTValue( "patrulMobileAppInfo" ) ) );
                }
        );

        return this;
    }

    @Override
    public String getEntityUpdateCommand() {
        return MessageFormat.format(
                """
                {0} {1}.{2}
                SET latitude = {3}
                    longitude = {4}
                    lastActiveDate = {5}
                    batteryLevel = {6}
                WHERE uuid = {7};
                """,
                CassandraCommands.UPDATE,

                CassandraTables.TABLETS,
                CassandraTables.PATRULS,

                this.getPatrulLocationData().getLatitude(),
                this.getPatrulLocationData().getLatitude(),

                this.getPatrulDateData().getLastActiveDate(),
                this.getPatrulMobileAppInfo().getBatteryLevel(),

                this.getUuid()
        );
    }
}

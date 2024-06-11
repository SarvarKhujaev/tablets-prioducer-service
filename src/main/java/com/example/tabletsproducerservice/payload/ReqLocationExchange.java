package com.example.tabletsproducerservice.payload;

import com.example.tabletsproducerservice.constants.TaskTypes;
import com.example.tabletsproducerservice.constants.Status;
import com.example.tabletsproducerservice.entity.Patrul;
import com.example.tabletsproducerservice.entity.Icons;
import java.util.UUID;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class ReqLocationExchange {
    // all Patrul params
    private Status status;
    private UUID patrulUUID;
    private Boolean sosStatus; // показывает послал ли патрульный сос сигнал
    private TaskTypes taskTypes;

    private String card;
    private String icon;
    private String icon2;
    private String patrulName;
    private String policeType;
    private String patrulPassportSeries;

    private Double latitudeOfTask;
    private Double longitudeOfTask;

    private Long regionId;
    private Long mahallaId;
    private Long districtId; // choosing from dictionary

    // original values from each Tablet
    private Double longitude;
    private Double latitude;

    private Long date;
    private Integer speed; // скоросmь передвижения патрульного
    private Integer batteryLevel;

    public void update ( final Patrul patrul, final Boolean sosStatus, final Icons icons ) {
        this.setSosStatus( sosStatus );
        this.setIcon( icons.getIcon1() );
        this.setIcon2( icons.getIcon2() );

        this.setRegionId( patrul.getRegionId() );
        this.setMahallaId( patrul.getMahallaId() );
        this.setDistrictId( patrul.getDistrictId() );

        this.setCard( patrul.getTaskId() );
        this.setStatus( patrul. getStatus() );
        this.setPatrulUUID( patrul.getUuid() );
        this.setPatrulName( patrul.getName() );
        this.setTaskTypes( patrul.getTaskTypes() );
        this.setPoliceType( patrul.getPoliceType() );
        this.setLatitudeOfTask( patrul.getLatitudeOfTask() );
        this.setLongitudeOfTask( patrul.getLongitudeOfTask() );
        this.setPatrulPassportSeries( patrul.getPassportNumber() ); }
}

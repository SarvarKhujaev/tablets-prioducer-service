package com.example.tabletsproducerservice.inspectors;

import java.util.Map;
import java.util.UUID;
import java.util.Objects;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiFunction;

import com.datastax.driver.core.Row;
import com.example.tabletsproducerservice.controller.Request;
import com.example.tabletsproducerservice.constants.CassandraTables;
import com.example.tabletsproducerservice.database.CassandraDataControl;

public class DataValidateInspector extends LogInspector {
    private final static DataValidateInspector INSPECTOR = new DataValidateInspector();

    public static DataValidateInspector getInstance () { return INSPECTOR; }

    public final Predicate< Object > checkParam = Objects::nonNull;

    protected final Function< Integer, Integer > checkDifference = integer -> integer > 0 && integer < 100 ? integer : 10;

    protected final Predicate< Row > checkLocation = row -> row.getDouble( "latitude" ) > 0 && row.getDouble( "longitude" ) > 0;

    protected final Predicate< Request > checkRequest = request ->
            this.checkParam.test( request )
            && this.checkParam.test( request.getEndDate() )
            && this.checkParam.test( request.getStartDate() );

    protected final Predicate< UUID > checkSosTable = patrulUUID -> CassandraDataControl
            .getInstance()
            .getSession()
            .execute( "SELECT * FROM "
                    + CassandraTables.TABLETS + "."
                    + CassandraTables.SOS_TABLE
                    + " WHERE patrulUUID = "
                    + patrulUUID + ";" ).one() != null;

    protected final BiFunction< Row, Map< String, Long >, Boolean > checkParams = ( row, params ) -> switch ( params.size() ) {
            case 1 -> params.containsKey( "viloyat" ) && Objects.equals( row.getLong( "regionId" ), params.get( "viloyat" ) );
            case 2 -> ( params.containsKey( "viloyat" ) && Objects.equals( row.getLong( "regionId" ), params.get( "viloyat" ) ) )
                    && ( params.containsKey( "tuman" ) && Objects.equals( row.getLong( "districtId" ), params.get( "tuman" ) ) );
            default -> ( params.containsKey( "tuman" ) && Objects.equals( row.getLong( "districtId" ), params.get( "tuman" ) ) )
                    && ( params.containsKey( "viloyat" ) && Objects.equals( row.getLong( "regionId" ), params.get( "viloyat" ) ) )
                    && ( params.containsKey( "mahalla" ) && Objects.equals( row.getLong( "mahallaId" ), params.get( "mahalla" ) ) ); };
}
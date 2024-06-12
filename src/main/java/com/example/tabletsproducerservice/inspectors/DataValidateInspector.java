package com.example.tabletsproducerservice.inspectors;

import java.util.Map;
import java.util.UUID;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.datastax.driver.core.Row;

import com.example.tabletsproducerservice.controller.Request;
import com.example.tabletsproducerservice.constants.CassandraTables;
import com.example.tabletsproducerservice.database.CassandraDataControl;
import com.example.tabletsproducerservice.TabletsProducerServiceApplication;

public class DataValidateInspector extends LogInspector {
    protected DataValidateInspector () {}

    protected final synchronized boolean objectIsNotNull (
            final Object o
    ) {
        return o != null;
    }

    protected final synchronized int checkDifference ( final int integer ) {
        return integer > 0 && integer < 100 ? integer : 10;
    }

    protected final synchronized boolean checkLocation ( final Row row ) {
        return row.getDouble( "latitude" ) > 0 && row.getDouble( "longitude" ) > 0;
    }

    protected final synchronized boolean checkRequest (
            final Request request
    ) {
        return this.objectIsNotNull( request )
                && this.objectIsNotNull( request.getEndDate() )
                && this.objectIsNotNull( request.getStartDate() );
    }

    protected final synchronized boolean checkSosTable (
            final UUID patrulUUID
    ) {
        return CassandraDataControl
                .getInstance()
                .getSession()
                .execute( "SELECT * FROM "
                        + CassandraTables.TABLETS + "."
                        + CassandraTables.SOS_TABLE
                        + " WHERE patrulUUID = "
                        + patrulUUID + ";" ).one() != null;
    }

    protected final synchronized boolean checkParams (
            final Row row,
            final Map< String, Long > params
    ) {
        return switch ( params.size() ) {
            case 1 -> params.containsKey( "viloyat" ) && Objects.equals( row.getLong( "regionId" ), params.get( "viloyat" ) );
            case 2 -> ( params.containsKey( "viloyat" ) && Objects.equals( row.getLong( "regionId" ), params.get( "viloyat" ) ) )
                    && ( params.containsKey( "tuman" ) && Objects.equals( row.getLong( "districtId" ), params.get( "tuman" ) ) );
            default -> ( params.containsKey( "tuman" ) && Objects.equals( row.getLong( "districtId" ), params.get( "tuman" ) ) )
                    && ( params.containsKey( "viloyat" ) && Objects.equals( row.getLong( "regionId" ), params.get( "viloyat" ) ) )
                    && ( params.containsKey( "mahalla" ) && Objects.equals( row.getLong( "mahallaId" ), params.get( "mahalla" ) ) );
        };
    }

    /*
    принимает запись из БД
    проверяет что запись не пуста
    и заполняет параметры объекта по заданной логике
    */
    protected final synchronized <T> void checkAndSetParams (
            final T object,
            final Consumer< T > customConsumer
    ) {
        Optional.ofNullable( object ).ifPresent( customConsumer );
    }

    /*
        получает в параметрах название параметра из файла application.yaml
        проверят что context внутри main класса GpsTabletsServiceApplication  инициализирован
        и среди параметров сервиса сузествует переданный параметр
        */
    protected final synchronized <T> T checkContextOrReturnDefaultValue (
            final String paramName,
            final T defaultValue
    ) {
        return this.objectIsNotNull( TabletsProducerServiceApplication.context )
                && this.objectIsNotNull(
                TabletsProducerServiceApplication
                        .context
                        .getEnvironment()
                        .getProperty( paramName )
        )
                ? (T) TabletsProducerServiceApplication
                .context
                .getEnvironment()
                .getProperty( paramName )
                : defaultValue;
    }
}
package com.example.tabletsproducerservice.inspectors;

import com.example.tabletsproducerservice.constants.CassandraDataTypes;

import java.util.function.Function;
import java.util.stream.Stream;

import java.lang.reflect.Field;
import java.util.*;

public class CassandraConverter extends TokenInspector {
    protected CassandraConverter() {}

    /*
    для экземпляра Java класса конвертирует каждый его параметр,
    в Cassandra подобный тип данных
    */
    private final Function< Class<?>, CassandraDataTypes > getCorrectDataType = type -> {
        if ( type.equals( String.class ) || type.isEnum() ) {
            return CassandraDataTypes.TEXT;
        }
        else if ( type.equals( UUID.class ) ) {
            return CassandraDataTypes.UUID;
        }
        else if ( type.equals( long.class ) ) {
            return CassandraDataTypes.BIGINT;
        }
        else if ( type.equals( int.class ) ) {
            return CassandraDataTypes.INT;
        }
        else if ( type.equals( double.class ) ) {
            return CassandraDataTypes.DOUBLE;
        }
        else if ( type.equals( Date.class ) ) {
            return CassandraDataTypes.TIMESTAMP;
        }
        else if ( type.equals( byte.class ) ) {
            return CassandraDataTypes.TINYINT;
        }
        else {
            return CassandraDataTypes.BOOLEAN;
        }
    };

    /*
    для экземпляра Java класса конвертирует каждый его параметр,
    в Cassandra подобный тип данных
    */
    protected final Function< Class<?>, String > convertClassToCassandra = object -> {
            final StringBuilder result = super.newStringBuilder( "( " );

            this.getFields.apply( object )
                    .filter( field -> field.getType().equals( String.class )
                            ^ field.getType().equals( int.class )
                            ^ field.getType().equals( double.class )
                            ^ field.getType().equals( byte.class )
                            ^ field.getType().equals( UUID.class )
                            ^ field.getType().equals( long.class )
                            ^ field.getType().equals( Date.class )
                            ^ field.getType().isEnum()
                            ^ field.getType().equals( boolean.class ) )
                    .forEach( field -> result
                            .append( field.getName() )
                            .append( " " )
                            .append( this.getCorrectDataType.apply( field.getType() ) )
                            .append( ", " ) );

            return result.substring( 0, result.toString().length() - 2 );
    };

    /*
    принимает объект класса и возвращает список всех его аттрибутов
    */
    private final Function< Class<?>, Stream< Field > > getFields = object -> Arrays.stream(
            object.getDeclaredFields()
    ).toList().stream();
}

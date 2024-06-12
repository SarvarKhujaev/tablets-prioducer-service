package com.example.tabletsproducerservice.interfaces;

import com.example.tabletsproducerservice.constants.CassandraTables;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Row;

import java.util.List;

/*
хранит все стандартные методы для сервисов работающих с БД
*/
public interface DatabaseCommonMethods {
    /*
    возвращает одно конкретное значение из БД
    */
    Row getRowFromTabletsKeyspace (
            // название таблицы внутри Tablets
            final CassandraTables cassandraTableName,
            // название колонки
            final String columnName,
            // параметр по которому введется поиск
            final String paramName
    );

    /*
    возвращает список значений из БД
    */
    List< Row > getListOfEntities (
            // название таблицы внутри Tablets
            final CassandraTables cassandraTableName
    );

    Session getSession();
}

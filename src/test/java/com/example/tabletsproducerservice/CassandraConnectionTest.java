package com.example.tabletsproducerservice;

import com.example.tabletsproducerservice.database.CassandraDataControl;
import com.example.tabletsproducerservice.constants.CassandraTables;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.core.Row;

import junit.framework.TestCase;
import java.text.MessageFormat;

import java.util.List;
import java.util.UUID;

/*
проверяем соединение с БД Cassandra
*/
public final class CassandraConnectionTest extends TestCase {
    private final UUID uuid = UUIDs.timeBased();

    @Override
    public void setUp () {
        super.setName( CassandraDataControl.class.getName() );

        /*
        Launch the Database connection
        */
        CassandraDataControl.getInstance();
    }

    @Override
    public void tearDown () {
        CassandraDataControl.getInstance().close();
    }

    public void testGetListOfEntities () {
        final List< Row > rows = CassandraDataControl
                .getInstance()
                .getListOfEntities(
                        CassandraTables.PATRULS
                );

        assertNotNull( rows );
        assertTrue( rows.isEmpty() );
    }

    /*
    checks and make sure that Cassandra Cluster was established
    and session from Cluster was initiated
    */
    public void testConnectionWasEstablished () {
        assertNotNull( CassandraDataControl.getInstance() );
        assertNotNull( CassandraDataControl.getInstance().getCluster() );
        assertNotNull( CassandraDataControl.getInstance().getSession() );
        assertFalse( CassandraDataControl.getInstance().getCluster().isClosed() );
        assertFalse( CassandraDataControl.getInstance().getSession().isClosed() );

        Row row = CassandraDataControl.getInstance().getRowFromTabletsKeyspace(
                CassandraTables.PATRULS,
                "uuid",
                this.uuid.toString()
        );

        assertNotNull( row );
        assertFalse( row.getString( "passportNumber" ).isBlank() );

        row = CassandraDataControl.getInstance().getSession().execute(
                MessageFormat.format(
                        """
                           SELECT COUNT(*)
                           FROM {0}.{1};
                           """,
                        CassandraTables.TABLETS,
                        CassandraTables.PATRULS
                )
        ).one();

        assertNotNull( row );
        assertTrue( row.getLong( "count" ) > 2 );
    }
}

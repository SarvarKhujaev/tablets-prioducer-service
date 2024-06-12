package com.example.tabletsproducerservice.inspectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogInspector extends Inspector {
    protected LogInspector () {}

    private final Logger LOGGER = LogManager.getLogger( "LOGGER_WITH_JSON_LAYOUT" );

    private Logger getLOGGER() { return this.LOGGER; }

    protected final synchronized void logging ( final String message ) {
        this.getLOGGER().info( message );
    }

    protected final synchronized void logging ( final Throwable error ) {
        this.getLOGGER().error( "Error: " + error.getMessage() );
    }

    protected final synchronized void logging ( final Throwable error, final Object o ) {
        this.getLOGGER().error("Error: {} and reason: {}: ",
            error.getMessage(), o );
    }

    protected final synchronized void logging ( final Object o ) {
        this.getLOGGER().info( o.getClass().getName() + " was closed successfully at: " + super.newDate() );
    }
}

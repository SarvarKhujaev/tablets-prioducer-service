package com.example.tabletsproducerservice.inspectors;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class TimeInspector {
    protected TimeInspector() {}

    protected final static int DAY_IN_SECOND = 86400;

    protected final synchronized Date newDate () {
        return new Date();
    }

    protected final synchronized int getTimeDifference (
            final Date date,
            final Date date2
    ) {
        return (int) Math.abs( Duration.between( date.toInstant(), date2.toInstant() ).toDays() );
    }

    protected final synchronized long getTimeDifference (
            final Date date
    ) {
        return Math.abs(
                Duration.between(
                    Instant.now(),
                    date.toInstant()
                ).toHours()
        );
    }

    protected final synchronized Date getDate(
            final long value
    ) {
        return value > 0L ? new Date( value ) : new Date();
    }
}

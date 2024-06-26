package com.example.tabletsproducerservice.constants;

public enum CassandraTables {
    // tables for CRUD
    TABLETS,
    REPORT_FOR_CARD, POLYGON_FOR_PATRUl, TABLETS_USAGE_TABLE,
    CARS, LUSTRA, PATRULS, POLYGON, NOTIFICATION, PATRULS_LOGIN_TABLE, PATRULS_STATUS_TABLE,
    PATRUL_TYPE, POLICE_TYPE, CAMERA_LIST, POLYGON_TYPE, POLYGON_ENTITY, VIOLATION_LIST_TYPE,

    // tables for Tasks
    FACECAR, EVENTCAR, EVENTFACE, EVENTBODY, FACEPERSON, ACTIVE_TASK, CARTOTALDATA, SELFEMPLOYMENT, SOS_TABLE,

    // tables for ESCORT Entity
    ESCORT,
    COUNTRIES, TUPLE_OF_CAR, TUPLE_OF_ESCORT, POINTS_ENTITY, POLYGON_FOR_ESCORT,

    // tables for TRACKERS
    TRACKERS,
    TRACKERS_LOCATION_TABLE, TRACKERS_ID,

    // tables for TABLETS
    GPSTABLETS,
    TABLETS_LOCATION_TABLE,

    PATRUL_CAR_DATA,
    PATRUL_FIO_DATA,
    PATRUL_TASK_DATA,
    PATRUL_DATE_DATA,
    PATRUL_AUTH_DATA,
    PATRUL_TOKEN_DATA,
    PATRUL_REGION_DATA,
    PATRUL_MOBILE_DATA,
    PATRUL_UNIQUE_DATA,
    PATRUL_LOCATION_DATA,
}

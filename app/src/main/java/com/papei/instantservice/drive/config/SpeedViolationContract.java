package com.papei.instantservice.drive.config;

public class SpeedViolationContract {

    private SpeedViolationContract() { }

    public static class SpeedViolationEntry {

        public static final String TABLE_NAME = "speed_violations";
        public static final String ID = "id";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String SPEED = "speed";
        public static final String TIMESTAMP = "timestamp";

    }
}

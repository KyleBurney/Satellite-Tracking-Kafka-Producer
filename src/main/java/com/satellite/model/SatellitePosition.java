package com.satellite.model;

public class SatellitePosition {
    private String satelliteId;
    private String satelliteName;
    private double latitude;
    private double longitude;
    private double altitude;
    private double velocity;
    private long timestamp;
    
    private SatellitePosition() {}
    
    public static SatellitePositionBuilder builder() {
        return new SatellitePositionBuilder();
    }
    
    // Getters
    public String getSatelliteId() { return satelliteId; }
    public String getSatelliteName() { return satelliteName; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getAltitude() { return altitude; }
    public double getVelocity() { return velocity; }
    public long getTimestamp() { return timestamp; }
    
    // Builder
    public static class SatellitePositionBuilder {
        private String satelliteId;
        private String satelliteName;
        private double latitude;
        private double longitude;
        private double altitude;
        private double velocity;
        private long timestamp;
        
        public SatellitePositionBuilder satelliteId(String satelliteId) {
            this.satelliteId = satelliteId;
            return this;
        }
        
        public SatellitePositionBuilder satelliteName(String satelliteName) {
            this.satelliteName = satelliteName;
            return this;
        }
        
        public SatellitePositionBuilder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }
        
        public SatellitePositionBuilder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }
        
        public SatellitePositionBuilder altitude(double altitude) {
            this.altitude = altitude;
            return this;
        }
        
        public SatellitePositionBuilder velocity(double velocity) {
            this.velocity = velocity;
            return this;
        }
        
        public SatellitePositionBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public SatellitePosition build() {
            SatellitePosition pos = new SatellitePosition();
            pos.satelliteId = this.satelliteId;
            pos.satelliteName = this.satelliteName;
            pos.latitude = this.latitude;
            pos.longitude = this.longitude;
            pos.altitude = this.altitude;
            pos.velocity = this.velocity;
            pos.timestamp = this.timestamp;
            return pos;
        }
    }
}
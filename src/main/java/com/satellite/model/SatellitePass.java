package com.satellite.model;

public class SatellitePass {
    private String satelliteId;
    private String satelliteName;
    private double observerLatitude;
    private double observerLongitude;
    private long startTime;
    private long endTime;
    private double maxElevation;
    private double startAzimuth;
    private double endAzimuth;
    
    private SatellitePass() {}
    
    public static SatellitePassBuilder builder() {
        return new SatellitePassBuilder();
    }
    
    // Getters
    public String getSatelliteId() { return satelliteId; }
    public String getSatelliteName() { return satelliteName; }
    public double getObserverLatitude() { return observerLatitude; }
    public double getObserverLongitude() { return observerLongitude; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public double getMaxElevation() { return maxElevation; }
    public double getStartAzimuth() { return startAzimuth; }
    public double getEndAzimuth() { return endAzimuth; }
    
    // Builder
    public static class SatellitePassBuilder {
        private String satelliteId;
        private String satelliteName;
        private double observerLatitude;
        private double observerLongitude;
        private long startTime;
        private long endTime;
        private double maxElevation;
        private double startAzimuth;
        private double endAzimuth;
        
        public SatellitePassBuilder satelliteId(String satelliteId) {
            this.satelliteId = satelliteId;
            return this;
        }
        
        public SatellitePassBuilder satelliteName(String satelliteName) {
            this.satelliteName = satelliteName;
            return this;
        }
        
        public SatellitePassBuilder observerLatitude(double observerLatitude) {
            this.observerLatitude = observerLatitude;
            return this;
        }
        
        public SatellitePassBuilder observerLongitude(double observerLongitude) {
            this.observerLongitude = observerLongitude;
            return this;
        }
        
        public SatellitePassBuilder startTime(long startTime) {
            this.startTime = startTime;
            return this;
        }
        
        public SatellitePassBuilder endTime(long endTime) {
            this.endTime = endTime;
            return this;
        }
        
        public SatellitePassBuilder maxElevation(double maxElevation) {
            this.maxElevation = maxElevation;
            return this;
        }
        
        public SatellitePassBuilder startAzimuth(double startAzimuth) {
            this.startAzimuth = startAzimuth;
            return this;
        }
        
        public SatellitePassBuilder endAzimuth(double endAzimuth) {
            this.endAzimuth = endAzimuth;
            return this;
        }
        
        public SatellitePass build() {
            SatellitePass pass = new SatellitePass();
            pass.satelliteId = this.satelliteId;
            pass.satelliteName = this.satelliteName;
            pass.observerLatitude = this.observerLatitude;
            pass.observerLongitude = this.observerLongitude;
            pass.startTime = this.startTime;
            pass.endTime = this.endTime;
            pass.maxElevation = this.maxElevation;
            pass.startAzimuth = this.startAzimuth;
            pass.endAzimuth = this.endAzimuth;
            return pass;
        }
    }
}
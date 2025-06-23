package com.satellite.model;

public class TleData {
    private String satelliteId;
    private String satelliteName;
    private String line1;
    private String line2;
    private int epochYear;
    private double epochDay;
    private double meanMotion;
    private double eccentricity;
    private double inclination;
    private double raan;
    private double argumentOfPerigee;
    private double meanAnomaly;
    private long lastUpdated;
    
    private TleData() {}
    
    public static TleDataBuilder builder() {
        return new TleDataBuilder();
    }
    
    public static TleData parseTle(String name, String line1, String line2) {
        return TleData.builder()
                .satelliteId(line1.substring(2, 7).trim())
                .satelliteName(name.trim())
                .line1(line1)
                .line2(line2)
                .epochYear(Integer.parseInt(line1.substring(18, 20)))
                .epochDay(Double.parseDouble(line1.substring(20, 32)))
                .meanMotion(Double.parseDouble(line2.substring(52, 63)))
                .eccentricity(Double.parseDouble("0." + line2.substring(26, 33)))
                .inclination(Double.parseDouble(line2.substring(8, 16)))
                .raan(Double.parseDouble(line2.substring(17, 25)))
                .argumentOfPerigee(Double.parseDouble(line2.substring(34, 42)))
                .meanAnomaly(Double.parseDouble(line2.substring(43, 51)))
                .lastUpdated(System.currentTimeMillis())
                .build();
    }
    
    // Getters
    public String getSatelliteId() { return satelliteId; }
    public String getSatelliteName() { return satelliteName; }
    public String getLine1() { return line1; }
    public String getLine2() { return line2; }
    public int getEpochYear() { return epochYear; }
    public double getEpochDay() { return epochDay; }
    public double getMeanMotion() { return meanMotion; }
    public double getEccentricity() { return eccentricity; }
    public double getInclination() { return inclination; }
    public double getRaan() { return raan; }
    public double getArgumentOfPerigee() { return argumentOfPerigee; }
    public double getMeanAnomaly() { return meanAnomaly; }
    public long getLastUpdated() { return lastUpdated; }
    
    // Builder
    public static class TleDataBuilder {
        private String satelliteId;
        private String satelliteName;
        private String line1;
        private String line2;
        private int epochYear;
        private double epochDay;
        private double meanMotion;
        private double eccentricity;
        private double inclination;
        private double raan;
        private double argumentOfPerigee;
        private double meanAnomaly;
        private long lastUpdated;
        
        public TleDataBuilder satelliteId(String satelliteId) {
            this.satelliteId = satelliteId;
            return this;
        }
        
        public TleDataBuilder satelliteName(String satelliteName) {
            this.satelliteName = satelliteName;
            return this;
        }
        
        public TleDataBuilder line1(String line1) {
            this.line1 = line1;
            return this;
        }
        
        public TleDataBuilder line2(String line2) {
            this.line2 = line2;
            return this;
        }
        
        public TleDataBuilder epochYear(int epochYear) {
            this.epochYear = epochYear;
            return this;
        }
        
        public TleDataBuilder epochDay(double epochDay) {
            this.epochDay = epochDay;
            return this;
        }
        
        public TleDataBuilder meanMotion(double meanMotion) {
            this.meanMotion = meanMotion;
            return this;
        }
        
        public TleDataBuilder eccentricity(double eccentricity) {
            this.eccentricity = eccentricity;
            return this;
        }
        
        public TleDataBuilder inclination(double inclination) {
            this.inclination = inclination;
            return this;
        }
        
        public TleDataBuilder raan(double raan) {
            this.raan = raan;
            return this;
        }
        
        public TleDataBuilder argumentOfPerigee(double argumentOfPerigee) {
            this.argumentOfPerigee = argumentOfPerigee;
            return this;
        }
        
        public TleDataBuilder meanAnomaly(double meanAnomaly) {
            this.meanAnomaly = meanAnomaly;
            return this;
        }
        
        public TleDataBuilder lastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
        
        public TleData build() {
            TleData tle = new TleData();
            tle.satelliteId = this.satelliteId;
            tle.satelliteName = this.satelliteName;
            tle.line1 = this.line1;
            tle.line2 = this.line2;
            tle.epochYear = this.epochYear;
            tle.epochDay = this.epochDay;
            tle.meanMotion = this.meanMotion;
            tle.eccentricity = this.eccentricity;
            tle.inclination = this.inclination;
            tle.raan = this.raan;
            tle.argumentOfPerigee = this.argumentOfPerigee;
            tle.meanAnomaly = this.meanAnomaly;
            tle.lastUpdated = this.lastUpdated;
            return tle;
        }
    }
}
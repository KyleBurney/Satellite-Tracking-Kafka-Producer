package com.satellite.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class N2yoPositionResponse {
    private Info info;
    private List<Position> positions;
    
    public Info getInfo() {
        return info;
    }
    
    public void setInfo(Info info) {
        this.info = info;
    }
    
    public List<Position> getPositions() {
        return positions;
    }
    
    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
    
    public static class Info {
        @JsonProperty("satname")
        private String satName;
        @JsonProperty("satid")
        private int satId;
        @JsonProperty("transactionscount")
        private int transactionsCount;
        
        public String getSatName() {
            return satName;
        }
        
        public void setSatName(String satName) {
            this.satName = satName;
        }
        
        public int getSatId() {
            return satId;
        }
        
        public void setSatId(int satId) {
            this.satId = satId;
        }
        
        public int getTransactionsCount() {
            return transactionsCount;
        }
        
        public void setTransactionsCount(int transactionsCount) {
            this.transactionsCount = transactionsCount;
        }
    }
    
    public static class Position {
        @JsonProperty("satlatitude")
        private double latitude;
        @JsonProperty("satlongitude")
        private double longitude;
        @JsonProperty("sataltitude")
        private double altitude;
        private double azimuth;
        private double elevation;
        private double ra;
        private double dec;
        private long timestamp;
        
        public double getLatitude() {
            return latitude;
        }
        
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        
        public double getAltitude() {
            return altitude;
        }
        
        public void setAltitude(double altitude) {
            this.altitude = altitude;
        }
        
        public double getAzimuth() {
            return azimuth;
        }
        
        public void setAzimuth(double azimuth) {
            this.azimuth = azimuth;
        }
        
        public double getElevation() {
            return elevation;
        }
        
        public void setElevation(double elevation) {
            this.elevation = elevation;
        }
        
        public double getRa() {
            return ra;
        }
        
        public void setRa(double ra) {
            this.ra = ra;
        }
        
        public double getDec() {
            return dec;
        }
        
        public void setDec(double dec) {
            this.dec = dec;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
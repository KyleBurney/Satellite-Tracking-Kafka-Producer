package com.satellite.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class N2yoPassesResponse {
    private Info info;
    private List<Pass> passes;
    
    public Info getInfo() {
        return info;
    }
    
    public void setInfo(Info info) {
        this.info = info;
    }
    
    public List<Pass> getPasses() {
        return passes;
    }
    
    public void setPasses(List<Pass> passes) {
        this.passes = passes;
    }
    
    public static class Info {
        @JsonProperty("satname")
        private String satName;
        @JsonProperty("satid")
        private int satId;
        @JsonProperty("passescount")
        private int passesCount;
        
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
        
        public int getPassesCount() {
            return passesCount;
        }
        
        public void setPassesCount(int passesCount) {
            this.passesCount = passesCount;
        }
    }
    
    public static class Pass {
        @JsonProperty("startAz")
        private double startAz;
        @JsonProperty("startAzCompass")
        private String startAzCompass;
        @JsonProperty("startEl")
        private double startEl;
        @JsonProperty("startUTC")
        private long startUTC;
        @JsonProperty("maxAz")
        private double maxAz;
        @JsonProperty("maxAzCompass")
        private String maxAzCompass;
        @JsonProperty("maxEl")
        private double maxEl;
        @JsonProperty("maxUTC")
        private long maxUTC;
        @JsonProperty("endAz")
        private double endAz;
        @JsonProperty("endAzCompass")
        private String endAzCompass;
        @JsonProperty("endEl")
        private double endEl;
        @JsonProperty("endUTC")
        private long endUTC;
        private int duration;
        
        // Getters and setters for all fields
        public double getStartAz() { return startAz; }
        public void setStartAz(double startAz) { this.startAz = startAz; }
        
        public String getStartAzCompass() { return startAzCompass; }
        public void setStartAzCompass(String startAzCompass) { this.startAzCompass = startAzCompass; }
        
        public double getStartEl() { return startEl; }
        public void setStartEl(double startEl) { this.startEl = startEl; }
        
        public long getStartUTC() { return startUTC; }
        public void setStartUTC(long startUTC) { this.startUTC = startUTC; }
        
        public double getMaxAz() { return maxAz; }
        public void setMaxAz(double maxAz) { this.maxAz = maxAz; }
        
        public String getMaxAzCompass() { return maxAzCompass; }
        public void setMaxAzCompass(String maxAzCompass) { this.maxAzCompass = maxAzCompass; }
        
        public double getMaxEl() { return maxEl; }
        public void setMaxEl(double maxEl) { this.maxEl = maxEl; }
        
        public long getMaxUTC() { return maxUTC; }
        public void setMaxUTC(long maxUTC) { this.maxUTC = maxUTC; }
        
        public double getEndAz() { return endAz; }
        public void setEndAz(double endAz) { this.endAz = endAz; }
        
        public String getEndAzCompass() { return endAzCompass; }
        public void setEndAzCompass(String endAzCompass) { this.endAzCompass = endAzCompass; }
        
        public double getEndEl() { return endEl; }
        public void setEndEl(double endEl) { this.endEl = endEl; }
        
        public long getEndUTC() { return endUTC; }
        public void setEndUTC(long endUTC) { this.endUTC = endUTC; }
        
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
    }
}
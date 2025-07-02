package com.satellite.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "satellite")
public class SatelliteConfig {
    private List<TrackedSatellite> trackedSatellites;
    private List<ObserverLocation> observerLocations;
    
    public List<TrackedSatellite> getTrackedSatellites() {
        return trackedSatellites;
    }
    
    public void setTrackedSatellites(List<TrackedSatellite> trackedSatellites) {
        this.trackedSatellites = trackedSatellites;
    }
    
    public List<ObserverLocation> getObserverLocations() {
        return observerLocations;
    }
    
    public void setObserverLocations(List<ObserverLocation> observerLocations) {
        this.observerLocations = observerLocations;
    }
    
    public static class TrackedSatellite {
        private String id;
        private String name;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class ObserverLocation {
        private String name;
        private double latitude;
        private double longitude;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
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
    }
}
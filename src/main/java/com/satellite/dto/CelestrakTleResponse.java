package com.satellite.dto;

import java.util.List;

public class CelestrakTleResponse {
    private List<TleEntry> tle;
    
        public static class TleEntry {
        private String satelliteId;
        private String name;
        private String line1;
        private String line2;
    }
}
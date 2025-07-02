package com.satellite.service;

import com.satellite.model.SatellitePass;
import com.satellite.model.SatellitePosition;
import com.satellite.model.TleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ChangeDetectionService {
    
    private static final Logger log = LoggerFactory.getLogger(ChangeDetectionService.class);
    
    private final CacheManager cacheManager;
    private final SatelliteEventProducer eventProducer;
    
    private static final String TLE_CACHE = "tleCache";
    private static final String POSITION_CACHE = "positionCache";
    private static final String PASS_CACHE = "passCache";
    
    public ChangeDetectionService(CacheManager cacheManager, SatelliteEventProducer eventProducer) {
        this.cacheManager = cacheManager;
        this.eventProducer = eventProducer;
    }
    
    public void checkAndProduceTleUpdate(TleData newTle, String source) {
        Cache cache = cacheManager.getCache(TLE_CACHE);
        if (cache == null) return;
        
        String cacheKey = newTle.getSatelliteId();
        TleData cachedTle = cache.get(cacheKey, TleData.class);
        
        if (cachedTle == null || hasTleChanged(cachedTle, newTle)) {
            log.info("TLE change detected for satellite {}", newTle.getSatelliteId());
            eventProducer.produceTleUpdateEvent(newTle, source);
            cache.put(cacheKey, newTle);
        }
    }
    
    public void checkAndProducePositionUpdate(SatellitePosition newPosition, String source) {
        Cache cache = cacheManager.getCache(POSITION_CACHE);
        if (cache == null) return;
        
        String cacheKey = newPosition.getSatelliteId();
        SatellitePosition cachedPosition = cache.get(cacheKey, SatellitePosition.class);
        
        if (cachedPosition == null || hasPositionChanged(cachedPosition, newPosition)) {
            eventProducer.producePositionEvent(newPosition, source);
            cache.put(cacheKey, newPosition);
        }
    }
    
    public void checkAndProducePassUpdate(SatellitePass newPass) {
        Cache cache = cacheManager.getCache(PASS_CACHE);
        if (cache == null) return;
        
        String cacheKey = String.format("%s_%f_%f_%d", 
                newPass.getSatelliteId(),
                newPass.getObserverLatitude(),
                newPass.getObserverLongitude(),
                newPass.getStartTime());
        
        SatellitePass cachedPass = cache.get(cacheKey, SatellitePass.class);
        
        if (cachedPass == null) {
            log.info("New pass detected for satellite {} at ({}, {})", 
                    newPass.getSatelliteId(),
                    newPass.getObserverLatitude(),
                    newPass.getObserverLongitude());
            eventProducer.producePassEvent(newPass);
            cache.put(cacheKey, newPass);
        }
    }
    
    private boolean hasTleChanged(TleData old, TleData new_) {
        // Check if key orbital elements have changed
        return !Objects.equals(old.getLine1(), new_.getLine1()) ||
               !Objects.equals(old.getLine2(), new_.getLine2()) ||
               Math.abs(old.getMeanMotion() - new_.getMeanMotion()) > 0.00001 ||
               Math.abs(old.getEccentricity() - new_.getEccentricity()) > 0.0000001 ||
               Math.abs(old.getInclination() - new_.getInclination()) > 0.001;
    }
    
    private boolean hasPositionChanged(SatellitePosition old, SatellitePosition new_) {
        // Check if position has changed significantly (more than ~1km)
        double latDiff = Math.abs(old.getLatitude() - new_.getLatitude());
        double lonDiff = Math.abs(old.getLongitude() - new_.getLongitude());
        double altDiff = Math.abs(old.getAltitude() - new_.getAltitude());
        
        return latDiff > 0.01 || lonDiff > 0.01 || altDiff > 1.0;
    }
}
package com.satellite.service;

import com.satellite.avro.SatellitePassEvent;
import com.satellite.avro.SatellitePositionEvent;
import com.satellite.avro.SatelliteTleUpdateEvent;
import com.satellite.model.SatellitePass;
import com.satellite.model.SatellitePosition;
import com.satellite.model.TleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SatelliteEventProducer {
    
    private static final Logger log = LoggerFactory.getLogger(SatelliteEventProducer.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topics.satellite-position}")
    private String positionTopic;
    
    @Value("${kafka.topics.satellite-pass}")
    private String passTopic;
    
    @Value("${kafka.topics.satellite-tle}")
    private String tleTopic;
    
    public SatelliteEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void producePositionEvent(SatellitePosition position, String source) {
        SatellitePositionEvent event = SatellitePositionEvent.newBuilder()
                .setSatelliteId(position.getSatelliteId())
                .setSatelliteName(position.getSatelliteName())
                .setTimestamp(position.getTimestamp())
                .setLatitude(position.getLatitude())
                .setLongitude(position.getLongitude())
                .setAltitude(position.getAltitude())
                .setVelocity(position.getVelocity())
                .setSource(source)
                .build();
        
        String key = position.getSatelliteId();
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(positionTopic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Produced position event for satellite {} at offset {}",
                        position.getSatelliteId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to produce position event for satellite {}: {}",
                        position.getSatelliteId(), ex.getMessage());
            }
        });
    }
    
    public void producePassEvent(SatellitePass pass) {
        SatellitePassEvent event = SatellitePassEvent.newBuilder()
                .setSatelliteId(pass.getSatelliteId())
                .setSatelliteName(pass.getSatelliteName())
                .setTimestamp(System.currentTimeMillis())
                .setObserverLatitude(pass.getObserverLatitude())
                .setObserverLongitude(pass.getObserverLongitude())
                .setStartTime(pass.getStartTime())
                .setEndTime(pass.getEndTime())
                .setMaxElevation(pass.getMaxElevation())
                .setStartAzimuth(pass.getStartAzimuth())
                .setEndAzimuth(pass.getEndAzimuth())
                .build();
        
        // Key includes location to allow multiple observers
        String key = String.format("%s_%f_%f", 
                pass.getSatelliteId(), 
                pass.getObserverLatitude(), 
                pass.getObserverLongitude());
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(passTopic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Produced pass event for satellite {} at location ({}, {})",
                        pass.getSatelliteId(), pass.getObserverLatitude(), pass.getObserverLongitude());
            } else {
                log.error("Failed to produce pass event for satellite {}: {}",
                        pass.getSatelliteId(), ex.getMessage());
            }
        });
    }
    
    public void produceTleUpdateEvent(TleData tle, String source) {
        SatelliteTleUpdateEvent event = SatelliteTleUpdateEvent.newBuilder()
                .setSatelliteId(tle.getSatelliteId())
                .setSatelliteName(tle.getSatelliteName())
                .setTimestamp(System.currentTimeMillis())
                .setLine1(tle.getLine1())
                .setLine2(tle.getLine2())
                .setEpochYear(tle.getEpochYear())
                .setEpochDay(tle.getEpochDay())
                .setMeanMotion(tle.getMeanMotion())
                .setEccentricity(tle.getEccentricity())
                .setInclination(tle.getInclination())
                .setSource(source)
                .build();
        
        String key = tle.getSatelliteId();
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(tleTopic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Produced TLE update event for satellite {} from source {}",
                        tle.getSatelliteId(), source);
            } else {
                log.error("Failed to produce TLE event for satellite {}: {}",
                        tle.getSatelliteId(), ex.getMessage());
            }
        });
    }
}
package com.satellite.service.polling;

import com.satellite.client.SpaceTrackClient;
import com.satellite.model.SatelliteConfig;
import com.satellite.service.ChangeDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(value = "satellite.polling.spacetrack.enabled", havingValue = "true")
public class SpaceTrackPollingService {
    
    private static final Logger log = LoggerFactory.getLogger(SpaceTrackPollingService.class);
    
    private final SpaceTrackClient spaceTrackClient;
    private final ChangeDetectionService changeDetectionService;
    private final SatelliteConfig satelliteConfig;
    
    public SpaceTrackPollingService(SpaceTrackClient spaceTrackClient,
                                   ChangeDetectionService changeDetectionService,
                                   SatelliteConfig satelliteConfig) {
        this.spaceTrackClient = spaceTrackClient;
        this.changeDetectionService = changeDetectionService;
        this.satelliteConfig = satelliteConfig;
    }
    
    @Scheduled(fixedDelayString = "${satellite.polling.spacetrack.interval}000")
    public void pollSpaceTrackData() {
        log.info("Starting SpaceTrack polling cycle");
        
        satelliteConfig.getTrackedSatellites().forEach(satellite -> {
            spaceTrackClient.getLatestTle(satellite.getId())
                    .doOnNext(tle -> changeDetectionService.checkAndProduceTleUpdate(tle, "SPACETRACK"))
                    .doOnError(e -> log.error("Error processing TLE from SpaceTrack for satellite {}: {}", 
                            satellite.getId(), e.getMessage()))
                    .onErrorResume(e -> Mono.empty())
                    .subscribe(
                            tle -> log.debug("Processed SpaceTrack TLE for satellite: {}", tle.getSatelliteId()),
                            error -> log.error("Error in SpaceTrack polling for satellite {}: {}", 
                                    satellite.getId(), error.getMessage())
                    );
        });
    }
}
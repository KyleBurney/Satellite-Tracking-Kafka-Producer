package com.satellite.service.polling;

import com.satellite.client.CelestrakClient;
import com.satellite.model.SatelliteConfig;
import com.satellite.service.ChangeDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(value = "satellite.polling.celestrak.enabled", havingValue = "true")
public class CelestrakPollingService {
    
    private static final Logger log = LoggerFactory.getLogger(CelestrakPollingService.class);
    
    private final CelestrakClient celestrakClient;
    private final ChangeDetectionService changeDetectionService;
    private final SatelliteConfig satelliteConfig;
    
    public CelestrakPollingService(CelestrakClient celestrakClient, 
                                  ChangeDetectionService changeDetectionService,
                                  SatelliteConfig satelliteConfig) {
        this.celestrakClient = celestrakClient;
        this.changeDetectionService = changeDetectionService;
        this.satelliteConfig = satelliteConfig;
    }
    
    @Scheduled(fixedDelayString = "${satellite.polling.celestrak.interval}000")
    public void pollCelestrakData() {
        log.info("Starting Celestrak polling cycle");
        
        satelliteConfig.getTrackedSatellites().forEach(satellite -> {
            celestrakClient.getTleForSatellite(satellite.getId())
                    .doOnNext(tle -> changeDetectionService.checkAndProduceTleUpdate(tle, "CELESTRAK"))
                    .doOnError(e -> log.error("Error processing TLE for satellite {}: {}", 
                            satellite.getId(), e.getMessage()))
                    .onErrorResume(e -> Mono.empty())
                    .subscribe(
                            tle -> log.debug("Processed TLE for satellite: {}", tle.getSatelliteId()),
                            error -> log.error("Error in Celestrak polling for satellite {}: {}", 
                                    satellite.getId(), error.getMessage())
                    );
        });
    }
}
package com.satellite.service.polling;

import com.satellite.client.N2yoClient;
import com.satellite.model.SatelliteConfig;
import com.satellite.service.ChangeDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(value = "satellite.polling.n2yo.enabled", havingValue = "true")
public class N2yoPollingService {
    
    private static final Logger log = LoggerFactory.getLogger(N2yoPollingService.class);
    
    private final N2yoClient n2yoClient;
    private final ChangeDetectionService changeDetectionService;
    private final SatelliteConfig satelliteConfig;
    
    public N2yoPollingService(N2yoClient n2yoClient,
                             ChangeDetectionService changeDetectionService,
                             SatelliteConfig satelliteConfig) {
        this.n2yoClient = n2yoClient;
        this.changeDetectionService = changeDetectionService;
        this.satelliteConfig = satelliteConfig;
    }
    
    @Scheduled(fixedDelayString = "${satellite.polling.n2yo.interval}000")
    public void pollN2yoData() {
        log.info("Starting N2YO polling cycle");
        
        // Poll current positions
        satelliteConfig.getTrackedSatellites().forEach(satellite -> {
            n2yoClient.getCurrentPosition(satellite.getId())
                    .doOnNext(position -> changeDetectionService.checkAndProducePositionUpdate(position, "N2YO"))
                    .doOnError(e -> log.error("Error getting position for satellite {}: {}", 
                            satellite.getId(), e.getMessage()))
                    .onErrorResume(e -> Mono.empty())
                    .subscribe(
                            position -> log.debug("Processed position for satellite: {}", position.getSatelliteId()),
                            error -> log.error("Error in N2YO position polling for satellite {}: {}", 
                                    satellite.getId(), error.getMessage())
                    );
        });
        
        // Poll upcoming passes
        satelliteConfig.getTrackedSatellites().forEach(satellite -> {
            satelliteConfig.getObserverLocations().forEach(location -> {
                n2yoClient.getUpcomingPasses(
                        satellite.getId(), 
                        location.getLatitude(), 
                        location.getLongitude(), 
                        2) // Look 2 days ahead
                        .doOnNext(pass -> changeDetectionService.checkAndProducePassUpdate(pass))
                        .doOnError(e -> log.error("Error getting passes for satellite {} at {}: {}", 
                                satellite.getId(), location.getName(), e.getMessage()))
                        .subscribe(
                                pass -> log.debug("Processed pass for satellite: {}", pass.getSatelliteId()),
                                error -> log.error("Error in N2YO pass polling: {}", error.getMessage())
                        );
            });
        });
    }
}
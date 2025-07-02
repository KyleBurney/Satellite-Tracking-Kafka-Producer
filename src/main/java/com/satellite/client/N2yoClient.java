package com.satellite.client;

import com.satellite.dto.N2yoPassesResponse;
import com.satellite.dto.N2yoPositionResponse;
import com.satellite.model.SatellitePass;
import com.satellite.model.SatellitePosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class N2yoClient {
    
    private static final Logger log = LoggerFactory.getLogger(N2yoClient.class);
    
    @Value("${satellite.polling.n2yo.api-key}")
    private String apiKey;
    
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.n2yo.com/rest/v1/satellite")
            .build();
    
    public Mono<SatellitePosition> getCurrentPosition(String satelliteId) {
        return webClient.get()
                .uri("/positions/{id}/0/0/0/1/&apiKey={key}", satelliteId, apiKey)
                .retrieve()
                .bodyToMono(N2yoPositionResponse.class)
                .map(response -> {
                    N2yoPositionResponse.Position pos = response.getPositions().get(0);
                    return SatellitePosition.builder()
                            .satelliteId(String.valueOf(response.getInfo().getSatId()))
                            .satelliteName(response.getInfo().getSatName())
                            .latitude(pos.getLatitude())
                            .longitude(pos.getLongitude())
                            .altitude(pos.getAltitude())
                            .velocity(calculateVelocity(pos.getAltitude()))
                            .timestamp(pos.getTimestamp() * 1000) // Convert to milliseconds
                            .build();
                })
                .doOnError(e -> log.error("Error fetching position for satellite {}: {}", satelliteId, e.getMessage()));
    }
    
    public Flux<SatellitePass> getUpcomingPasses(String satelliteId, double lat, double lon, int days) {
        return webClient.get()
                .uri("/visualpasses/{id}/{lat}/{lon}/0/{days}/300/&apiKey={key}", 
                    satelliteId, lat, lon, days, apiKey)
                .retrieve()
                .bodyToMono(N2yoPassesResponse.class)
                .flatMapMany(response -> 
                    Flux.fromIterable(response.getPasses())
                        .map(pass -> SatellitePass.builder()
                            .satelliteId(String.valueOf(response.getInfo().getSatId()))
                            .satelliteName(response.getInfo().getSatName())
                            .observerLatitude(lat)
                            .observerLongitude(lon)
                            .startTime(pass.getStartUTC() * 1000)
                            .endTime(pass.getEndUTC() * 1000)
                            .maxElevation(pass.getMaxEl())
                            .startAzimuth(pass.getStartAz())
                            .endAzimuth(pass.getEndAz())
                            .build())
                )
                .doOnError(e -> log.error("Error fetching passes for satellite {}: {}", satelliteId, e.getMessage()));
    }
    
    private double calculateVelocity(double altitude) {
        // Simplified orbital velocity calculation
        double earthRadius = 6371.0; // km
        double orbitalRadius = earthRadius + altitude;
        return Math.sqrt(398600.4418 / orbitalRadius); // km/s
    }
}
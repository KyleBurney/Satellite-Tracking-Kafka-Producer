package com.satellite.client;

import com.satellite.model.TleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CelestrakClient {
    
    private static final Logger log = LoggerFactory.getLogger(CelestrakClient.class);
    
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://celestrak.org")
            .build();
    
    @Cacheable(value = "celestrakTle", key = "#satelliteId")
    public Mono<TleData> getTleForSatellite(String satelliteId) {
        return webClient.get()
                .uri("/NORAD/elements/gp.php?CATNR={id}&FORMAT=TLE", satelliteId)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    String[] lines = response.trim().split("\n");
                    if (lines.length >= 3) {
                        return TleData.parseTle(lines[0], lines[1], lines[2]);
                    }
                    throw new RuntimeException("Invalid TLE response for satellite: " + satelliteId);
                })
                .doOnError(e -> log.error("Error fetching TLE for satellite {}: {}", satelliteId, e.getMessage()));
    }
    
    public Flux<TleData> getActivesSatellites() {
        return webClient.get()
                .uri("/NORAD/elements/gp.php?GROUP=active&FORMAT=TLE")
                .retrieve()
                .bodyToMono(String.class)
                .flatMapMany(response -> {
                    String[] lines = response.trim().split("\n");
                    return Flux.range(0, lines.length / 3)
                            .map(i -> {
                                int idx = i * 3;
                                return TleData.parseTle(lines[idx], lines[idx + 1], lines[idx + 2]);
                            });
                })
                .doOnError(e -> log.error("Error fetching active satellites: {}", e.getMessage()));
    }
}
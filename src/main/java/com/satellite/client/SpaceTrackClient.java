package com.satellite.client;

import com.satellite.model.TleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class SpaceTrackClient {
    
    private static final Logger log = LoggerFactory.getLogger(SpaceTrackClient.class);
    
    @Value("${satellite.polling.spacetrack.username}")
    private String username;
    
    @Value("${satellite.polling.spacetrack.password}")
    private String password;
    
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.space-track.org")
            .build();
    
    private String authCookie;
    
    private Mono<String> authenticate() {
        if (authCookie != null) {
            return Mono.just(authCookie);
        }
        
        return webClient.post()
                .uri("/ajaxauth/login")
                .body(BodyInserters.fromFormData("identity", username)
                        .with("password", password))
                .exchangeToMono(response -> {
                    ResponseCookie chocolatechip = response.cookies().getFirst("chocolatechip");
                    String cookie = chocolatechip != null ? chocolatechip.getValue() : null;
                    authCookie = cookie;
                    return Mono.just(cookie != null ? cookie : "");
                })
                .doOnError(e -> log.error("SpaceTrack authentication failed: {}", e.getMessage()));
    }
    
    public Mono<TleData> getLatestTle(String satelliteId) {
        return authenticate()
                .flatMap(cookie -> webClient.get()
                        .uri("/basicspacedata/query/class/gp/NORAD_CAT_ID/{id}/orderby/EPOCH%20desc/limit/1/format/tle",
                                satelliteId)
                        .cookie("chocolatechip", cookie)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> {
                            String[] lines = response.trim().split("\n");
                            if (lines.length >= 2) {
                                // SpaceTrack doesn't include name in TLE format, extract from line 1
                                String line1 = lines[0];
                                String line2 = lines[1];
                                
                                // Extract satellite name from line 1 (e.g., "25544U" -> "NORAD-25544")
                                String satName = "NORAD-" + satelliteId;
                                
                                // If there are 3 lines, use the first as name
                                if (lines.length >= 3) {
                                    satName = lines[0];
                                    line1 = lines[1];
                                    line2 = lines[2];
                                }
                                
                                log.debug("Parsed TLE for satellite {}: {} / {}", satelliteId, line1, line2);
                                return TleData.parseTle(satName, line1, line2);
                            }
                            throw new RuntimeException("Invalid TLE response - expected at least 2 lines, got " + lines.length);
                        }))
                .doOnError(e -> log.error("Error fetching TLE from SpaceTrack for {}: {}", satelliteId, e.getMessage()));
    }
}
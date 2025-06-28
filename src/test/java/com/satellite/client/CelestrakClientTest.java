package com.satellite.client;

import com.satellite.model.TleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CelestrakClientTest {

    private WebClient mockWebClient;
    private WebClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;
    private WebClient.RequestHeadersSpec mockRequestHeadersSpec;
    private WebClient.ResponseSpec mockResponseSpec;
    private CelestrakClient client;

    @BeforeEach
    void setUp() throws Exception {
        mockWebClient = mock(WebClient.class);
        mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        mockResponseSpec = mock(WebClient.ResponseSpec.class);
        
        client = new CelestrakClient();
        
        // Use reflection to inject the mock WebClient
        Field webClientField = CelestrakClient.class.getDeclaredField("webClient");
        webClientField.setAccessible(true);
        webClientField.set(client, mockWebClient);
        
        // Set up the mock chain
        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    }

    @Test
    void getTleForSatellite_WithValidResponse_ShouldReturnTleData() {
        String satelliteId = "25544";
        String tleResponse = "ISS (ZARYA)\n" +
                "1 25544U 98067A   23001.00000000  .00001234  00000-0  12345-4 0  9999\n" +
                "2 25544  51.6400 123.4567 0001234  12.3456 347.8901 15.48901234123456";

        when(mockRequestHeadersUriSpec.uri(eq("/NORAD/elements/gp.php?CATNR={id}&FORMAT=TLE"), eq(satelliteId))).thenReturn(mockRequestHeadersSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(tleResponse));

        Mono<TleData> result = client.getTleForSatellite(satelliteId);

        StepVerifier.create(result)
                .expectNextMatches(tle -> 
                        tle.getSatelliteId().equals("25544") &&
                        tle.getSatelliteName().equals("ISS (ZARYA)") &&
                        tle.getLine1().contains("25544U 98067A") &&
                        tle.getLine2().contains("25544  51.6400")
                )
                .verifyComplete();
    }

    @Test
    void getTleForSatellite_WithInvalidResponse_ShouldReturnError() {
        String satelliteId = "25544";
        String invalidResponse = "Invalid response";

        when(mockRequestHeadersUriSpec.uri(eq("/NORAD/elements/gp.php?CATNR={id}&FORMAT=TLE"), eq(satelliteId))).thenReturn(mockRequestHeadersSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidResponse));

        Mono<TleData> result = client.getTleForSatellite(satelliteId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Invalid TLE response")
                )
                .verify();
    }

    @Test
    void getActivesSatellites_WithValidResponse_ShouldReturnFluxOfTleData() {
        String tleResponse = "ISS (ZARYA)\n" +
                "1 25544U 98067A   23001.00000000  .00001234  00000-0  12345-4 0  9999\n" +
                "2 25544  51.6400 123.4567 0001234  12.3456 347.8901 15.48901234123456\n" +
                "NOAA-18\n" +
                "1 28654U 05018A   23001.00000000  .00000234  00000-0  12345-5 0  9999\n" +
                "2 28654  99.1234 234.5678 0001234  23.4567 336.7890 14.12345678123456";

        when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(tleResponse));

        Flux<TleData> result = client.getActivesSatellites();

        StepVerifier.create(result)
                .expectNextMatches(tle -> 
                        tle.getSatelliteId().equals("25544") &&
                        tle.getSatelliteName().equals("ISS (ZARYA)")
                )
                .expectNextMatches(tle -> 
                        tle.getSatelliteId().equals("28654") &&
                        tle.getSatelliteName().equals("NOAA-18")
                )
                .verifyComplete();
    }

    @Test
    void getActivesSatellites_WithNetworkError_ShouldReturnError() {
        when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("Network error")));

        Flux<TleData> result = client.getActivesSatellites();

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Network error")
                )
                .verify();
    }
}
package com.satellite.service;

import com.satellite.model.SatellitePass;
import com.satellite.model.SatellitePosition;
import com.satellite.model.TleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeDetectionServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private SatelliteEventProducer eventProducer;

    @Mock
    private Cache tleCache;

    @Mock
    private Cache positionCache;

    @Mock
    private Cache passCache;

    private ChangeDetectionService service;

    @BeforeEach
    void setUp() {
        service = new ChangeDetectionService(cacheManager, eventProducer);
    }

    @Test
    void checkAndProduceTleUpdate_WithNewTle_ShouldProduceEvent() {
        TleData newTle = TleData.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .line1("1 25544U 98067A   23001.00000000  .00001234  00000-0  12345-4 0  9999")
                .line2("2 25544  51.6400 123.4567 0001234  12.3456 347.8901 15.48901234123456")
                .meanMotion(15.48901234)
                .eccentricity(0.0001234)
                .inclination(51.6400)
                .build();

        when(cacheManager.getCache("tleCache")).thenReturn(tleCache);
        when(tleCache.get("25544", TleData.class)).thenReturn(null);

        service.checkAndProduceTleUpdate(newTle, "Celestrak");

        verify(eventProducer).produceTleUpdateEvent(newTle, "Celestrak");
        verify(tleCache).put("25544", newTle);
    }

    @Test
    void checkAndProduceTleUpdate_WithChangedTle_ShouldProduceEvent() {
        TleData cachedTle = TleData.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .line1("1 25544U 98067A   23001.00000000  .00001234  00000-0  12345-4 0  9999")
                .line2("2 25544  51.6400 123.4567 0001234  12.3456 347.8901 15.48901234123456")
                .meanMotion(15.48901234)
                .eccentricity(0.0001234)
                .inclination(51.6400)
                .build();

        TleData newTle = TleData.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .line1("1 25544U 98067A   23002.00000000  .00001234  00000-0  12345-4 0  9999")
                .line2("2 25544  51.6400 123.4567 0001234  12.3456 347.8901 15.48901234123456")
                .meanMotion(15.48901234)
                .eccentricity(0.0001234)
                .inclination(51.6400)
                .build();

        when(cacheManager.getCache("tleCache")).thenReturn(tleCache);
        when(tleCache.get("25544", TleData.class)).thenReturn(cachedTle);

        service.checkAndProduceTleUpdate(newTle, "Celestrak");

        verify(eventProducer).produceTleUpdateEvent(newTle, "Celestrak");
        verify(tleCache).put("25544", newTle);
    }

    @Test
    void checkAndProduceTleUpdate_WithUnchangedTle_ShouldNotProduceEvent() {
        TleData cachedTle = TleData.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .line1("1 25544U 98067A   23001.00000000  .00001234  00000-0  12345-4 0  9999")
                .line2("2 25544  51.6400 123.4567 0001234  12.3456 347.8901 15.48901234123456")
                .meanMotion(15.48901234)
                .eccentricity(0.0001234)
                .inclination(51.6400)
                .build();

        TleData newTle = TleData.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .line1("1 25544U 98067A   23001.00000000  .00001234  00000-0  12345-4 0  9999")
                .line2("2 25544  51.6400 123.4567 0001234  12.3456 347.8901 15.48901234123456")
                .meanMotion(15.48901234)
                .eccentricity(0.0001234)
                .inclination(51.6400)
                .build();

        when(cacheManager.getCache("tleCache")).thenReturn(tleCache);
        when(tleCache.get("25544", TleData.class)).thenReturn(cachedTle);

        service.checkAndProduceTleUpdate(newTle, "Celestrak");

        verify(eventProducer, never()).produceTleUpdateEvent(any(), any());
        verify(tleCache, never()).put(any(), any());
    }

    @Test
    void checkAndProducePositionUpdate_WithNewPosition_ShouldProduceEvent() {
        SatellitePosition newPosition = SatellitePosition.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .timestamp(1234567890L)
                .latitude(45.0)
                .longitude(-75.0)
                .altitude(400.0)
                .velocity(7.8)
                .build();

        when(cacheManager.getCache("positionCache")).thenReturn(positionCache);
        when(positionCache.get("25544", SatellitePosition.class)).thenReturn(null);

        service.checkAndProducePositionUpdate(newPosition, "N2YO");

        verify(eventProducer).producePositionEvent(newPosition, "N2YO");
        verify(positionCache).put("25544", newPosition);
    }

    @Test
    void checkAndProducePositionUpdate_WithSignificantChange_ShouldProduceEvent() {
        SatellitePosition cachedPosition = SatellitePosition.builder()
                .satelliteId("25544")
                .latitude(45.0)
                .longitude(-75.0)
                .altitude(400.0)
                .build();

        SatellitePosition newPosition = SatellitePosition.builder()
                .satelliteId("25544")
                .latitude(45.02)
                .longitude(-75.02)
                .altitude(402.0)
                .build();

        when(cacheManager.getCache("positionCache")).thenReturn(positionCache);
        when(positionCache.get("25544", SatellitePosition.class)).thenReturn(cachedPosition);

        service.checkAndProducePositionUpdate(newPosition, "N2YO");

        verify(eventProducer).producePositionEvent(newPosition, "N2YO");
        verify(positionCache).put("25544", newPosition);
    }

    @Test
    void checkAndProducePositionUpdate_WithInsignificantChange_ShouldNotProduceEvent() {
        SatellitePosition cachedPosition = SatellitePosition.builder()
                .satelliteId("25544")
                .latitude(45.0)
                .longitude(-75.0)
                .altitude(400.0)
                .build();

        SatellitePosition newPosition = SatellitePosition.builder()
                .satelliteId("25544")
                .latitude(45.005)
                .longitude(-75.005)
                .altitude(400.5)
                .build();

        when(cacheManager.getCache("positionCache")).thenReturn(positionCache);
        when(positionCache.get("25544", SatellitePosition.class)).thenReturn(cachedPosition);

        service.checkAndProducePositionUpdate(newPosition, "N2YO");

        verify(eventProducer, never()).producePositionEvent(any(), any());
        verify(positionCache, never()).put(any(), any());
    }

    @Test
    void checkAndProducePassUpdate_WithNewPass_ShouldProduceEvent() {
        SatellitePass newPass = SatellitePass.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .observerLatitude(45.0)
                .observerLongitude(-75.0)
                .startTime(1234567890L)
                .endTime(1234568000L)
                .maxElevation(85.0)
                .startAzimuth(270.0)
                .endAzimuth(90.0)
                .build();

        when(cacheManager.getCache("passCache")).thenReturn(passCache);
        when(passCache.get("25544_45.000000_-75.000000_1234567890", SatellitePass.class)).thenReturn(null);

        service.checkAndProducePassUpdate(newPass);

        verify(eventProducer).producePassEvent(newPass);
        verify(passCache).put("25544_45.000000_-75.000000_1234567890", newPass);
    }

    @Test
    void checkAndProducePassUpdate_WithExistingPass_ShouldNotProduceEvent() {
        SatellitePass existingPass = SatellitePass.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .observerLatitude(45.0)
                .observerLongitude(-75.0)
                .startTime(1234567890L)
                .endTime(1234568000L)
                .maxElevation(85.0)
                .startAzimuth(270.0)
                .endAzimuth(90.0)
                .build();

        when(cacheManager.getCache("passCache")).thenReturn(passCache);
        when(passCache.get("25544_45.000000_-75.000000_1234567890", SatellitePass.class)).thenReturn(existingPass);

        service.checkAndProducePassUpdate(existingPass);

        verify(eventProducer, never()).producePassEvent(any());
        verify(passCache, never()).put(any(), any());
    }

    @Test
    void checkAndProduceTleUpdate_WithNullCache_ShouldNotProduceEvent() {
        TleData newTle = TleData.builder()
                .satelliteId("25544")
                .build();

        when(cacheManager.getCache("tleCache")).thenReturn(null);

        service.checkAndProduceTleUpdate(newTle, "Celestrak");

        verify(eventProducer, never()).produceTleUpdateEvent(any(), any());
    }
}
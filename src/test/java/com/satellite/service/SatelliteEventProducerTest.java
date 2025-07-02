package com.satellite.service;

import com.satellite.avro.SatellitePassEvent;
import com.satellite.avro.SatellitePositionEvent;
import com.satellite.avro.SatelliteTleUpdateEvent;
import com.satellite.model.SatellitePass;
import com.satellite.model.SatellitePosition;
import com.satellite.model.TleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SatelliteEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SendResult<String, Object> sendResult;

    private SatelliteEventProducer producer;

    @BeforeEach
    void setUp() {
        producer = new SatelliteEventProducer(kafkaTemplate);
        ReflectionTestUtils.setField(producer, "positionTopic", "satellite-position");
        ReflectionTestUtils.setField(producer, "passTopic", "satellite-pass");
        ReflectionTestUtils.setField(producer, "tleTopic", "satellite-tle");
    }

    @Test
    void producePositionEvent_ShouldSendEventToKafka() {
        SatellitePosition position = SatellitePosition.builder()
                .satelliteId("12345")
                .satelliteName("Test Satellite")
                .timestamp(1234567890L)
                .latitude(45.0)
                .longitude(-75.0)
                .altitude(400.0)
                .velocity(7.8)
                .build();

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(String.class), any(String.class), any())).thenReturn(future);

        producer.producePositionEvent(position, "N2YO");

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SatellitePositionEvent> eventCaptor = ArgumentCaptor.forClass(SatellitePositionEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertEquals("satellite-position", topicCaptor.getValue());
        assertEquals("12345", keyCaptor.getValue());

        SatellitePositionEvent event = eventCaptor.getValue();
        assertEquals("12345", event.getSatelliteId());
        assertEquals("Test Satellite", event.getSatelliteName());
        assertEquals(1234567890L, event.getTimestamp());
        assertEquals(45.0, event.getLatitude());
        assertEquals(-75.0, event.getLongitude());
        assertEquals(400.0, event.getAltitude());
        assertEquals(7.8, event.getVelocity());
        assertEquals("N2YO", event.getSource());
    }

    @Test
    void producePassEvent_ShouldSendEventToKafka() {
        SatellitePass pass = SatellitePass.builder()
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

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(String.class), any(String.class), any())).thenReturn(future);

        producer.producePassEvent(pass);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SatellitePassEvent> eventCaptor = ArgumentCaptor.forClass(SatellitePassEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertEquals("satellite-pass", topicCaptor.getValue());
        assertEquals("25544_45.000000_-75.000000", keyCaptor.getValue());

        SatellitePassEvent event = eventCaptor.getValue();
        assertEquals("25544", event.getSatelliteId());
        assertEquals("ISS", event.getSatelliteName());
        assertEquals(45.0, event.getObserverLatitude());
        assertEquals(-75.0, event.getObserverLongitude());
        assertEquals(1234567890L, event.getStartTime());
        assertEquals(1234568000L, event.getEndTime());
        assertEquals(85.0, event.getMaxElevation());
        assertEquals(270.0, event.getStartAzimuth());
        assertEquals(90.0, event.getEndAzimuth());
    }

    @Test
    void produceTleUpdateEvent_ShouldSendEventToKafka() {
        TleData tle = TleData.builder()
                .satelliteId("25544")
                .satelliteName("ISS")
                .line1("1 25544U 98067A   23001.00000000  .00001234  00000-0  12345-4 0  9999")
                .line2("2 25544  51.6400 123.4567 0001234  12.3456 347.8901 15.48901234123456")
                .epochYear(23)
                .epochDay(1.0)
                .meanMotion(15.48901234)
                .eccentricity(0.0001234)
                .inclination(51.6400)
                .build();

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(String.class), any(String.class), any())).thenReturn(future);

        producer.produceTleUpdateEvent(tle, "Celestrak");

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SatelliteTleUpdateEvent> eventCaptor = ArgumentCaptor.forClass(SatelliteTleUpdateEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertEquals("satellite-tle", topicCaptor.getValue());
        assertEquals("25544", keyCaptor.getValue());

        SatelliteTleUpdateEvent event = eventCaptor.getValue();
        assertEquals("25544", event.getSatelliteId());
        assertEquals("ISS", event.getSatelliteName());
        assertEquals(tle.getLine1(), event.getLine1());
        assertEquals(tle.getLine2(), event.getLine2());
        assertEquals(23, event.getEpochYear());
        assertEquals(1.0, event.getEpochDay());
        assertEquals(15.48901234, event.getMeanMotion());
        assertEquals(0.0001234, event.getEccentricity());
        assertEquals(51.6400, event.getInclination());
        assertEquals("Celestrak", event.getSource());
    }
}
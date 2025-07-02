# Satellite Tracking Kafka Producer

A Spring Boot application that polls multiple satellite data sources and produces Kafka events for satellite position updates, orbital changes, and pass predictions.

## Features

- **Multi-source Data Collection**: Polls data from CelesTrak, N2YO, and Space-Track.org
- **Kafka Event Streaming**: Produces Avro-serialized events to Kafka topics
- **Change Detection**: Only produces events when significant changes are detected
- **Log Compaction**: Topics configured for log compaction to maintain latest state
- **Caching**: Built-in caching to reduce API calls and improve performance

## Prerequisites

- Java 17+
- Maven 3.6+
- Kafka broker running on localhost:9092
- Avro Schema Registry running on localhost:8081
- API keys for [N2YO](https://www.n2yo.com/login) and [Space-Track](https://www.space-track.org/auth/login) 

## Setup

### 1. Start Kafka and Schema Registry

Using Docker Compose (recommended):

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  schema-registry:
    image: confluentinc/cp-schema-registry:7.5.0
    depends_on:
      - kafka
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema-registry
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: kafka:9092
```

### 2. Configure API Keys

Set environment variables or update `application.yml`:

```bash
export N2YO_API_KEY=your-n2yo-api-key
export SPACETRACK_USERNAME=your-spacetrack-username
export SPACETRACK_PASSWORD=your-spacetrack-password
```

### 3. Build and Run

```bash
# Clone the repository
git clone <repository-url>
cd satellite-tracker

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

Or run as a JAR:

```bash
java -jar target/satellite-tracker-1.0.0.jar
```

## Testing

Run the unit tests:

```bash
mvn test
```

Run tests with detailed output:

```bash
mvn test -Dtest.verbose=true
```

Run a specific test class:

```bash
mvn test -Dtest=CelestrakClientTest
```

## Configuration

### Tracked Satellites

Edit `application.yml` to add or remove satellites:

```yaml
satellite:
  tracked-satellites:
    - id: "25544"
      name: "ISS"
    - id: "43013"
      name: "NOAA-20"
```

### Observer Locations

Configure locations for pass predictions:

```yaml
satellite:
  observer-locations:
    - name: "New York"
      latitude: 40.7128
      longitude: -74.0060
```

### Polling Intervals

Adjust polling frequencies (in seconds):

```yaml
satellite:
  polling:
    celestrak:
      interval: 1800  # 30 minutes
    n2yo:
      interval: 300   # 5 minutes
    spacetrack:
      interval: 3600  # 1 hour
```

## Kafka Topics

The application creates three log-compacted topics:

1. **satellite-position-events**: Current satellite positions
   - Key: satellite ID
   - Value: SatellitePositionEvent (Avro)

2. **satellite-pass-events**: Upcoming visible passes
   - Key: `satelliteId_latitude_longitude`
   - Value: SatellitePassEvent (Avro)

3. **satellite-tle-updates**: TLE orbital element updates
   - Key: satellite ID
   - Value: SatelliteTleUpdateEvent (Avro)

## Consuming Events

Example Kafka consumer configuration:

```java
props.put("key.deserializer", StringDeserializer.class);
props.put("value.deserializer", KafkaAvroDeserializer.class);
props.put("schema.registry.url", "http://localhost:8081");
props.put("specific.avro.reader", true);
```

## API Endpoints

- `GET /health` - Health check endpoint
- `GET /metrics` - Kafka producer metrics

## Event Examples

### Position Event
```json
{
  "satelliteId": "25544",
  "satelliteName": "ISS",
  "timestamp": 1703123456789,
  "latitude": 51.5074,
  "longitude": -0.1278,
  "altitude": 408.5,
  "velocity": 7.66,
  "source": "N2YO"
}
```

### Pass Event
```json
{
  "satelliteId": "25544",
  "satelliteName": "ISS",
  "observerLatitude": 40.7128,
  "observerLongitude": -74.0060,
  "startTime": 1703123456789,
  "endTime": 1703123856789,
  "maxElevation": 78.5,
  "startAzimuth": 315.2,
  "endAzimuth": 135.8
}
```

## Monitoring

Monitor Kafka topics:

```bash
# List topics
kafka-topics --bootstrap-server localhost:9092 --list

# View events
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic satellite-position-events \
  --from-beginning \
  --property print.key=true

# Check topic configuration
kafka-topics --bootstrap-server localhost:9092 \
  --describe --topic satellite-position-events
```

## Troubleshooting

1. **Connection refused to Kafka**: Ensure Kafka is running on localhost:9092
2. **Schema Registry errors**: Verify Schema Registry is running on localhost:8081
3. **API rate limits**: Adjust polling intervals if hitting rate limits
4. **Missing TLE data**: Some satellites may not have publicly available TLE data

## License

This project is licensed under the MIT License.
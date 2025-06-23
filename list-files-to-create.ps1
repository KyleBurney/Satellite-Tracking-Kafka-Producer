# This script helps you know which files need to be created
# Copy the Java code from the artifacts provided in the conversation

$files = @{
    "Main Application" = "src\main\java\com\satellite\SatelliteTrackerApplication.java"
    "Kafka Config" = "src\main\java\com\satellite\config\KafkaConfig.java"
    "Cache Config" = "src\main\java\com\satellite\config\CacheConfig.java"
    "Satellite Config" = "src\main\java\com\satellite\model\SatelliteConfig.java"
    "TLE Data" = "src\main\java\com\satellite\model\TleData.java"
    "Satellite Position" = "src\main\java\com\satellite\model\SatellitePosition.java"
    "Satellite Pass" = "src\main\java\com\satellite\model\SatellitePass.java"
    "Celestrak Client" = "src\main\java\com\satellite\client\CelestrakClient.java"
    "N2YO Client" = "src\main\java\com\satellite\client\N2yoClient.java"
    "SpaceTrack Client" = "src\main\java\com\satellite\client\SpaceTrackClient.java"
    "Event Producer" = "src\main\java\com\satellite\service\SatelliteEventProducer.java"
    "Change Detection" = "src\main\java\com\satellite\service\ChangeDetectionService.java"
    "Celestrak Polling" = "src\main\java\com\satellite\service\polling\CelestrakPollingService.java"
    "N2YO Polling" = "src\main\java\com\satellite\service\polling\N2yoPollingService.java"
    "SpaceTrack Polling" = "src\main\java\com\satellite\service\polling\SpaceTrackPollingService.java"
    "Health Controller" = "src\main\java\com\satellite\controller\HealthCheckController.java"
}

Write-Host "`nJava files to create:" -ForegroundColor Yellow
foreach ($file in $files.GetEnumerator()) {
    Write-Host "$($file.Key): $($file.Value)" -ForegroundColor Cyan
}

Write-Host "`nRefer to the artifacts in the conversation for the complete source code." -ForegroundColor Green

package com.htw.collector.app.service;

import com.htw.collector.domain.collecteddata.CollectedData;
import com.htw.collector.domain.collecteddata.repository.CollectedDataRepository;
import com.htw.collector.domain.datapoint.DataPoint;
import com.htw.collector.domain.datapoint.repository.DataPointRepository;
import com.htw.collector.domain.datasource.DataSource;
import com.htw.collector.domain.datasource.repository.DataSourceRepository;
import com.htw.collector.domain.enums.CollectionStatus;
import com.htw.collector.domain.enums.ProtocolType;
import com.htw.collector.domain.enums.TaskStatus;
import com.htw.collector.domain.port.ProtocolAdapterPort;
import com.htw.collector.domain.task.CollectionTaskLog;
import com.htw.collector.domain.task.repository.CollectionTaskLogRepository;
import com.htw.collector.infrastructure.protocol.CollectorAdapterFactory;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectorAdapterFactory adapterFactory;
    private final DataPointRepository dataPointRepo;
    private final CollectedDataRepository collectedDataRepo;
    private final DataSourceRepository dataSourceRepo;
    private final CollectionTaskLogRepository taskLogRepo;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, CollectionStatus> statusMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public void startCollection(Long dataSourceId) {
        if (scheduledTasks.containsKey(dataSourceId)) {
            log.info("Collection already running for dataSourceId={}", dataSourceId);
            return;
        }

        DataSource dataSource = dataSourceRepo.findById(dataSourceId)
                .orElseThrow(() -> new IllegalArgumentException("DataSource not found with id: " + dataSourceId));

        List<DataPoint> dataPoints = dataPointRepo.findEnabledByDataSourceId(dataSourceId);
        if (dataPoints.isEmpty()) {
            log.warn("No enabled data points found for dataSourceId={}", dataSourceId);
            return;
        }

        try {
            ProtocolAdapterPort adapter = adapterFactory.getOrCreate(dataSource);
            adapter.connect();
            log.info("Connected adapter for dataSourceId={}", dataSourceId);

            // For MQTT, set up subscriptions instead of polling
            if (dataSource.getProtocolType() == ProtocolType.MQTT) {
                for (DataPoint point : dataPoints) {
                    try {
                        adapter.subscribe(point, value -> {
                            CollectedData cd = buildCollectedData(point, value);
                            collectedDataRepo.save(cd);
                        });
                    } catch (Exception e) {
                        log.error("Failed to subscribe to MQTT topic for point {}", point.getTagName(), e);
                    }
                }
                statusMap.put(dataSourceId, CollectionStatus.RUNNING);
                // Store a dummy scheduled future to mark as running
                ScheduledFuture<?> dummyFuture = scheduler.scheduleAtFixedRate(
                        () -> {}, 1, Integer.MAX_VALUE, TimeUnit.HOURS);
                scheduledTasks.put(dataSourceId, dummyFuture);
            } else {
                // Polling: use minimum interval across data points
                int intervalMs = dataPoints.stream()
                        .mapToInt(dp -> dp.getSampleIntervalMs() != null ? dp.getSampleIntervalMs() : 1000)
                        .min()
                        .orElse(1000);

                ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                        () -> collectOnce(dataSource, dataPoints, adapter),
                        0, intervalMs, TimeUnit.MILLISECONDS);

                scheduledTasks.put(dataSourceId, future);
                statusMap.put(dataSourceId, CollectionStatus.RUNNING);
                log.info("Started polling collection for dataSourceId={} at interval={}ms", dataSourceId, intervalMs);
            }
        } catch (Exception e) {
            log.error("Failed to start collection for dataSourceId={}", dataSourceId, e);
            statusMap.put(dataSourceId, CollectionStatus.ERROR);
            adapterFactory.close(dataSourceId);
            throw new RuntimeException("Failed to start collection: " + e.getMessage(), e);
        }
    }

    public void stopCollection(Long dataSourceId) {
        ScheduledFuture<?> future = scheduledTasks.remove(dataSourceId);
        if (future != null) {
            future.cancel(false);
        }
        adapterFactory.close(dataSourceId);
        statusMap.put(dataSourceId, CollectionStatus.STOPPED);
        log.info("Stopped collection for dataSourceId={}", dataSourceId);
    }

    private void collectOnce(DataSource dataSource, List<DataPoint> dataPoints, ProtocolAdapterPort adapter) {
        LocalDateTime startTime = LocalDateTime.now();
        int successCount = 0;
        int failureCount = 0;
        List<CollectedData> batch = new ArrayList<>();

        for (DataPoint point : dataPoints) {
            try {
                String value = adapter.readValue(point);
                CollectedData cd = buildCollectedData(point, value);
                batch.add(cd);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to collect data for point {}: {}", point.getTagName(), e.getMessage());
                failureCount++;
            }
        }

        if (!batch.isEmpty()) {
            try {
                collectedDataRepo.saveBatch(batch);
            } catch (Exception e) {
                log.error("Failed to save batch collected data", e);
            }
        }

        // Log task result
        TaskStatus taskStatus = failureCount == 0 ? TaskStatus.SUCCESS
                : successCount == 0 ? TaskStatus.FAILURE
                : TaskStatus.PARTIAL;

        CollectionTaskLog taskLog = CollectionTaskLog.builder()
                .dataSourceId(dataSource.getId())
                .taskName("collection-" + dataSource.getName())
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .status(taskStatus)
                .successCount(successCount)
                .failureCount(failureCount)
                .build();

        try {
            taskLogRepo.save(taskLog);
        } catch (Exception e) {
            log.error("Failed to save task log", e);
        }
    }

    private CollectedData buildCollectedData(DataPoint point, String value) {
        CollectedData.CollectedDataBuilder builder = CollectedData.builder()
                .dataPointId(point.getId())
                .tagName(point.getTagName())
                .dataSourceId(point.getDataSourceId())
                .rawValue(value)
                .collectedAt(LocalDateTime.now())
                .quality(0); // 0=good

        if (value != null) {
            try {
                builder.numericValue(Double.parseDouble(value));
            } catch (NumberFormatException ignored) {
                // Not a number
            }
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                builder.booleanValue(Boolean.parseBoolean(value));
            }
            builder.stringValue(value);
        } else {
            builder.quality(2); // 2=bad
        }

        return builder.build();
    }

    public Map<Long, CollectionStatus> getStatus() {
        return Map.copyOf(statusMap);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down CollectionService, stopping all active collections...");
        for (Long dataSourceId : new ArrayList<>(scheduledTasks.keySet())) {
            stopCollection(dataSourceId);
        }
        scheduler.shutdown();
    }
}

package com.htw.collector.domain.repository;

import com.htw.collector.domain.model.CollectedData;

import java.time.Instant;
import java.util.List;

public interface CollectedDataRepository {
    void batchInsert(List<CollectedData> records);
    List<CollectedData> findByDataPointIdAndTimeRange(Long dataPointId, Instant from, Instant to);
    long countByDataPointId(Long dataPointId);
}

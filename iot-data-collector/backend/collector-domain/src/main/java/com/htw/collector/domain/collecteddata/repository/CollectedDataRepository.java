package com.htw.collector.domain.collecteddata.repository;

import com.htw.collector.domain.collecteddata.CollectedData;

import java.util.List;

public interface CollectedDataRepository {
    CollectedData save(CollectedData collectedData);
    void saveBatch(List<CollectedData> dataList);
    List<CollectedData> findLatestByDataPointId(Long dataPointId, int limit);
    long countByDateRange(Long dataPointId, java.time.LocalDateTime from, java.time.LocalDateTime to);
}

package com.htw.collector.domain.repository;

import com.htw.collector.domain.model.CollectedReading;
import java.time.Instant;
import java.util.List;

public interface CollectedReadingRepository {
    /** 批量写入，减少 DB 往返 */
    void saveBatch(List<CollectedReading> readings);

    /** 按点位查询历史 */
    List<CollectedReading> findByDataPointId(Long dataPointId, Instant from, Instant to);
}

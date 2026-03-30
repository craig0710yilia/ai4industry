package com.htw.collector.app.service;

import com.htw.collector.domain.model.DataPoint;

/**
 * Port (interface) for the collection engine. Implementation lives in collector-infrastructure.
 */
public interface CollectionEngineService {
    void scheduleDataPoint(DataPoint dataPoint);
    void unscheduleDataPoint(Long dataPointId);
    void stopDataSource(Long dataSourceId);
    void testConnection(Long dataSourceId);
    void startAll();
}

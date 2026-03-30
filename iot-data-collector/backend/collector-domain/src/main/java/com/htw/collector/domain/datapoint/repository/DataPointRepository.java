package com.htw.collector.domain.datapoint.repository;

import com.htw.collector.domain.datapoint.DataPoint;

import java.util.List;
import java.util.Optional;

public interface DataPointRepository {
    Optional<DataPoint> findById(Long id);
    List<DataPoint> findByDataSourceId(Long dataSourceId);
    List<DataPoint> findAllEnabled();
    List<DataPoint> findEnabledByDataSourceId(Long dataSourceId);
    DataPoint save(DataPoint dataPoint);
    DataPoint update(DataPoint dataPoint);
    void deleteById(Long id);
}

package com.htw.collector.domain.repository;

import com.htw.collector.domain.model.DataPoint;
import java.util.List;
import java.util.Optional;

public interface DataPointRepository {
    DataPoint save(DataPoint dataPoint);
    Optional<DataPoint> findById(Long id);
    List<DataPoint> findAll();
    List<DataPoint> findByDataSourceId(Long dataSourceId);
    List<DataPoint> findEnabledByDataSourceId(Long dataSourceId);
    void deleteById(Long id);
}

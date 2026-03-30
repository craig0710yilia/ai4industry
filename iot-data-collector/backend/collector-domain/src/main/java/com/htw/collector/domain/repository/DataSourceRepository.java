package com.htw.collector.domain.repository;

import com.htw.collector.domain.model.DataSource;
import java.util.List;
import java.util.Optional;

public interface DataSourceRepository {
    DataSource save(DataSource dataSource);
    Optional<DataSource> findById(Long id);
    List<DataSource> findAll();
    List<DataSource> findAllEnabled();
    void deleteById(Long id);
}

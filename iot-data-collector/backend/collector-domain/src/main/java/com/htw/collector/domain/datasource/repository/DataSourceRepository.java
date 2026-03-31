package com.htw.collector.domain.datasource.repository;

import com.htw.collector.domain.datasource.DataSource;

import java.util.List;
import java.util.Optional;

public interface DataSourceRepository {
    Optional<DataSource> findById(Long id);
    List<DataSource> findAll();
    List<DataSource> findAllEnabled();
    DataSource save(DataSource dataSource);
    DataSource update(DataSource dataSource);
    void deleteById(Long id);
}

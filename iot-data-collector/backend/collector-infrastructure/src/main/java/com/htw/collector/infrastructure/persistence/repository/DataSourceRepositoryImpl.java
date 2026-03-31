package com.htw.collector.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.htw.collector.domain.datasource.DataSource;
import com.htw.collector.domain.datasource.repository.DataSourceRepository;
import com.htw.collector.infrastructure.persistence.converter.DataSourceConverter;
import com.htw.collector.infrastructure.persistence.entity.DataSourceDO;
import com.htw.collector.infrastructure.persistence.mapper.DataSourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryImpl implements DataSourceRepository {

    private final DataSourceMapper dataSourceMapper;
    private final DataSourceConverter converter;

    @Override
    public Optional<DataSource> findById(Long id) {
        DataSourceDO entity = dataSourceMapper.selectById(id);
        return Optional.ofNullable(entity).map(converter::toDomain);
    }

    @Override
    public List<DataSource> findAll() {
        List<DataSourceDO> list = dataSourceMapper.selectList(new LambdaQueryWrapper<>());
        return converter.toDomainList(list);
    }

    @Override
    public List<DataSource> findAllEnabled() {
        LambdaQueryWrapper<DataSourceDO> wrapper = new LambdaQueryWrapper<DataSourceDO>()
                .eq(DataSourceDO::getEnabled, true);
        return converter.toDomainList(dataSourceMapper.selectList(wrapper));
    }

    @Override
    public DataSource save(DataSource dataSource) {
        DataSourceDO entity = converter.toEntity(dataSource);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        dataSourceMapper.insert(entity);
        return converter.toDomain(entity);
    }

    @Override
    public DataSource update(DataSource dataSource) {
        DataSourceDO entity = converter.toEntity(dataSource);
        entity.setUpdatedAt(LocalDateTime.now());
        dataSourceMapper.updateById(entity);
        return converter.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {
        dataSourceMapper.deleteById(id);
    }
}

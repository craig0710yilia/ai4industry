package com.htw.collector.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.htw.collector.domain.datapoint.DataPoint;
import com.htw.collector.domain.datapoint.repository.DataPointRepository;
import com.htw.collector.infrastructure.persistence.converter.DataPointConverter;
import com.htw.collector.infrastructure.persistence.entity.DataPointDO;
import com.htw.collector.infrastructure.persistence.mapper.DataPointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DataPointRepositoryImpl implements DataPointRepository {

    private final DataPointMapper dataPointMapper;
    private final DataPointConverter converter;

    @Override
    public Optional<DataPoint> findById(Long id) {
        return Optional.ofNullable(dataPointMapper.selectById(id)).map(converter::toDomain);
    }

    @Override
    public List<DataPoint> findByDataSourceId(Long dataSourceId) {
        LambdaQueryWrapper<DataPointDO> wrapper = new LambdaQueryWrapper<DataPointDO>()
                .eq(DataPointDO::getDataSourceId, dataSourceId);
        return converter.toDomainList(dataPointMapper.selectList(wrapper));
    }

    @Override
    public List<DataPoint> findAllEnabled() {
        LambdaQueryWrapper<DataPointDO> wrapper = new LambdaQueryWrapper<DataPointDO>()
                .eq(DataPointDO::getEnabled, true);
        return converter.toDomainList(dataPointMapper.selectList(wrapper));
    }

    @Override
    public List<DataPoint> findEnabledByDataSourceId(Long dataSourceId) {
        LambdaQueryWrapper<DataPointDO> wrapper = new LambdaQueryWrapper<DataPointDO>()
                .eq(DataPointDO::getDataSourceId, dataSourceId)
                .eq(DataPointDO::getEnabled, true);
        return converter.toDomainList(dataPointMapper.selectList(wrapper));
    }

    @Override
    public DataPoint save(DataPoint dataPoint) {
        DataPointDO entity = converter.toEntity(dataPoint);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        dataPointMapper.insert(entity);
        return converter.toDomain(entity);
    }

    @Override
    public DataPoint update(DataPoint dataPoint) {
        DataPointDO entity = converter.toEntity(dataPoint);
        entity.setUpdatedAt(LocalDateTime.now());
        dataPointMapper.updateById(entity);
        return converter.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {
        dataPointMapper.deleteById(id);
    }
}

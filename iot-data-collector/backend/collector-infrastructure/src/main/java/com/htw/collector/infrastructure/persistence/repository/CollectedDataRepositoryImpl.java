package com.htw.collector.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.htw.collector.domain.collecteddata.CollectedData;
import com.htw.collector.domain.collecteddata.repository.CollectedDataRepository;
import com.htw.collector.infrastructure.persistence.converter.CollectedDataConverter;
import com.htw.collector.infrastructure.persistence.entity.CollectedDataDO;
import com.htw.collector.infrastructure.persistence.mapper.CollectedDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CollectedDataRepositoryImpl implements CollectedDataRepository {

    private final CollectedDataMapper collectedDataMapper;
    private final CollectedDataConverter converter;

    @Override
    public CollectedData save(CollectedData collectedData) {
        CollectedDataDO entity = converter.toEntity(collectedData);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        collectedDataMapper.insert(entity);
        return converter.toDomain(entity);
    }

    @Override
    public void saveBatch(List<CollectedData> dataList) {
        if (dataList == null || dataList.isEmpty()) return;
        List<CollectedDataDO> entities = converter.toEntityList(dataList);
        LocalDateTime now = LocalDateTime.now();
        entities.forEach(e -> {
            if (e.getCreatedAt() == null) e.setCreatedAt(now);
        });
        // Use batch insert via MyBatis-Plus
        for (CollectedDataDO entity : entities) {
            collectedDataMapper.insert(entity);
        }
    }

    @Override
    public List<CollectedData> findLatestByDataPointId(Long dataPointId, int limit) {
        List<CollectedDataDO> list = collectedDataMapper.selectLatestByDataPointId(dataPointId, limit);
        return converter.toDomainList(list);
    }

    @Override
    public long countByDateRange(Long dataPointId, LocalDateTime from, LocalDateTime to) {
        LambdaQueryWrapper<CollectedDataDO> wrapper = new LambdaQueryWrapper<CollectedDataDO>()
                .eq(CollectedDataDO::getDataPointId, dataPointId)
                .between(CollectedDataDO::getCollectedAt, from, to);
        return collectedDataMapper.selectCount(wrapper);
    }
}

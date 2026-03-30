package com.htw.collector.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.htw.collector.domain.task.CollectionTaskLog;
import com.htw.collector.domain.task.repository.CollectionTaskLogRepository;
import com.htw.collector.infrastructure.persistence.converter.CollectionTaskLogConverter;
import com.htw.collector.infrastructure.persistence.entity.CollectionTaskLogDO;
import com.htw.collector.infrastructure.persistence.mapper.CollectionTaskLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CollectionTaskLogRepositoryImpl implements CollectionTaskLogRepository {

    private final CollectionTaskLogMapper collectionTaskLogMapper;
    private final CollectionTaskLogConverter converter;

    @Override
    public CollectionTaskLog save(CollectionTaskLog log) {
        CollectionTaskLogDO entity = converter.toEntity(log);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        collectionTaskLogMapper.insert(entity);
        return converter.toDomain(entity);
    }

    @Override
    public List<CollectionTaskLog> findRecentLogs(int limit) {
        LambdaQueryWrapper<CollectionTaskLogDO> wrapper = new LambdaQueryWrapper<CollectionTaskLogDO>()
                .orderByDesc(CollectionTaskLogDO::getCreatedAt)
                .last("LIMIT " + limit);
        return converter.toDomainList(collectionTaskLogMapper.selectList(wrapper));
    }
}

package com.htw.collector.infrastructure.persistence.converter;

import com.htw.collector.domain.collecteddata.CollectedData;
import com.htw.collector.infrastructure.persistence.entity.CollectedDataDO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CollectedDataConverter {
    CollectedData toDomain(CollectedDataDO collectedDataDO);
    CollectedDataDO toEntity(CollectedData collectedData);
    List<CollectedData> toDomainList(List<CollectedDataDO> list);
    List<CollectedDataDO> toEntityList(List<CollectedData> list);
}

package com.htw.collector.infrastructure.persistence.converter;

import com.htw.collector.domain.datapoint.DataPoint;
import com.htw.collector.domain.enums.DataPointType;
import com.htw.collector.infrastructure.persistence.entity.DataPointDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DataPointConverter {

    @Mapping(target = "dataType", source = "dataType", qualifiedByName = "stringToDataPointType")
    DataPoint toDomain(DataPointDO dataPointDO);

    @Mapping(target = "dataType", source = "dataType", qualifiedByName = "dataPointTypeToString")
    @Mapping(target = "deleted", ignore = true)
    DataPointDO toEntity(DataPoint dataPoint);

    List<DataPoint> toDomainList(List<DataPointDO> list);

    List<DataPointDO> toEntityList(List<DataPoint> list);

    @Named("stringToDataPointType")
    default DataPointType stringToDataPointType(String value) {
        if (value == null) return null;
        return DataPointType.valueOf(value);
    }

    @Named("dataPointTypeToString")
    default String dataPointTypeToString(DataPointType dataPointType) {
        if (dataPointType == null) return null;
        return dataPointType.name();
    }
}

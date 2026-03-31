package com.htw.collector.infrastructure.persistence.converter;

import com.htw.collector.domain.datasource.DataSource;
import com.htw.collector.domain.enums.ProtocolType;
import com.htw.collector.infrastructure.persistence.entity.DataSourceDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DataSourceConverter {

    @Mapping(target = "protocolType", source = "protocolType", qualifiedByName = "stringToProtocolType")
    DataSource toDomain(DataSourceDO dataSourceDO);

    @Mapping(target = "protocolType", source = "protocolType", qualifiedByName = "protocolTypeToString")
    @Mapping(target = "deleted", ignore = true)
    DataSourceDO toEntity(DataSource dataSource);

    List<DataSource> toDomainList(List<DataSourceDO> list);

    List<DataSourceDO> toEntityList(List<DataSource> list);

    @Named("stringToProtocolType")
    default ProtocolType stringToProtocolType(String value) {
        if (value == null) return null;
        return ProtocolType.valueOf(value);
    }

    @Named("protocolTypeToString")
    default String protocolTypeToString(ProtocolType protocolType) {
        if (protocolType == null) return null;
        return protocolType.name();
    }
}

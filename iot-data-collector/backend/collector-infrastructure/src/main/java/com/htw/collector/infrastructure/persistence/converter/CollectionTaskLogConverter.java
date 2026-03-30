package com.htw.collector.infrastructure.persistence.converter;

import com.htw.collector.domain.enums.TaskStatus;
import com.htw.collector.domain.task.CollectionTaskLog;
import com.htw.collector.infrastructure.persistence.entity.CollectionTaskLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CollectionTaskLogConverter {

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToTaskStatus")
    CollectionTaskLog toDomain(CollectionTaskLogDO collectionTaskLogDO);

    @Mapping(target = "status", source = "status", qualifiedByName = "taskStatusToString")
    CollectionTaskLogDO toEntity(CollectionTaskLog collectionTaskLog);

    List<CollectionTaskLog> toDomainList(List<CollectionTaskLogDO> list);

    @Named("stringToTaskStatus")
    default TaskStatus stringToTaskStatus(String value) {
        if (value == null) return null;
        return TaskStatus.valueOf(value);
    }

    @Named("taskStatusToString")
    default String taskStatusToString(TaskStatus taskStatus) {
        if (taskStatus == null) return null;
        return taskStatus.name();
    }
}

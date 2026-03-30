package com.htw.collector.domain.task.repository;

import com.htw.collector.domain.task.CollectionTaskLog;

import java.util.List;

public interface CollectionTaskLogRepository {
    CollectionTaskLog save(CollectionTaskLog log);
    List<CollectionTaskLog> findRecentLogs(int limit);
}

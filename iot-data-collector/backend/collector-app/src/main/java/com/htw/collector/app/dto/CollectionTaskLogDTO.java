package com.htw.collector.app.dto;

import com.htw.collector.domain.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollectionTaskLogDTO {
    private Long id;
    private Long dataSourceId;
    private String taskName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TaskStatus status;
    private Integer successCount;
    private Integer failureCount;
    private String errorMessage;
    private LocalDateTime createdAt;
}

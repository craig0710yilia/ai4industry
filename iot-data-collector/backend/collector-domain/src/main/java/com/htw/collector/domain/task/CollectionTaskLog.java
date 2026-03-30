package com.htw.collector.domain.task;

import com.htw.collector.domain.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionTaskLog {
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

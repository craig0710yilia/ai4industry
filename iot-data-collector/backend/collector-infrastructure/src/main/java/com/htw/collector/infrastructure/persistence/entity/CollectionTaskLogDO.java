package com.htw.collector.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_collection_task_log")
public class CollectionTaskLogDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dataSourceId;
    private String taskName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer successCount;
    private Integer failureCount;
    private String errorMessage;
    private LocalDateTime createdAt;
}

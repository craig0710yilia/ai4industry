package com.htw.collector.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_collected_data")
public class CollectedDataDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dataPointId;
    private String tagName;
    private Long dataSourceId;
    private String rawValue;
    private Double numericValue;
    private String stringValue;
    private Boolean booleanValue;
    private Integer quality;
    private LocalDateTime collectedAt;
    private LocalDateTime createdAt;
}

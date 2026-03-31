package com.htw.collector.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_data_point")
public class DataPointDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dataSourceId;
    private String tagName;
    private String displayName;
    private String description;
    private String dataType;
    private String opcuaNodeId;
    private String modbusRegisterType;
    private Integer modbusRegisterAddress;
    private String mqttTopic;
    private String mqttJsonPath;
    private Integer sampleIntervalMs;
    private Boolean enabled;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

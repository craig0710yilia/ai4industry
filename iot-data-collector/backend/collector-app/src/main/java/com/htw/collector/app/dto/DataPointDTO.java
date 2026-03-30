package com.htw.collector.app.dto;

import com.htw.collector.domain.enums.DataPointType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DataPointDTO {
    private Long id;
    private Long dataSourceId;
    private String tagName;
    private String displayName;
    private String description;
    private DataPointType dataType;
    private String opcuaNodeId;
    private String modbusRegisterType;
    private Integer modbusRegisterAddress;
    private String mqttTopic;
    private String mqttJsonPath;
    private Integer sampleIntervalMs;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

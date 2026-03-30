package com.htw.collector.app.dto;

import com.htw.collector.domain.enums.DataPointType;
import lombok.Data;

@Data
public class DataPointUpdateCmd {
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
}

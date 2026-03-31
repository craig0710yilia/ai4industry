package com.htw.collector.app.dto;

import com.htw.collector.domain.enums.DataPointType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DataPointCreateCmd {
    @NotNull(message = "Data source ID is required")
    private Long dataSourceId;

    @NotBlank(message = "Tag name is required")
    private String tagName;

    private String displayName;
    private String description;

    @NotNull(message = "Data type is required")
    private DataPointType dataType;

    private String opcuaNodeId;
    private String modbusRegisterType;
    private Integer modbusRegisterAddress;
    private String mqttTopic;
    private String mqttJsonPath;
    private Integer sampleIntervalMs = 1000;
    private Boolean enabled = true;
}

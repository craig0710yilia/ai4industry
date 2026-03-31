package com.htw.collector.domain.datapoint;

import com.htw.collector.domain.enums.DataPointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPoint {
    private Long id;
    private Long dataSourceId;
    private String tagName;
    private String displayName;
    private String description;
    private DataPointType dataType;
    private String opcuaNodeId;
    /** COIL / DISCRETE / INPUT_REGISTER / HOLDING_REGISTER */
    private String modbusRegisterType;
    private Integer modbusRegisterAddress;
    private String mqttTopic;
    private String mqttJsonPath;
    @Builder.Default
    private Integer sampleIntervalMs = 1000;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

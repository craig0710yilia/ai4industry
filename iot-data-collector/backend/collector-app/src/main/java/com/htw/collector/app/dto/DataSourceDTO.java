package com.htw.collector.app.dto;

import com.htw.collector.domain.enums.CollectionStatus;
import com.htw.collector.domain.enums.ProtocolType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DataSourceDTO {
    private Long id;
    private String name;
    private ProtocolType protocolType;
    private String host;
    private Integer port;
    private String opcuaEndpointUrl;
    private String opcuaSecurityMode;
    private Integer modbusSlaveId;
    private String mqttBrokerUrl;
    private String mqttClientId;
    private String mqttUsername;
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CollectionStatus status;
}

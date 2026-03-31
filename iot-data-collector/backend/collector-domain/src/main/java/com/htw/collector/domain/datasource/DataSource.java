package com.htw.collector.domain.datasource;

import com.htw.collector.domain.enums.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSource {
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
    private String mqttPassword;
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

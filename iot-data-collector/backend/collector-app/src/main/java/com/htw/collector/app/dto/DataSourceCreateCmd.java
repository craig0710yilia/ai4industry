package com.htw.collector.app.dto;

import com.htw.collector.domain.enums.ProtocolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DataSourceCreateCmd {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Protocol type is required")
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
    private Boolean enabled = true;
}

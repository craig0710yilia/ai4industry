package com.htw.collector.app.dto;

import com.htw.collector.domain.enums.ProtocolType;
import lombok.Data;

@Data
public class DataSourceUpdateCmd {
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
}

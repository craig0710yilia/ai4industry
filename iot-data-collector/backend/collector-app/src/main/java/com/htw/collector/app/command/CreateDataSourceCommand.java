package com.htw.collector.app.command;

import com.htw.collector.domain.enums.ProtocolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDataSourceCommand {
    @NotBlank
    private String name;
    @NotNull
    private ProtocolType protocol;
    @NotBlank
    private String host;
    private int port;
    private String endpointUrl;
    private String securityPolicy;
    private String clientCertificate;
    private String clientPrivateKey;
    private int unitId = 1;
    private String clientId;
    private String mqttUsername;
    private String mqttPassword;
    private String mqttTopics;
    private String username;
    private String password;
}

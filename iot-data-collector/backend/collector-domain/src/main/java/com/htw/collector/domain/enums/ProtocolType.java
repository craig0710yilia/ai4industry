package com.htw.collector.domain.enums;

import lombok.Getter;

@Getter
public enum ProtocolType {
    OPCUA("OPCUA", "OPC-UA Protocol"),
    MODBUS_TCP("MODBUS_TCP", "Modbus TCP Protocol"),
    MQTT("MQTT", "MQTT Protocol");

    private final String code;
    private final String description;

    ProtocolType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

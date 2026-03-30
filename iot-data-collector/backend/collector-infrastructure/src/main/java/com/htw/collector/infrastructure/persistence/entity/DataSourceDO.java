package com.htw.collector.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_data_source")
public class DataSourceDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** Protocol type: OPC_UA, MODBUS_TCP, MQTT */
    private String protocol;

    private String host;

    private Integer port;

    /** OPC-UA endpoint URL or MQTT broker URI */
    private String endpointUrl;

    private String securityPolicy;

    private String clientCertificate;

    private String clientPrivateKey;

    /** Modbus unit/slave ID */
    private Integer unitId;

    /** MQTT client ID */
    private String clientId;

    private String mqttUsername;

    private String mqttPassword;

    /** MQTT topics (comma-separated) */
    private String mqttTopics;

    private String username;

    private String password;

    private String description;

    private Boolean enabled;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

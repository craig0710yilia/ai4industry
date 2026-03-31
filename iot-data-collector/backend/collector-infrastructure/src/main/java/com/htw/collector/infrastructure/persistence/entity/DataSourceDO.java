package com.htw.collector.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_data_source")
public class DataSourceDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String protocolType;
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
    @TableLogic
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

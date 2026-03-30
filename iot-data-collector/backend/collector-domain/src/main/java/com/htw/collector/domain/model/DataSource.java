package com.htw.collector.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 数据连接源配置（OPC-UA / Modbus TCP / MQTT Broker）
 */
@Data
@Accessors(chain = true)
public class DataSource {

    private Long id;

    /** 显示名称，如 "注塑机1号 OPC-UA" */
    private String name;

    /** 协议类型 */
    private ProtocolType protocol;

    /** 目标主机/IP */
    private String host;

    /** 目标端口 */
    private Integer port;

    /** OPC-UA: endpoint URL；MQTT: broker URI；Modbus: 留空 */
    private String endpointUrl;

    /** OPC-UA: 用户名；MQTT: clientId 前缀 */
    private String username;

    /** OPC-UA / MQTT 密码（存储时需加密） */
    private String password;

    /** OPC-UA 证书文件路径（可选） */
    private String certPath;

    /** 连接超时（毫秒），默认 5000 */
    private Integer connectTimeoutMs;

    /** 是否启用 */
    private Boolean enabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

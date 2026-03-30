package com.htw.collector.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 点位配置：描述一个需要采集的测量点
 */
@Data
@Accessors(chain = true)
public class DataPoint {

    private Long id;

    /** 所属连接源 */
    private Long dataSourceId;

    /** 点位业务标签名，如 "CavityTemp1" */
    private String tagName;

    /** 描述，如 "1号腔体温度" */
    private String description;

    /** OPC-UA: NodeId（如 ns=2;i=1001）；Modbus: 寄存器地址（如 40001）；MQTT: topic */
    private String address;

    /** Modbus 功能码（仅 Modbus 使用，如 3=保持寄存器, 1=线圈） */
    private Integer modbusFunction;

    /** 数据类型 */
    private DataType dataType;

    /** 工程单位，如 "℃"、"MPa" */
    private String unit;

    /** 采集间隔（毫秒），最小 1000（即 1Hz） */
    private Integer intervalMs;

    /** 是否启用变化值采集（COV）而非全量采集 */
    private Boolean covEnabled;

    /** COV 死区（用于过滤微小波动），仅 covEnabled=true 时有效 */
    private Double covDeadband;

    /** 是否启用 */
    private Boolean enabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

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
@TableName("t_data_point")
public class DataPointDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dataSourceId;

    private String tagName;

    private String displayName;

    private String description;

    /** Data type: BOOLEAN, INT16, INT32, INT64, FLOAT, DOUBLE, STRING */
    private String dataType;

    /** OPC-UA: NodeId (e.g. ns=2;i=1001) */
    private String address;

    /** Modbus function code (1=coil, 3=holding register, etc.) */
    private Integer modbusFunction;

    private String unit;

    /** Sample interval in milliseconds */
    private Integer intervalMs;

    private Boolean covEnabled;

    private Double covDeadband;

    private Boolean enabled;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

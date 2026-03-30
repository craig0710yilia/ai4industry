package com.htw.collector.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("t_collected_data")
public class CollectedDataDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dataPointId;

    private String tagName;

    private Long dataSourceId;

    /** Raw value as string */
    private String value;

    /** Quality flag: true = good */
    private Boolean good;

    private LocalDateTime collectedAt;

    private LocalDateTime createdAt;
}

package com.htw.collector.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * 一条采集到的原始读数（内存中流转，批量写库）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectedReading {

    /** 采集时间戳（UTC） */
    private Instant timestamp;

    /** 点位ID */
    private Long dataPointId;

    /** 点位标签名（冗余，方便日志） */
    private String tagName;

    /** 字符串化的值 */
    private String value;

    /** 采集是否成功 */
    private boolean success;

    /** 失败原因（可选） */
    private String errorMessage;
}

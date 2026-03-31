package com.htw.collector.domain.collecteddata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectedData {
    private Long id;
    private Long dataPointId;
    private String tagName;
    private Long dataSourceId;
    private String rawValue;
    private Double numericValue;
    private String stringValue;
    private Boolean booleanValue;
    /** 0=good, 1=uncertain, 2=bad */
    private Integer quality;
    private LocalDateTime collectedAt;
    private LocalDateTime createdAt;
}

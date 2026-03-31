package com.htw.collector.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollectedDataDTO {
    private Long id;
    private String tagName;
    private String displayName;
    private Long dataSourceId;
    private String rawValue;
    private Double numericValue;
    private String stringValue;
    private Boolean booleanValue;
    private Integer quality;
    private LocalDateTime collectedAt;
}

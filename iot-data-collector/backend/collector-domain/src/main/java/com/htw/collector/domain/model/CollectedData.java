package com.htw.collector.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * A single collected measurement value.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectedData {

    private Long id;
    private Long dataPointId;
    private String tagName;
    /** ISO-8601 timestamp from the source device (or collection time if unavailable) */
    private Instant timestamp;
    /** Serialized value as string for uniform storage */
    private String value;
    /** Quality flag: true = good quality / fresh value */
    private boolean good;
}

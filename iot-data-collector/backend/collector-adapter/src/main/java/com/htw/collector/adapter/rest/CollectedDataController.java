package com.htw.collector.adapter.rest;

import com.htw.collector.domain.model.CollectedData;
import com.htw.collector.domain.repository.CollectedDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/collected-data")
@RequiredArgsConstructor
public class CollectedDataController {

    private final CollectedDataRepository collectedDataRepository;

    @GetMapping
    public List<CollectedData> query(
            @RequestParam Long dataPointId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return collectedDataRepository.findByDataPointIdAndTimeRange(dataPointId, from, to);
    }
}

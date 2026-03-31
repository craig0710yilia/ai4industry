package com.htw.collector.adapter.web;

import com.htw.collector.adapter.web.common.Result;
import com.htw.collector.app.dto.CollectedDataDTO;
import com.htw.collector.app.dto.PageResult;
import com.htw.collector.app.service.DataQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/collected-data")
@RequiredArgsConstructor
public class DataQueryController {

    private final DataQueryService dataQueryService;

    @GetMapping("/latest")
    public Result<List<CollectedDataDTO>> latestAllPoints(@RequestParam Long dataSourceId) {
        return Result.success(dataQueryService.queryLatestAllPoints(dataSourceId));
    }

    @GetMapping("/point/{pointId}/latest")
    public Result<List<CollectedDataDTO>> latestByPoint(@PathVariable Long pointId,
                                                         @RequestParam(defaultValue = "50") int limit) {
        return Result.success(dataQueryService.queryLatest(pointId, limit));
    }

    @GetMapping("/point/{pointId}")
    public Result<PageResult<CollectedDataDTO>> queryByTimeRange(
            @PathVariable Long pointId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size) {
        return Result.success(dataQueryService.queryByTimeRange(pointId, from, to, page, size));
    }
}

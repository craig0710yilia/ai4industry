package com.htw.collector.adapter.web;

import com.htw.collector.adapter.web.common.Result;
import com.htw.collector.app.dto.DataPointCreateCmd;
import com.htw.collector.app.dto.DataPointDTO;
import com.htw.collector.app.dto.DataPointUpdateCmd;
import com.htw.collector.app.service.DataPointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/data-points")
@RequiredArgsConstructor
public class DataPointController {

    private final DataPointService dataPointService;

    @GetMapping
    public Result<List<DataPointDTO>> listByDataSource(@RequestParam Long dataSourceId) {
        return Result.success(dataPointService.listByDataSourceId(dataSourceId));
    }

    @GetMapping("/{id}")
    public Result<DataPointDTO> getById(@PathVariable Long id) {
        return Result.success(dataPointService.getById(id));
    }

    @PostMapping
    public Result<DataPointDTO> create(@Valid @RequestBody DataPointCreateCmd cmd) {
        return Result.success(dataPointService.create(cmd));
    }

    @PutMapping("/{id}")
    public Result<DataPointDTO> update(@PathVariable Long id, @RequestBody DataPointUpdateCmd cmd) {
        return Result.success(dataPointService.update(id, cmd));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dataPointService.deleteById(id);
        return Result.success();
    }
}

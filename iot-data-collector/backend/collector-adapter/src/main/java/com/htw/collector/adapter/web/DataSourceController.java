package com.htw.collector.adapter.web;

import com.htw.collector.adapter.web.common.Result;
import com.htw.collector.app.dto.DataSourceCreateCmd;
import com.htw.collector.app.dto.DataSourceDTO;
import com.htw.collector.app.dto.DataSourceUpdateCmd;
import com.htw.collector.app.service.CollectionService;
import com.htw.collector.app.service.DataSourceService;
import com.htw.collector.domain.enums.CollectionStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/data-sources")
@Validated
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceService dataSourceService;
    private final CollectionService collectionService;

    @GetMapping
    public Result<List<DataSourceDTO>> listAll() {
        List<DataSourceDTO> list = dataSourceService.listAll();
        // Enrich with collection status
        Map<Long, CollectionStatus> statusMap = collectionService.getStatus();
        list.forEach(dto -> dto.setStatus(statusMap.getOrDefault(dto.getId(), CollectionStatus.STOPPED)));
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<DataSourceDTO> getById(@PathVariable Long id) {
        DataSourceDTO dto = dataSourceService.getById(id);
        Map<Long, CollectionStatus> statusMap = collectionService.getStatus();
        dto.setStatus(statusMap.getOrDefault(id, CollectionStatus.STOPPED));
        return Result.success(dto);
    }

    @PostMapping
    public Result<DataSourceDTO> create(@Valid @RequestBody DataSourceCreateCmd cmd) {
        return Result.success(dataSourceService.create(cmd));
    }

    @PutMapping("/{id}")
    public Result<DataSourceDTO> update(@PathVariable Long id, @RequestBody DataSourceUpdateCmd cmd) {
        return Result.success(dataSourceService.update(id, cmd));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dataSourceService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/{id}/start")
    public Result<Void> startCollection(@PathVariable Long id) {
        collectionService.startCollection(id);
        return Result.success();
    }

    @PostMapping("/{id}/stop")
    public Result<Void> stopCollection(@PathVariable Long id) {
        collectionService.stopCollection(id);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Map<Long, CollectionStatus>> getStatus() {
        return Result.success(collectionService.getStatus());
    }

    @PostMapping("/{id}/test")
    public Result<String> testConnection(@PathVariable Long id) {
        try {
            DataSourceDTO dto = dataSourceService.getById(id);
            // Just return that connection test was initiated
            return Result.success("Connection test initiated for: " + dto.getName());
        } catch (Exception e) {
            return Result.fail("Connection test failed: " + e.getMessage());
        }
    }
}

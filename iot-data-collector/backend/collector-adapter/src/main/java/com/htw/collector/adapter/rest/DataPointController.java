package com.htw.collector.adapter.rest;

import com.htw.collector.app.command.CreateDataPointCommand;
import com.htw.collector.app.service.DataPointService;
import com.htw.collector.domain.model.DataPoint;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data-points")
@RequiredArgsConstructor
public class DataPointController {

    private final DataPointService dataPointService;

    @GetMapping
    public List<DataPoint> listByDataSource(@RequestParam Long dataSourceId) {
        return dataPointService.listByDataSource(dataSourceId);
    }

    @GetMapping("/{id}")
    public DataPoint getById(@PathVariable Long id) {
        return dataPointService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataPoint create(@Valid @RequestBody CreateDataPointCommand cmd) {
        return dataPointService.create(cmd);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        dataPointService.delete(id);
    }
}

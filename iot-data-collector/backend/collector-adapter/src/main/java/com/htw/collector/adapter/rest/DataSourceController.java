package com.htw.collector.adapter.rest;

import com.htw.collector.app.command.CreateDataSourceCommand;
import com.htw.collector.app.service.DataSourceService;
import com.htw.collector.domain.model.DataSource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data-sources")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceService dataSourceService;

    @GetMapping
    public List<DataSource> listAll() {
        return dataSourceService.listAll();
    }

    @GetMapping("/{id}")
    public DataSource getById(@PathVariable Long id) {
        return dataSourceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataSource create(@Valid @RequestBody CreateDataSourceCommand cmd) {
        return dataSourceService.create(cmd);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        dataSourceService.delete(id);
    }

    @PostMapping("/{id}/test-connection")
    public ResponseEntity<Void> testConnection(@PathVariable Long id) {
        dataSourceService.testConnection(id);
        return ResponseEntity.ok().build();
    }
}

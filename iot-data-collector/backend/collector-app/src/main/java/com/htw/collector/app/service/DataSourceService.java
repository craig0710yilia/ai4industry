package com.htw.collector.app.service;

import com.htw.collector.app.command.CreateDataSourceCommand;
import com.htw.collector.domain.model.DataSource;
import com.htw.collector.domain.repository.DataSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceService {

    private final DataSourceRepository dataSourceRepository;
    private final CollectionEngineService collectionEngineService;

    public DataSource create(CreateDataSourceCommand cmd) {
        DataSource ds = new DataSource();
        ds.setName(cmd.getName());
        ds.setProtocol(cmd.getProtocol());
        ds.setHost(cmd.getHost());
        ds.setPort(cmd.getPort());
        ds.setEndpointUrl(cmd.getEndpointUrl());
        ds.setSecurityPolicy(cmd.getSecurityPolicy());
        ds.setClientCertificate(cmd.getClientCertificate());
        ds.setClientPrivateKey(cmd.getClientPrivateKey());
        ds.setUnitId(cmd.getUnitId());
        ds.setClientId(cmd.getClientId());
        ds.setMqttUsername(cmd.getMqttUsername());
        ds.setMqttPassword(cmd.getMqttPassword());
        ds.setMqttTopics(cmd.getMqttTopics());
        ds.setUsername(cmd.getUsername());
        ds.setPassword(cmd.getPassword());
        ds.setCreatedAt(LocalDateTime.now());
        ds.setUpdatedAt(LocalDateTime.now());
        DataSource saved = dataSourceRepository.save(ds);
        log.info("Created data source [id={}] name={} protocol={}", saved.getId(), saved.getName(), saved.getProtocol());
        return saved;
    }

    public List<DataSource> listAll() {
        return dataSourceRepository.findAll();
    }

    public DataSource getById(Long id) {
        return dataSourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DataSource not found: " + id));
    }

    public void delete(Long id) {
        collectionEngineService.stopDataSource(id);
        dataSourceRepository.deleteById(id);
        log.info("Deleted data source id={}", id);
    }

    public void testConnection(Long id) {
        collectionEngineService.testConnection(id);
    }
}

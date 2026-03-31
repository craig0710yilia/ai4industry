package com.htw.collector.app.service;

import com.htw.collector.app.dto.DataSourceCreateCmd;
import com.htw.collector.app.dto.DataSourceDTO;
import com.htw.collector.app.dto.DataSourceUpdateCmd;
import com.htw.collector.domain.datasource.DataSource;
import com.htw.collector.domain.datasource.repository.DataSourceRepository;
import com.htw.collector.domain.datapoint.repository.DataPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DataSourceService {

    private final DataSourceRepository dataSourceRepository;
    private final DataPointRepository dataPointRepository;

    @Transactional(readOnly = true)
    public DataSourceDTO getById(Long id) {
        return dataSourceRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("DataSource not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<DataSourceDTO> listAll() {
        return dataSourceRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DataSourceDTO create(DataSourceCreateCmd cmd) {
        // Check duplicate name
        dataSourceRepository.findAll().stream()
                .filter(ds -> ds.getName().equals(cmd.getName()))
                .findFirst()
                .ifPresent(ds -> {
                    throw new IllegalArgumentException("DataSource with name '" + cmd.getName() + "' already exists");
                });

        DataSource dataSource = DataSource.builder()
                .name(cmd.getName())
                .protocolType(cmd.getProtocolType())
                .host(cmd.getHost())
                .port(cmd.getPort())
                .opcuaEndpointUrl(cmd.getOpcuaEndpointUrl())
                .opcuaSecurityMode(cmd.getOpcuaSecurityMode())
                .modbusSlaveId(cmd.getModbusSlaveId())
                .mqttBrokerUrl(cmd.getMqttBrokerUrl())
                .mqttClientId(cmd.getMqttClientId())
                .mqttUsername(cmd.getMqttUsername())
                .mqttPassword(cmd.getMqttPassword())
                .description(cmd.getDescription())
                .enabled(cmd.getEnabled() != null ? cmd.getEnabled() : true)
                .build();

        return toDTO(dataSourceRepository.save(dataSource));
    }

    public DataSourceDTO update(Long id, DataSourceUpdateCmd cmd) {
        DataSource existing = dataSourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DataSource not found with id: " + id));

        // Check name conflict (if name changed)
        if (cmd.getName() != null && !cmd.getName().equals(existing.getName())) {
            dataSourceRepository.findAll().stream()
                    .filter(ds -> ds.getName().equals(cmd.getName()))
                    .findFirst()
                    .ifPresent(ds -> {
                        throw new IllegalArgumentException("DataSource with name '" + cmd.getName() + "' already exists");
                    });
            existing.setName(cmd.getName());
        }

        if (cmd.getProtocolType() != null) existing.setProtocolType(cmd.getProtocolType());
        if (cmd.getHost() != null) existing.setHost(cmd.getHost());
        if (cmd.getPort() != null) existing.setPort(cmd.getPort());
        if (cmd.getOpcuaEndpointUrl() != null) existing.setOpcuaEndpointUrl(cmd.getOpcuaEndpointUrl());
        if (cmd.getOpcuaSecurityMode() != null) existing.setOpcuaSecurityMode(cmd.getOpcuaSecurityMode());
        if (cmd.getModbusSlaveId() != null) existing.setModbusSlaveId(cmd.getModbusSlaveId());
        if (cmd.getMqttBrokerUrl() != null) existing.setMqttBrokerUrl(cmd.getMqttBrokerUrl());
        if (cmd.getMqttClientId() != null) existing.setMqttClientId(cmd.getMqttClientId());
        if (cmd.getMqttUsername() != null) existing.setMqttUsername(cmd.getMqttUsername());
        if (cmd.getMqttPassword() != null) existing.setMqttPassword(cmd.getMqttPassword());
        if (cmd.getDescription() != null) existing.setDescription(cmd.getDescription());
        if (cmd.getEnabled() != null) existing.setEnabled(cmd.getEnabled());

        return toDTO(dataSourceRepository.update(existing));
    }

    public void deleteById(Long id) {
        dataSourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DataSource not found with id: " + id));

        // Check if there are associated data points
        List<?> dataPoints = dataPointRepository.findByDataSourceId(id);
        if (!dataPoints.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot delete DataSource with id " + id + " because it has " + dataPoints.size() + " data points");
        }

        dataSourceRepository.deleteById(id);
    }

    private DataSourceDTO toDTO(DataSource ds) {
        DataSourceDTO dto = new DataSourceDTO();
        dto.setId(ds.getId());
        dto.setName(ds.getName());
        dto.setProtocolType(ds.getProtocolType());
        dto.setHost(ds.getHost());
        dto.setPort(ds.getPort());
        dto.setOpcuaEndpointUrl(ds.getOpcuaEndpointUrl());
        dto.setOpcuaSecurityMode(ds.getOpcuaSecurityMode());
        dto.setModbusSlaveId(ds.getModbusSlaveId());
        dto.setMqttBrokerUrl(ds.getMqttBrokerUrl());
        dto.setMqttClientId(ds.getMqttClientId());
        dto.setMqttUsername(ds.getMqttUsername());
        dto.setDescription(ds.getDescription());
        dto.setEnabled(ds.getEnabled());
        dto.setCreatedAt(ds.getCreatedAt());
        dto.setUpdatedAt(ds.getUpdatedAt());
        return dto;
    }
}

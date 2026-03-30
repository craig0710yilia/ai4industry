package com.htw.collector.app.service;

import com.htw.collector.app.dto.DataPointCreateCmd;
import com.htw.collector.app.dto.DataPointDTO;
import com.htw.collector.app.dto.DataPointUpdateCmd;
import com.htw.collector.domain.datapoint.DataPoint;
import com.htw.collector.domain.datapoint.repository.DataPointRepository;
import com.htw.collector.domain.datasource.repository.DataSourceRepository;
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
public class DataPointService {

    private final DataPointRepository dataPointRepository;
    private final DataSourceRepository dataSourceRepository;

    @Transactional(readOnly = true)
    public DataPointDTO getById(Long id) {
        return dataPointRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("DataPoint not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<DataPointDTO> listByDataSourceId(Long dataSourceId) {
        return dataPointRepository.findByDataSourceId(dataSourceId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DataPointDTO create(DataPointCreateCmd cmd) {
        // Validate data source exists
        dataSourceRepository.findById(cmd.getDataSourceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "DataSource not found with id: " + cmd.getDataSourceId()));

        // Check tagName uniqueness within dataSource
        dataPointRepository.findByDataSourceId(cmd.getDataSourceId()).stream()
                .filter(dp -> dp.getTagName().equals(cmd.getTagName()))
                .findFirst()
                .ifPresent(dp -> {
                    throw new IllegalArgumentException(
                            "DataPoint with tagName '" + cmd.getTagName() + "' already exists in this DataSource");
                });

        DataPoint dataPoint = DataPoint.builder()
                .dataSourceId(cmd.getDataSourceId())
                .tagName(cmd.getTagName())
                .displayName(cmd.getDisplayName())
                .description(cmd.getDescription())
                .dataType(cmd.getDataType())
                .opcuaNodeId(cmd.getOpcuaNodeId())
                .modbusRegisterType(cmd.getModbusRegisterType())
                .modbusRegisterAddress(cmd.getModbusRegisterAddress())
                .mqttTopic(cmd.getMqttTopic())
                .mqttJsonPath(cmd.getMqttJsonPath())
                .sampleIntervalMs(cmd.getSampleIntervalMs() != null ? cmd.getSampleIntervalMs() : 1000)
                .enabled(cmd.getEnabled() != null ? cmd.getEnabled() : true)
                .build();

        return toDTO(dataPointRepository.save(dataPoint));
    }

    public DataPointDTO update(Long id, DataPointUpdateCmd cmd) {
        DataPoint existing = dataPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DataPoint not found with id: " + id));

        if (cmd.getTagName() != null && !cmd.getTagName().equals(existing.getTagName())) {
            dataPointRepository.findByDataSourceId(existing.getDataSourceId()).stream()
                    .filter(dp -> !dp.getId().equals(id) && dp.getTagName().equals(cmd.getTagName()))
                    .findFirst()
                    .ifPresent(dp -> {
                        throw new IllegalArgumentException(
                                "DataPoint with tagName '" + cmd.getTagName() + "' already exists in this DataSource");
                    });
            existing.setTagName(cmd.getTagName());
        }

        if (cmd.getDisplayName() != null) existing.setDisplayName(cmd.getDisplayName());
        if (cmd.getDescription() != null) existing.setDescription(cmd.getDescription());
        if (cmd.getDataType() != null) existing.setDataType(cmd.getDataType());
        if (cmd.getOpcuaNodeId() != null) existing.setOpcuaNodeId(cmd.getOpcuaNodeId());
        if (cmd.getModbusRegisterType() != null) existing.setModbusRegisterType(cmd.getModbusRegisterType());
        if (cmd.getModbusRegisterAddress() != null) existing.setModbusRegisterAddress(cmd.getModbusRegisterAddress());
        if (cmd.getMqttTopic() != null) existing.setMqttTopic(cmd.getMqttTopic());
        if (cmd.getMqttJsonPath() != null) existing.setMqttJsonPath(cmd.getMqttJsonPath());
        if (cmd.getSampleIntervalMs() != null) existing.setSampleIntervalMs(cmd.getSampleIntervalMs());
        if (cmd.getEnabled() != null) existing.setEnabled(cmd.getEnabled());

        return toDTO(dataPointRepository.update(existing));
    }

    public void deleteById(Long id) {
        dataPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DataPoint not found with id: " + id));
        dataPointRepository.deleteById(id);
    }

    private DataPointDTO toDTO(DataPoint dp) {
        DataPointDTO dto = new DataPointDTO();
        dto.setId(dp.getId());
        dto.setDataSourceId(dp.getDataSourceId());
        dto.setTagName(dp.getTagName());
        dto.setDisplayName(dp.getDisplayName());
        dto.setDescription(dp.getDescription());
        dto.setDataType(dp.getDataType());
        dto.setOpcuaNodeId(dp.getOpcuaNodeId());
        dto.setModbusRegisterType(dp.getModbusRegisterType());
        dto.setModbusRegisterAddress(dp.getModbusRegisterAddress());
        dto.setMqttTopic(dp.getMqttTopic());
        dto.setMqttJsonPath(dp.getMqttJsonPath());
        dto.setSampleIntervalMs(dp.getSampleIntervalMs());
        dto.setEnabled(dp.getEnabled());
        dto.setCreatedAt(dp.getCreatedAt());
        dto.setUpdatedAt(dp.getUpdatedAt());
        return dto;
    }
}

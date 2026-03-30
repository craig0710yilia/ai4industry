package com.htw.collector.app.service;

import com.htw.collector.app.command.CreateDataPointCommand;
import com.htw.collector.domain.model.DataPoint;
import com.htw.collector.domain.repository.DataPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPointService {

    private final DataPointRepository dataPointRepository;
    private final CollectionEngineService collectionEngineService;

    public DataPoint create(CreateDataPointCommand cmd) {
        DataPoint dp = new DataPoint();
        dp.setDataSourceId(cmd.getDataSourceId());
        dp.setTagName(cmd.getTagName());
        dp.setDataType(cmd.getDataType());
        dp.setAddress(cmd.getAddress());
        dp.setIntervalMs(cmd.getIntervalMs());
        dp.setUnit(cmd.getUnit());
        dp.setDescription(cmd.getDescription());
        dp.setEnabled(cmd.isEnabled());
        dp.setCreatedAt(LocalDateTime.now());
        dp.setUpdatedAt(LocalDateTime.now());
        DataPoint saved = dataPointRepository.save(dp);
        if (saved.isEnabled()) {
            collectionEngineService.scheduleDataPoint(saved);
        }
        log.info("Created data point [id={}] tag={}", saved.getId(), saved.getTagName());
        return saved;
    }

    public List<DataPoint> listByDataSource(Long dataSourceId) {
        return dataPointRepository.findByDataSourceId(dataSourceId);
    }

    public DataPoint getById(Long id) {
        return dataPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DataPoint not found: " + id));
    }

    public void delete(Long id) {
        collectionEngineService.unscheduleDataPoint(id);
        dataPointRepository.deleteById(id);
    }
}

package com.htw.collector.app.service;

import com.htw.collector.app.dto.CollectedDataDTO;
import com.htw.collector.app.dto.PageResult;
import com.htw.collector.domain.collecteddata.CollectedData;
import com.htw.collector.domain.collecteddata.repository.CollectedDataRepository;
import com.htw.collector.domain.datapoint.DataPoint;
import com.htw.collector.domain.datapoint.repository.DataPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DataQueryService {

    private final CollectedDataRepository collectedDataRepository;
    private final DataPointRepository dataPointRepository;

    public List<CollectedDataDTO> queryLatest(Long dataPointId, int limit) {
        List<CollectedData> data = collectedDataRepository.findLatestByDataPointId(dataPointId, limit);
        Optional<DataPoint> pointOpt = dataPointRepository.findById(dataPointId);
        String displayName = pointOpt.map(DataPoint::getDisplayName).orElse(null);
        return data.stream()
                .map(cd -> toDTO(cd, displayName))
                .collect(Collectors.toList());
    }

    public PageResult<CollectedDataDTO> queryByTimeRange(Long dataPointId, LocalDateTime from,
                                                          LocalDateTime to, int page, int size) {
        long total = collectedDataRepository.countByDateRange(dataPointId, from, to);
        List<CollectedData> allData = collectedDataRepository.findLatestByDataPointId(dataPointId, (int) total);

        Optional<DataPoint> pointOpt = dataPointRepository.findById(dataPointId);
        String displayName = pointOpt.map(DataPoint::getDisplayName).orElse(null);

        List<CollectedData> filtered = allData.stream()
                .filter(cd -> cd.getCollectedAt() != null
                        && !cd.getCollectedAt().isBefore(from)
                        && !cd.getCollectedAt().isAfter(to))
                .collect(Collectors.toList());

        int fromIndex = Math.min((page - 1) * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());
        List<CollectedData> pageData = filtered.subList(fromIndex, toIndex);

        List<CollectedDataDTO> dtos = pageData.stream()
                .map(cd -> toDTO(cd, displayName))
                .collect(Collectors.toList());

        return PageResult.of(filtered.size(), page, size, dtos);
    }

    public List<CollectedDataDTO> queryLatestAllPoints(Long dataSourceId) {
        List<DataPoint> dataPoints = dataPointRepository.findEnabledByDataSourceId(dataSourceId);
        Map<Long, String> displayNames = dataPoints.stream()
                .collect(Collectors.toMap(DataPoint::getId, dp -> dp.getDisplayName() != null ? dp.getDisplayName() : dp.getTagName()));

        List<CollectedDataDTO> results = new ArrayList<>();
        for (DataPoint point : dataPoints) {
            List<CollectedData> latest = collectedDataRepository.findLatestByDataPointId(point.getId(), 1);
            if (!latest.isEmpty()) {
                results.add(toDTO(latest.get(0), displayNames.get(point.getId())));
            }
        }
        return results;
    }

    private CollectedDataDTO toDTO(CollectedData cd, String displayName) {
        CollectedDataDTO dto = new CollectedDataDTO();
        dto.setId(cd.getId());
        dto.setTagName(cd.getTagName());
        dto.setDisplayName(displayName);
        dto.setDataSourceId(cd.getDataSourceId());
        dto.setRawValue(cd.getRawValue());
        dto.setNumericValue(cd.getNumericValue());
        dto.setStringValue(cd.getStringValue());
        dto.setBooleanValue(cd.getBooleanValue());
        dto.setQuality(cd.getQuality());
        dto.setCollectedAt(cd.getCollectedAt());
        return dto;
    }
}

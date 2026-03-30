package com.htw.collector.infrastructure.protocol;

import com.htw.collector.domain.datasource.DataSource;
import com.htw.collector.domain.port.ProtocolAdapterPort;
import com.htw.collector.infrastructure.protocol.modbus.ModbusTcpAdapter;
import com.htw.collector.infrastructure.protocol.mqtt.MqttAdapter;
import com.htw.collector.infrastructure.protocol.opcua.OpcUaAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CollectorAdapterFactory {

    private final Map<Long, ProtocolAdapterPort> activeAdapters = new ConcurrentHashMap<>();

    public ProtocolAdapterPort getOrCreate(DataSource dataSource) {
        return activeAdapters.computeIfAbsent(dataSource.getId(), id -> createAdapter(dataSource));
    }

    public void close(Long dataSourceId) {
        ProtocolAdapterPort adapter = activeAdapters.remove(dataSourceId);
        if (adapter != null) {
            try {
                adapter.disconnect();
                log.info("Closed adapter for data source id={}", dataSourceId);
            } catch (Exception e) {
                log.warn("Error closing adapter for data source id={}", dataSourceId, e);
            }
        }
    }

    public boolean hasAdapter(Long dataSourceId) {
        return activeAdapters.containsKey(dataSourceId);
    }

    private ProtocolAdapterPort createAdapter(DataSource dataSource) {
        if (dataSource.getProtocolType() == null) {
            throw new IllegalArgumentException("Protocol type is not set for data source: " + dataSource.getName());
        }
        return switch (dataSource.getProtocolType()) {
            case OPCUA -> new OpcUaAdapter(dataSource);
            case MODBUS_TCP -> new ModbusTcpAdapter(dataSource);
            case MQTT -> new MqttAdapter(dataSource);
        };
    }
}

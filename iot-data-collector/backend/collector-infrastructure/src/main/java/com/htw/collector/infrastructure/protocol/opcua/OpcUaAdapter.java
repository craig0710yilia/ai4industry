package com.htw.collector.infrastructure.protocol.opcua;

import com.htw.collector.domain.datapoint.DataPoint;
import com.htw.collector.domain.datasource.DataSource;
import com.htw.collector.domain.port.ProtocolAdapterPort;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
public class OpcUaAdapter implements ProtocolAdapterPort {

    private static final AtomicLong CLIENT_HANDLE_COUNTER = new AtomicLong(1L);

    @Setter
    private DataSource dataSource;
    private OpcUaClient client;

    public OpcUaAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void connect() throws Exception {
        String endpointUrl = dataSource.getOpcuaEndpointUrl();
        if (endpointUrl == null || endpointUrl.isBlank()) {
            endpointUrl = "opc.tcp://" + dataSource.getHost() + ":" + dataSource.getPort();
        }
        log.info("Connecting to OPC-UA endpoint: {}", endpointUrl);
        client = OpcUaClient.create(endpointUrl);
        client.connect().get();
        log.info("Connected to OPC-UA server: {}", endpointUrl);
    }

    @Override
    public void disconnect() {
        if (client != null) {
            try {
                client.disconnect().get();
                log.info("Disconnected from OPC-UA server");
            } catch (Exception e) {
                log.warn("Error disconnecting from OPC-UA server", e);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return client != null;
    }

    @Override
    public String readValue(DataPoint point) throws Exception {
        if (client == null) {
            throw new IllegalStateException("OPC-UA client is not connected");
        }
        NodeId nodeId = NodeId.parse(point.getOpcuaNodeId());
        DataValue value = client.readValue(0, TimestampsToReturn.Both, nodeId).get();
        if (value.getValue() != null && value.getValue().getValue() != null) {
            return value.getValue().getValue().toString();
        }
        return null;
    }

    @Override
    public void subscribe(DataPoint point, Consumer<String> callback) throws Exception {
        if (client == null) {
            throw new IllegalStateException("OPC-UA client is not connected");
        }
        UaSubscription subscription = client.getSubscriptionManager()
                .createSubscription(1000.0).get();

        NodeId nodeId = NodeId.parse(point.getOpcuaNodeId());
        ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);

        MonitoringParameters parameters = new MonitoringParameters(
                UInteger.valueOf(CLIENT_HANDLE_COUNTER.getAndIncrement()),
                (double) (point.getSampleIntervalMs() != null ? point.getSampleIntervalMs() : 1000),
                null,
                UInteger.valueOf(10),
                true
        );

        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                readValueId, MonitoringMode.Reporting, parameters);

        UaSubscription.ItemCreationCallback onItemCreated = (item, id) ->
                item.setValueConsumer((i, value) -> {
                    if (value.getValue() != null && value.getValue().getValue() != null) {
                        callback.accept(value.getValue().getValue().toString());
                    }
                });

        List<UaMonitoredItem> items = subscription.createMonitoredItems(
                TimestampsToReturn.Both,
                List.of(request),
                onItemCreated
        ).get();

        for (UaMonitoredItem item : items) {
            if (item.getStatusCode().isGood()) {
                log.info("Subscribed to OPC-UA node: {}", point.getOpcuaNodeId());
            } else {
                log.warn("Failed to subscribe to OPC-UA node {}: {}", point.getOpcuaNodeId(), item.getStatusCode());
            }
        }
    }
}

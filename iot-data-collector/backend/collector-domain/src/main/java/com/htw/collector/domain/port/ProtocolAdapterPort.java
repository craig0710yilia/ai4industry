package com.htw.collector.domain.port;

import com.htw.collector.domain.datapoint.DataPoint;

import java.util.function.Consumer;

/**
 * Outbound port for protocol adapters (OPC-UA, Modbus TCP, MQTT).
 */
public interface ProtocolAdapterPort {

    void connect() throws Exception;

    void disconnect();

    boolean isConnected();

    String readValue(DataPoint point) throws Exception;

    void subscribe(DataPoint point, Consumer<String> callback) throws Exception;
}

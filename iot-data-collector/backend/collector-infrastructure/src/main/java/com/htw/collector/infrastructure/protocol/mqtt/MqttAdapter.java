package com.htw.collector.infrastructure.protocol.mqtt;

import com.htw.collector.domain.datapoint.DataPoint;
import com.htw.collector.domain.datasource.DataSource;
import com.htw.collector.domain.port.ProtocolAdapterPort;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class MqttAdapter implements ProtocolAdapterPort {

    private final DataSource dataSource;
    private MqttClient mqttClient;
    private final Map<String, String> latestValues = new ConcurrentHashMap<>();
    private final Set<String> subscribedTopics = ConcurrentHashMap.newKeySet();

    public MqttAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void connect() throws Exception {
        String brokerUrl = dataSource.getMqttBrokerUrl();
        String clientId = dataSource.getMqttClientId();
        if (clientId == null || clientId.isBlank()) {
            clientId = MqttClient.generateClientId();
        }
        log.info("Connecting to MQTT broker: {}", brokerUrl);
        mqttClient = new MqttClient(brokerUrl, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        if (dataSource.getMqttUsername() != null && !dataSource.getMqttUsername().isBlank()) {
            options.setUserName(dataSource.getMqttUsername());
        }
        if (dataSource.getMqttPassword() != null && !dataSource.getMqttPassword().isBlank()) {
            options.setPassword(dataSource.getMqttPassword().toCharArray());
        }

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                log.warn("MQTT connection lost: {}", cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                latestValues.put(topic, new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Not used for subscriptions
            }
        });

        mqttClient.connect(options);
        log.info("Connected to MQTT broker: {}", brokerUrl);
    }

    @Override
    public void disconnect() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                log.info("Disconnected from MQTT broker");
            } catch (MqttException e) {
                log.warn("Error disconnecting from MQTT broker", e);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    @Override
    public String readValue(DataPoint point) throws Exception {
        String topic = point.getMqttTopic();
        if (topic == null || topic.isBlank()) {
            return null;
        }
        if (!subscribedTopics.contains(topic)) {
            mqttClient.subscribe(topic);
            subscribedTopics.add(topic);
        }
        return latestValues.get(topic);
    }

    @Override
    public void subscribe(DataPoint point, Consumer<String> callback) throws Exception {
        String topic = point.getMqttTopic();
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("MQTT topic is not set for point: " + point.getTagName());
        }
        mqttClient.subscribe(topic, (t, message) -> callback.accept(new String(message.getPayload())));
        subscribedTopics.add(topic);
        log.info("Subscribed to MQTT topic: {}", topic);
    }
}

-- Data Sources table
CREATE TABLE IF NOT EXISTS t_data_source (
    id                   BIGSERIAL PRIMARY KEY,
    name                 VARCHAR(100) NOT NULL UNIQUE,
    protocol_type        VARCHAR(20)  NOT NULL,  -- OPCUA, MODBUS_TCP, MQTT
    host                 VARCHAR(255),
    port                 INTEGER,
    opcua_endpoint_url   VARCHAR(500),
    opcua_security_mode  VARCHAR(50)  DEFAULT 'None',
    modbus_slave_id      INTEGER      DEFAULT 1,
    mqtt_broker_url      VARCHAR(500),
    mqtt_client_id       VARCHAR(100),
    mqtt_username        VARCHAR(100),
    mqtt_password        VARCHAR(255),
    description          TEXT,
    enabled              BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted              SMALLINT     NOT NULL DEFAULT 0,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Data Points table
CREATE TABLE IF NOT EXISTS t_data_point (
    id                      BIGSERIAL PRIMARY KEY,
    data_source_id          BIGINT       NOT NULL REFERENCES t_data_source(id),
    tag_name                VARCHAR(200) NOT NULL,
    display_name            VARCHAR(200),
    description             TEXT,
    data_type               VARCHAR(20)  NOT NULL,  -- BOOLEAN, INT16, INT32, INT64, FLOAT, DOUBLE, STRING
    opcua_node_id           VARCHAR(500),
    modbus_register_type    VARCHAR(30),  -- COIL, DISCRETE, INPUT_REGISTER, HOLDING_REGISTER
    modbus_register_address INTEGER,
    mqtt_topic              VARCHAR(500),
    mqtt_json_path          VARCHAR(200),
    sample_interval_ms      INTEGER      NOT NULL DEFAULT 1000,
    enabled                 BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted                 SMALLINT     NOT NULL DEFAULT 0,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (data_source_id, tag_name)
);

-- Collected Data table (partitioned by month)
CREATE TABLE IF NOT EXISTS t_collected_data (
    id              BIGSERIAL,
    data_point_id   BIGINT       NOT NULL,
    tag_name        VARCHAR(200) NOT NULL,
    data_source_id  BIGINT       NOT NULL,
    raw_value       TEXT,
    numeric_value   DOUBLE PRECISION,
    string_value    TEXT,
    boolean_value   BOOLEAN,
    quality         SMALLINT     NOT NULL DEFAULT 0,  -- 0=good, 1=uncertain, 2=bad
    collected_at    TIMESTAMP    NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, collected_at)
) PARTITION BY RANGE (collected_at);

-- Create initial partitions (2026 coverage)
CREATE TABLE t_collected_data_2026_01 PARTITION OF t_collected_data
    FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
CREATE TABLE t_collected_data_2026_02 PARTITION OF t_collected_data
    FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');
CREATE TABLE t_collected_data_2026_03 PARTITION OF t_collected_data
    FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');
CREATE TABLE t_collected_data_2026_04 PARTITION OF t_collected_data
    FOR VALUES FROM ('2026-04-01') TO ('2026-05-01');
CREATE TABLE t_collected_data_2026_05 PARTITION OF t_collected_data
    FOR VALUES FROM ('2026-05-01') TO ('2026-06-01');
CREATE TABLE t_collected_data_2026_06 PARTITION OF t_collected_data
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');

-- Indexes
CREATE INDEX idx_collected_data_point_time ON t_collected_data (data_point_id, collected_at DESC);
CREATE INDEX idx_collected_data_source_time ON t_collected_data (data_source_id, collected_at DESC);

-- Collection Task Log table
CREATE TABLE IF NOT EXISTS t_collection_task_log (
    id              BIGSERIAL PRIMARY KEY,
    data_source_id  BIGINT       NOT NULL,
    task_name       VARCHAR(200),
    start_time      TIMESTAMP    NOT NULL,
    end_time        TIMESTAMP,
    status          VARCHAR(20)  NOT NULL,  -- SUCCESS, FAILURE, PARTIAL
    success_count   INTEGER      DEFAULT 0,
    failure_count   INTEGER      DEFAULT 0,
    error_message   TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_task_log_source ON t_collection_task_log (data_source_id, start_time DESC);

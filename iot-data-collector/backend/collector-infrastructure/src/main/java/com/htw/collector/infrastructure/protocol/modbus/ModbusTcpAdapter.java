package com.htw.collector.infrastructure.protocol.modbus;

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.util.BitVector;
import com.htw.collector.domain.datapoint.DataPoint;
import com.htw.collector.domain.datasource.DataSource;
import com.htw.collector.domain.port.ProtocolAdapterPort;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class ModbusTcpAdapter implements ProtocolAdapterPort {

    private final DataSource dataSource;
    private ModbusTCPMaster master;

    public ModbusTcpAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void connect() throws Exception {
        int port = dataSource.getPort() != null ? dataSource.getPort() : 502;
        log.info("Connecting to Modbus TCP: {}:{}", dataSource.getHost(), port);
        master = new ModbusTCPMaster(dataSource.getHost(), port);
        master.connect();
        log.info("Connected to Modbus TCP: {}:{}", dataSource.getHost(), port);
    }

    @Override
    public void disconnect() {
        if (master != null) {
            try {
                master.disconnect();
                log.info("Disconnected from Modbus TCP");
            } catch (Exception e) {
                log.warn("Error disconnecting from Modbus TCP", e);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return master != null;
    }

    @Override
    public String readValue(DataPoint point) throws Exception {
        if (master == null) {
            throw new IllegalStateException("Modbus TCP master is not connected");
        }
        int slaveId = dataSource.getModbusSlaveId() != null ? dataSource.getModbusSlaveId() : 1;
        int address = point.getModbusRegisterAddress() != null ? point.getModbusRegisterAddress() : 0;
        String registerType = point.getModbusRegisterType();

        if (registerType == null) {
            throw new IllegalArgumentException("Modbus register type is not set for point: " + point.getTagName());
        }

        switch (registerType.toUpperCase()) {
            case "COIL": {
                BitVector bits = master.readCoils(slaveId, address, 1);
                return String.valueOf(bits.getBit(0));
            }
            case "DISCRETE": {
                BitVector bits = master.readInputDiscretes(slaveId, address, 1);
                return String.valueOf(bits.getBit(0));
            }
            case "INPUT_REGISTER": {
                InputRegister[] regs = master.readInputRegisters(slaveId, address, 1);
                return String.valueOf(regs[0].getValue());
            }
            case "HOLDING_REGISTER": {
                Register[] regs = master.readMultipleRegisters(slaveId, address, 1);
                return String.valueOf(regs[0].getValue());
            }
            default:
                throw new IllegalArgumentException("Unsupported Modbus register type: " + registerType);
        }
    }

    @Override
    public void subscribe(DataPoint point, Consumer<String> callback) {
        throw new UnsupportedOperationException(
                "Modbus TCP is a polling-based protocol and does not support subscriptions");
    }
}

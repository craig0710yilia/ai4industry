package com.htw.collector.domain.gateway;

import com.htw.collector.domain.model.CollectedReading;
import com.htw.collector.domain.model.DataPoint;
import com.htw.collector.domain.model.DataSource;
import java.util.List;

/**
 * 协议网关抽象：不同协议的实现放在 infrastructure 层
 */
public interface ProtocolGateway {

    /** 建立连接 */
    void connect(DataSource dataSource) throws Exception;

    /** 断开连接 */
    void disconnect(DataSource dataSource);

    /** 同步读取一批点位的当前值 */
    List<CollectedReading> readPoints(DataSource dataSource, List<DataPoint> points);

    /** 是否处于连接状态 */
    boolean isConnected(Long dataSourceId);
}

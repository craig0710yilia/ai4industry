package com.htw.collector.domain.model;

public enum CollectionTaskStatus {
    /** 空闲，等待下次调度 */
    IDLE,
    /** 正在采集 */
    RUNNING,
    /** 采集出错，等待重试 */
    ERROR,
    /** 已停止 */
    STOPPED
}

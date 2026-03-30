package com.htw.collector.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.htw.collector.infrastructure.persistence.entity.CollectionTaskLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollectionTaskLogMapper extends BaseMapper<CollectionTaskLogDO> {
}

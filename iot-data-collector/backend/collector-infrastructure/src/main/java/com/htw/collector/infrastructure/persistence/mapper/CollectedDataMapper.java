package com.htw.collector.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.htw.collector.infrastructure.persistence.entity.CollectedDataDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CollectedDataMapper extends BaseMapper<CollectedDataDO> {

    @Select("SELECT * FROM t_collected_data WHERE data_point_id = #{dataPointId} " +
            "ORDER BY collected_at DESC LIMIT #{limit}")
    List<CollectedDataDO> selectLatestByDataPointId(@Param("dataPointId") Long dataPointId,
                                                     @Param("limit") int limit);
}

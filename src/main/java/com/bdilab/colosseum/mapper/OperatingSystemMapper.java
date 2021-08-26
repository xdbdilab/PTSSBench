package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.OperatingSystem;
import org.apache.ibatis.annotations.Param;

public interface OperatingSystemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OperatingSystem record);

    int insertSelective(OperatingSystem record);

    OperatingSystem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OperatingSystem record);

    int updateByPrimaryKey(OperatingSystem record);

    //需不需要创建name和version的唯一索引
    OperatingSystem selectByNameAndVersion(@Param("name") String name, @Param("version") String version);

}
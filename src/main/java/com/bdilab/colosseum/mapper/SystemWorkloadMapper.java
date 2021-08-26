package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.SystemWorkload;
import com.bdilab.colosseum.vo.SystemWorkloadVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemWorkloadMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SystemWorkload record);

    int insertSelective(SystemWorkload record);

    SystemWorkload selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SystemWorkload record);

    int updateByPrimaryKey(SystemWorkload record);

    SystemWorkload selectByNameAndVersion(@Param("name") String name, @Param("version") String version);

    List<String> selectName();

    List<String> selectVersionByName(@Param("name") String name);

    List<SystemWorkload> selectAll();
}
package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.SystemSut;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemSutMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SystemSut record);

    int insertSelective(SystemSut record);

    SystemSut selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SystemSut record);

    int updateByPrimaryKey(SystemSut record);

    SystemSut selectByNameAndVersion(@Param("name") String name, @Param("version") String version);

    List<String> selectName();

    List<String> selectVersionByName(@Param("name") String name);

    List<SystemSut> selectAll();
}
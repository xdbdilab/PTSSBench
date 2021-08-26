package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.Hardware;

public interface HardwareMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Hardware record);

    int insertSelective(Hardware record);

    Hardware selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Hardware record);

    int updateByPrimaryKey(Hardware record);
}
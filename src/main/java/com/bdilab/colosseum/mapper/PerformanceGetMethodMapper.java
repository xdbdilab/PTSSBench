package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.PerformanceGetMethod;

public interface PerformanceGetMethodMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PerformanceGetMethod record);

    int insertSelective(PerformanceGetMethod record);

    PerformanceGetMethod selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PerformanceGetMethod record);

    int updateByPrimaryKey(PerformanceGetMethod record);

    PerformanceGetMethod selectBySutName(String sutName);
}
package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.ComponentParam;

import java.util.List;

public interface ComponentParamMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ComponentParam record);

    int insertSelective(ComponentParam record);

    ComponentParam selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ComponentParam record);

    int updateByPrimaryKey(ComponentParam record);

    List<ComponentParam> selectAll();

    /**
     * 根据组件id查找组件参数
     * @param componentId
     * @return
     */
    List<ComponentParam> selectByComponentId(Long componentId);
}
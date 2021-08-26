package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.Component;

import java.util.List;

public interface ComponentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Component record);

    int insertSelective(Component record);

    Component selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Component record);

    int updateByPrimaryKey(Component record);

    List<Component> selectAllPublicComponent();

    List<Component> selectByUserId(Long userId);

    /**
     * 根据组件名获取组件类型
     * @param componentName
     * @return
     */
    Integer selectTypeByComponentName(String componentName);
}
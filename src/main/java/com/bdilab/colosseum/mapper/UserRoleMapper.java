package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.UserRole;

import java.util.List;

public interface UserRoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    UserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);

    List<UserRole> findByUserId(Long userId);
}
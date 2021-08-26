package com.bdilab.colosseum.service;

import com.bdilab.colosseum.domain.UserRole;

import java.util.List;

public interface UserRoleService {
    /**
     * 根据Id返回UserRole对象
     */
    UserRole findById(Long id);

    /**
     * 根据用户id查询该用户对应的UserRole列表(目前一个用户对应一个角色 普通用户|管理员)
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * 新增UserRole
     */
    int insertSelective(UserRole userRole);
}

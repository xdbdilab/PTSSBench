package com.bdilab.colosseum.service.impl;

import com.bdilab.colosseum.domain.UserRole;
import com.bdilab.colosseum.mapper.UserRoleMapper;
import com.bdilab.colosseum.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    UserRoleMapper userRoleMapper;

    /*根据id返回UserRole对象*/
    @Override
    public UserRole findById(Long id) {
        return userRoleMapper.selectByPrimaryKey(id);
    }

    /*根据用户id查询该用户对应的UserRole列表(目前一个用户对应一个角色 普通用户|管理员)*/
    @Override
    public List<UserRole> findByUserId(Long userId) {
        return userRoleMapper.findByUserId(userId);
    }

    /*新增UserRole*/
    @Override
    public int insertSelective(UserRole userRole) {
        return userRoleMapper.insertSelective(userRole);
    }
}

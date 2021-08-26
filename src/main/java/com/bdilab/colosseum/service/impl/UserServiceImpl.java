package com.bdilab.colosseum.service.impl;

import com.bdilab.colosseum.domain.User;
import com.bdilab.colosseum.domain.UserRole;
import com.bdilab.colosseum.exception.BusinessException;
import com.bdilab.colosseum.mapper.UserMapper;
import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.service.UserRoleService;
import com.bdilab.colosseum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserServiceImpl
 * @Author wx
 * @Date 2020/12/10 0010 15:31
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRoleService userRoleService;

    /**
     * 根据Id返回User对象
     *
     * @param userId
     * @return
     */
    @Override
    public User findById(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    /**
     * 登录验证
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public User userLoginCheck(String username, String password) {
        return userMapper.selectUserByNameAndPassword(username,password);
    }

    /**
     * 注册检测
     *
     * @param username
     * @return
     */
    @Override
    public User userRegisterCheck(String username) {
        return userMapper.selectUserByName(username);
    }

    /**
     * 新建用户
     *
     * @param user
     */
    @Override
    public void saveUser(User user) {
        try{
            int i = userMapper.insertSelective(user);
            //查找该用户名是否存在多个，多个的话抛出一场
            int count=userMapper.selectCountByName(user.getUsername());
            System.out.println(count);
            if(count>1) throw new BusinessException(HttpCode.BAD_REQUEST.getCode(), "注册失败，用户名已存在", "UserServiceImpl->saveUser出错：用户名重复");
            User user1=userMapper.selectUserByName(user.getUsername());
            UserRole userRole = new UserRole();
            userRole.setFkUserId(user1.getId());
            userRole.setType((byte) 0);
            int j = userRoleService.insertSelective(userRole);
        }catch (Exception e){
            throw new BusinessException(HttpCode.SERVER_ERROR,"UserServiceImpl->saveUser出错：未知错误",e);
        }
    }
}

package com.bdilab.colosseum.service;

import com.bdilab.colosseum.domain.User;

/**
 * @Name UserService
 * @Author wx
 * @Date 2020/12/10 0010 15:31
 **/
public interface UserService {

    /**
     * 根据Id返回User对象
     * @param userId
     * @return
     */
    User findById(Long userId);

    /**
     * 登录验证
     * @param username
     * @param password
     * @return
     */
    User userLoginCheck(String username, String password);

    /**
     * 注册检测
     * @param username
     * @return
     */
    User userRegisterCheck(String username);

    /**
     * 新建用户
     * @param user
     */
    void saveUser(User user);
}

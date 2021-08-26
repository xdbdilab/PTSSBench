package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 根据用户名和密码检索用户
     * @param username
     * @param password
     * @return
     */
    User selectUserByNameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 根据用户名检索用户
     * @param username 用户名
     * @return
     */
    User selectUserByName(@Param("username") String username);

    /**
     * 根据用户名检索是否存在同名用户
     * @param username 用户名
     * @return
     */
    Integer selectCountByName(@Param("username") String username);

}
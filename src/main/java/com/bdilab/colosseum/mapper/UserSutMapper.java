package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.UserSut;

public interface UserSutMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserSut record);

    int insertSelective(UserSut record);

    UserSut selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserSut record);

    int updateByPrimaryKey(UserSut record);
}
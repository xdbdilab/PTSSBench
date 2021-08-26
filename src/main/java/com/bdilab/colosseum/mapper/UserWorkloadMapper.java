package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.UserWorkload;

public interface UserWorkloadMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserWorkload record);

    int insertSelective(UserWorkload record);

    UserWorkload selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserWorkload record);

    int updateByPrimaryKey(UserWorkload record);
}
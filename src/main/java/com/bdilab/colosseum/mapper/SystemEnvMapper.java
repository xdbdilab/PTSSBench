package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.bo.SoftwareLocationBO;
import com.bdilab.colosseum.domain.SystemEnv;
import com.bdilab.colosseum.vo.SystemEnvVO;
import com.bdilab.colosseum.vo.SystemEnvDetailVo;
import com.bdilab.colosseum.vo.SystemEnvVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SystemEnvMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SystemEnv record);

    int insertSelective(SystemEnv record);

    SystemEnv selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SystemEnv record);

    int updateByPrimaryKey(SystemEnv record);

    /**
     * 根据sysEnvId查询环境配置VO对象
     * @param sysEnvId
     * @return
     */
    SystemEnvVO selectBySysEnvId(Long sysEnvId);

    List<SystemEnv> selectByFkUserId(Long userId);

    SystemEnv selectByIdAndUserId(@Param("userId") Long userId, @Param("id") Long systemEnvId);

    SystemEnvDetailVo selectDetailByIdAndUserId(@Param("userId") Long userId, @Param("id") Long systemEnvId);

    /**
     * 根据环境id查询环境信息是否存在
     * @param envId
     * @return
     */
    int checkBySystemEnvId(Long envId);

    /**
     * 根据环境id查询对应的sut和workload的安装路径
     * @param envId
     * @return
     */
    SoftwareLocationBO selectSoftWareLocationByEnvId(Long envId);

    /**
     * 根据envId获取sutName and sutVersion
     * @param envId
     * @return
     */
    Map<String,Object> selectSutNameAndSutVersionByEnvId(Long envId);

    /**
     * 根据envId获取sutName and sutVersion
     * @param envId
     * @return
     */
    Map<String,Object> selectWorkloadNameAndWorkloadVersionByEnvId(Long envId);
}
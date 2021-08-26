package com.bdilab.colosseum.service;

import com.bdilab.colosseum.domain.ComponentParam;
import com.bdilab.colosseum.vo.ComponentVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author duyeye
 * @Date 2020/12/29 0029 15:38
 */
public interface ComponentService {
    List<ComponentVO> loadPublicComponent();

    boolean createComponent(String name, Byte type, String desc, String image, List<ComponentParam> param, long userId, String componentFile, MultipartFile logo);

    boolean deleteComponentById(Long componentId);

    Map<String, Object> selectUserComponent(int pageNum, int pageSize, Long userId);
}

package com.bdilab.colosseum.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author SunRen
 * @version 1.0
 * @date 2021/10/4 14:09
 **/
@Data
public class XlsRead {
    //表头
    private Map<Integer, String> titleMap;

    //数据
    private List<Map<String, Object>> dataList;
}

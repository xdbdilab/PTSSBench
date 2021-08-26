package com.bdilab.colosseum.vo;

import java.util.List;

/**
 * @author SunRen
 * @version 1.0
 * @date 2021/1/8 17:22
 */
public class SystemSutVo {
    private String sutName;

    private List<String> sutVersion;

    public String getSutName() {
        return sutName;
    }

    public void setSutName(String sutName) {
        this.sutName = sutName;
    }

    public List<String> getSutVersion() {
        return sutVersion;
    }

    public void setSutVersion(List<String> sutVersion) {
        this.sutVersion = sutVersion;
    }
}

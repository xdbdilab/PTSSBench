package com.bdilab.colosseum.vo;

/**
 * @Author liran
 * @Date 2021/6/25 0006 14:31
 */
public class AlgorithmForExpDetailVO {

    private Long id;

    private String algorithmName;

    private String tag;

    private String description;

    private String ggeditorObjectString;

    private Integer isTemplate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGgeditorObjectString() {
        return ggeditorObjectString;
    }

    public void setGgeditorObjectString(String ggeditorObjectString) {
        this.ggeditorObjectString = ggeditorObjectString;
    }

    public Integer getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Integer isTemplate) {
        this.isTemplate = isTemplate;
    }
}

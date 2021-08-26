package com.bdilab.colosseum.vo;

import java.util.Date;

/**
 * @Author duyeye
 * @Date 2021/1/6 0006 14:31
 */
public class AlgorithmVO {

    private Long id;

    private String algorithmName;

    private String tag;

    private String description;

    private String ggeditorObjectString;

    private String paramValue;

    private Date createTime;

    private Date modifyTime;

    private String logo;

    private Integer isTemplate=0;//默认为0

    public Integer getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Integer isTemplate) {
        this.isTemplate = isTemplate;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

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

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}

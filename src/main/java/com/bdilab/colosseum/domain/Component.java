package com.bdilab.colosseum.domain;

import java.util.Date;

public class Component {
    private Long id;

    private Byte type;

    private String componentName;

    private String description;

    private Long fkUserId;

    private String componentFile;

    private String image;

    private String inputStub;

    private String outputStub;

    private Date createTime;

    private Date modifyTime;

    private String logoPath;

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName == null ? null : componentName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }

    public String getComponentFile() {
        return componentFile;
    }

    public void setComponentFile(String componentFile) {
        this.componentFile = componentFile == null ? null : componentFile.trim();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image == null ? null : image.trim();
    }

    public String getInputStub() {
        return inputStub;
    }

    public void setInputStub(String inputStub) {
        this.inputStub = inputStub == null ? null : inputStub.trim();
    }

    public String getOutputStub() {
        return outputStub;
    }

    public void setOutputStub(String outputStub) {
        this.outputStub = outputStub == null ? null : outputStub.trim();
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
package com.bdilab.colosseum.domain;

public class UserRole {
    private Long id;

    private Long fkUserId;

    private Byte type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", fkUserId=" + fkUserId +
                ", type=" + type +
                '}';
    }
}
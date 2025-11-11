package com.platform.system.qywx.vo;

import java.io.Serializable;

public class AccessTokenVo implements Serializable {

    private String corpid;

    private String corpsecret;

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getCorpsecret() {
        return corpsecret;
    }

    public void setCorpsecret(String corpsecret) {
        this.corpsecret = corpsecret;
    }

    @Override
    public String toString() {
        return "AccessTokenVo{" +
                "corpid='" + corpid + '\'' +
                ", corpsecret='" + corpsecret + '\'' +
                '}';
    }
}

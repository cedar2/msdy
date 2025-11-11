package com.platform.system.qywx.dto;

import java.io.Serializable;

public class UserInfoDTO implements Serializable {


    private Integer errcode;

    private String errmsg;

    private String UserId;

    /**
     * 手机设备号(由企业微信在安装时随机生成，删除重装会改变，升级不受影响)
     */
    private String DeviceId;


    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    @Override
    public String toString() {
        return "UserInfoDTO{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", UserId='" + UserId + '\'' +
                ", DeviceId='" + DeviceId + '\'' +
                '}';
    }
}


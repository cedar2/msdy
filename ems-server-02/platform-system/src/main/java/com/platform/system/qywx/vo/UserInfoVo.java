package com.platform.system.qywx.vo;

import java.io.Serializable;

public class UserInfoVo implements Serializable {
    private String access_token;

    private String code;


    public UserInfoVo() {
    }

    public UserInfoVo(String access_token, String code) {
        this.access_token = access_token;
        this.code = code;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "UserInfoVo{" +
                "access_token='" + access_token + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}

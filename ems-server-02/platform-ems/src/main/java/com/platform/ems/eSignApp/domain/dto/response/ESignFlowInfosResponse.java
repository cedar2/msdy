package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowInfosResponse {
    private int code;

    private String message;

    private ESignFlowInfoResponse data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(ESignFlowInfoResponse data) {
        this.data = data;
    }

    public ESignFlowInfoResponse getData() {
        return data;
    }
}

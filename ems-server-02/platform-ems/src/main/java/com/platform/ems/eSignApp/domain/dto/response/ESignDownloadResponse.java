package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同下载
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignDownloadResponse {
    private int code;

    private String message;

    private ESignDownloadFilesResponse data;

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

    public ESignDownloadFilesResponse getData() {
        return data;
    }

    public void setData(ESignDownloadFilesResponse data) {
        this.data = data;
    }
}

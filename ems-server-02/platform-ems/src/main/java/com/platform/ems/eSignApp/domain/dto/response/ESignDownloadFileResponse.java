package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同下载
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignDownloadFileResponse {
    //签署文件ID
    private String fileId;
    //签署文件名称
    private String fileName;
    //已签署文件下载链接
    private String downloadUrl;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}

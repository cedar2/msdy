package com.platform.ems.eSignApp.domain.dto.response;

import java.util.List;

/**
 * @description: 合同下载
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignDownloadFilesResponse {
    private List<ESignDownloadFileResponse> files;

    private List<ESignDownloadFileResponse> attachments;

    public List<ESignDownloadFileResponse> getFiles() {
        return files;
    }

    public void setFiles(List<ESignDownloadFileResponse> files) {
        this.files = files;
    }

    public List<ESignDownloadFileResponse> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ESignDownloadFileResponse> attachments) {
        this.attachments = attachments;
    }
}

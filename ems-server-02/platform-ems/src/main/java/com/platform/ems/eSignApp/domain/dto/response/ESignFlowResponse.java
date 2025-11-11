package com.platform.ems.eSignApp.domain.dto.response;

import java.util.List;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowResponse {
    //签署流程ID
    private String signFlowId;

    //流程开始时间
    private Long signFlowStartTime;

    //流程结束时间
    private Long signFlowEndTime;

    //签署流程标题
    private String signFlowTitle;

    //签署流程状态
    private int signFlowStatus;

    //签署流程的解约状态
    private int rescissionStatus;

    //签署方信息列表
    private List<ESignFlowSignersResponse> signers;

    //签署流程发起方信息
    private ESignFlowInitResponse signFlowInitiator;

    public String getSignFlowId() {
        return signFlowId;
    }

    public void setSignFlowId(String signFlowId) {
        this.signFlowId = signFlowId;
    }

    public Long getSignFlowStartTime() {
        return signFlowStartTime;
    }

    public void setSignFlowStartTime(Long signFlowStartTime) {
        this.signFlowStartTime = signFlowStartTime;
    }

    public Long getSignFlowEndTime() {
        return signFlowEndTime;
    }

    public void setSignFlowEndTime(Long signFlowEndTime) {
        this.signFlowEndTime = signFlowEndTime;
    }

    public String getSignFlowTitle() {
        return signFlowTitle;
    }

    public void setSignFlowTitle(String signFlowTitle) {
        this.signFlowTitle = signFlowTitle;
    }

    public int getSignFlowStatus() {
        return signFlowStatus;
    }

    public void setSignFlowStatus(int signFlowStatus) {
        this.signFlowStatus = signFlowStatus;
    }

    public int getRescissionStatus() {
        return rescissionStatus;
    }

    public void setRescissionStatus(int rescissionStatus) {
        this.rescissionStatus = rescissionStatus;
    }

    public List<ESignFlowSignersResponse> getSigners() {
        return signers;
    }

    public void setSigners(List<ESignFlowSignersResponse> signers) {
        this.signers = signers;
    }

    public ESignFlowInitResponse getSignFlowInitiator() {
        return signFlowInitiator;
    }

    public void setSignFlowInitiator(ESignFlowInitResponse signFlowInitiator) {
        this.signFlowInitiator = signFlowInitiator;
    }
}

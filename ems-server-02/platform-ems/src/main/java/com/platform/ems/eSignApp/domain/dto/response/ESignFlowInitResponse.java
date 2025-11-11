package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowInitResponse {
    private ESignFlowOrgResponse orgInitiator;

    private ESignFlowPsnResponse psnInitiator;

    public ESignFlowOrgResponse getOrgInitiator() {
        return orgInitiator;
    }

    public void setOrgInitiator(ESignFlowOrgResponse orgInitiator) {
        this.orgInitiator = orgInitiator;
    }

    public ESignFlowPsnResponse getPsnInitiator() {
        return psnInitiator;
    }

    public void setPsnInitiator(ESignFlowPsnResponse psnInitiator) {
        this.psnInitiator = psnInitiator;
    }
}

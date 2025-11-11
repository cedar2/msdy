package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowSignersResponse {
    private ESignFlowOrgResponse orgSigner;

    private ESignFlowPsnResponse psnSigner;

    public ESignFlowOrgResponse getOrgSigner() {
        return orgSigner;
    }

    public void setOrgSigner(ESignFlowOrgResponse orgSigner) {
        this.orgSigner = orgSigner;
    }

    public ESignFlowPsnResponse getPsnSigner() {
        return psnSigner;
    }

    public void setPsnSigner(ESignFlowPsnResponse psnSigner) {
        this.psnSigner = psnSigner;
    }
}

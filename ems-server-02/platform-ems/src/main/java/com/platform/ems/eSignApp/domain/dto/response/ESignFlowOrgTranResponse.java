package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowOrgTranResponse {
    //经办人账号ID
    private String psnId;

    //经办人账号
    private ESignFlowOrgTranAccResponse psnAccount;

    public String getPsnId() {
        return psnId;
    }

    public void setPsnId(String psnId) {
        this.psnId = psnId;
    }

    public ESignFlowOrgTranAccResponse getPsnAccount() {
        return psnAccount;
    }

    public void setPsnAccount(ESignFlowOrgTranAccResponse psnAccount) {
        this.psnAccount = psnAccount;
    }
}

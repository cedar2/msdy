package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowPsnResponse {
    //个人签署方账号ID
    private String psnId;

    //个人签署方账号
    private ESignFlowPsnAccResponse psnAccount;

    public String getPsnId() {
        return psnId;
    }

    public void setPsnId(String psnId) {
        this.psnId = psnId;
    }

    public ESignFlowPsnAccResponse getPsnAccount() {
        return psnAccount;
    }

    public void setPsnAccount(ESignFlowPsnAccResponse psnAccount) {
        this.psnAccount = psnAccount;
    }
}

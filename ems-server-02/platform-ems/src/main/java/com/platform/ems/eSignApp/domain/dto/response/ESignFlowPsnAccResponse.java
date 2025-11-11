package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowPsnAccResponse {
    //个人签署方账号ID
    private String accountMobile;

    //个人签署方账号
    private String accountEmail;

    public String getAccountMobile() {
        return accountMobile;
    }

    public void setAccountMobile(String accountMobile) {
        this.accountMobile = accountMobile;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }
}

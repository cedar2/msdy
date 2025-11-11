package com.platform.ems.eSignApp.domain.dto.response;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowOrgResponse {
    //机构账号ID
    private String orgId;

    //机构名称
    private String orgName;

    //机构签署经办人
    private ESignFlowOrgTranResponse transactor;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public ESignFlowOrgTranResponse getTransactor() {
        return transactor;
    }

    public void setTransactor(ESignFlowOrgTranResponse transactor) {
        this.transactor = transactor;
    }
}

package com.platform.ems.eSignApp.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 签署合同
 * @author:
 * @date:
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESignFlowRequest {

    private String beginTime;

    private String endTime;

    private Integer pageNum;

    private Integer pageSize;

    //机构类别
    private String orgType;

    //签署状态
    private String handleContractList;

    //公司名称或个人电话
    private String companyName;

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getHandleContractList() {
        return handleContractList;
    }

    public void setHandleContractList(String handleContractList) {
        this.handleContractList = handleContractList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}

package com.platform.ems.eSignApp.domain.dto.response;

import java.util.List;

/**
 * @description: 合同列表
 * @author: hhy
 * @date: 2022-11-09
 */
public class ESignFlowInfoResponse {
    private int total;

    private List<ESignFlowResponse> signFlowInfos;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ESignFlowResponse> getSignFlowInfos() {
        return signFlowInfos;
    }

    public void setSignFlowInfos(List<ESignFlowResponse> signFlowInfos) {
        this.signFlowInfos = signFlowInfos;
    }
}

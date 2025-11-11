package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @description: 批量提交、审核、撤回
 * @author:
 * @date:
 */
public class BasButtonRequest implements Serializable {

    //ids
    private String[] ids;
    //处理状态
    private String handleStatus;
    //按钮
    private String button;
    //启用/停用
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisableRemark() {
        return disableRemark;
    }

    public void setDisableRemark(String disableRemark) {
        this.disableRemark = disableRemark;
    }
}

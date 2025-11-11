package com.platform.ems.domain.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 版型档案
 *
 * @author olive
 */
@Data
public class GetInfoModelSystemResponse implements Serializable {
    private String modelSid;
    private String modelCode;
    private String modelName;
    private String skuGroupSid;
    private String upDownSuit;
    private String maleFemaleFlag;
    private Integer standardSku;
    private String oldYoungFlag;
    private String modelType;
    private Integer customer;
    private String creatorAccount;
    private String handleStatus;
    private String status;
    private String remark;
}

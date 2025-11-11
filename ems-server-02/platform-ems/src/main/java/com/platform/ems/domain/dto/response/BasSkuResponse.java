package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors( chain = true)
public class BasSkuResponse implements Serializable {

    /**
     * skusid
     */
    private String skuSid;

    private String codeAndName;

    private String codeName;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * SKU名称
     */
    private String skuName2;

    /**
     * SKU名称
     */
    private String skuName3;

    /**
     * SKU类型编码
     */
    private String skuType;

    /**
     * 启用/停用状态
     */
    private String status;

    /**
     * 处理状态
     */
    private String handleStatus;

    /**
     * 创建者
     */
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private Date createDate;

    /**
     * 更新者
     */
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private Date updateDate;

}

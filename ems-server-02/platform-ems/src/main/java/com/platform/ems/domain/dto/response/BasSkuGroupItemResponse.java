package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * SKU组明细对象 s_bas_sku_group_item
 *
 * @author chenkaiwen
 * @date 2021-01-26
 */
@Data
@Accessors( chain = true)
public class BasSkuGroupItemResponse implements Serializable {

    /** SKU组明细sid */
    public String skuGroupItemSid;

    /** SKU组sid */
    public String skuGroupSid;

    /** SKUsid */
    public String skuSid;

    /** SKU编码名称 */
    public String skuCodeName;

    /** SKU编码 */
    public String skuCode;

    /** SKU名称 */
    public String skuName;

    /** SKU名称 */
    public String skuName2;

    /** SKU名称 */
    public String skuName3;

    /** SKU名称 */
    public String skuName4;

    /** SKU名称 */
    public String skuName5;

    /** 处理状态 */
    public String handleStatus;

    /** 序号 */
    public String sort;

    /** 启用/停用状态 */
    public String status;

    /** 创建人账号 */
    public String creatorAccount;

    /** 创建日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date createDate;

    /** 更改人账号 */
    public String updaterAccount;

    /** 更改日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date updateDate;

}

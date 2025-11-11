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
public class BasSkuGroupListResponse implements Serializable  {

    /** SKU组sid */
    public String skuGroupSid;

    /** SKU组编码 */
    public String skuGroupCode;

    /** SKU组名称 */
    public String skuGroupName;

    /** 启用/停用状态 */
    public String status;

    /** SKU类型编码 */
    public String skuType;

    /** 处理状态 */
    public String handleStatus;

    /** 备注 */
    public String remark;

    /** 上下装 */
    private String upDownSuit;

    /** 客户 */
    private Long customer;

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

    /** 确认人账号 */
    public String confirmerAccount;

    /** 确认日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date confirmDate;


}

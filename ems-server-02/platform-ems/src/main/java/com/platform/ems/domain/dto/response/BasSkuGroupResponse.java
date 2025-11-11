package com.platform.ems.domain.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * SKU组明细对象 s_bas_sku_group_item
 *
 * @author chenkaiwen
 * @date 2021-01-26
 */
@Data
@Accessors( chain = true)
public class BasSkuGroupResponse implements Serializable  {

    /** SKU组sid */
    public String skuGroupSid;

    /** SKU组编码 */
    public String skuGroupCode;

    /** SKU组名称 */
    public String skuGroupName;

    /** SKU类型编码 */
    public String skuType;

    /** 上下装 */
    private String upDownSuit;

    /** 客户 */
    private Long customer;

    /** 启用/停用状态 */
    public String status;

    /** 处理状态 */
    public String handleStatus;

    /** 备注 */
    public String remark;

    /** 创建人账号 */
    public String creatorAccount;
}

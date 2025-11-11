package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * sku组明细报表 BasSkuGroupItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-11-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasSkuGroupItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "系统SID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuGroupItemSid;

    @Excel(name = "SKU属性组编码")
    @ApiModelProperty(value = "SKU组编码")
    private String skuGroupCode;

    @Excel(name = "SKU属性组名称")
    @ApiModelProperty(value = "SKU属性组名称")
    private String skuGroupName;

    @Excel(name = "SKU属性编码")
    @ApiModelProperty(value = "SKU属性编码")
    private String skuCode;

    @Excel(name = "SKU属性名称")
    @ApiModelProperty(value = "SKU属性名称")
    private String skuName;

    @Excel(name = "sku属性名称2")
    @ApiModelProperty(value = "sku属性名称2")
    private String skuName2;

    @Excel(name = "sku属性类型",dictType = "s_sku_type")
    @ApiModelProperty(value = "sku属性类型")
    private String skuType;

    @Excel(name = "sku属性数值")
    @ApiModelProperty(value = "sku数值")
    private String skuNumeralValue;

    @Excel(name = "启用/停用",dictType = "s_valid_flag")
    @ApiModelProperty(value = "启停状态")
    private String status;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "上下装/套装",dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装套装")
    private String upDownSuit;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private String confirmDate;

    @ApiModelProperty(value = "sku名称3")
    private String skuName3;

    @ApiModelProperty(value = "sku名称4")
    private String skuName4;

    @ApiModelProperty(value = "sku名称5")
    private String skuName5;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

}

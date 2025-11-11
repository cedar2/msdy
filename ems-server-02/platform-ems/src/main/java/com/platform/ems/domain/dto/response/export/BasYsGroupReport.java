package com.platform.ems.domain.dto.response.export;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.BasSkuGroupItem;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * SKU组档案对象 s_bas_sku_group
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_sku_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasYsGroupReport extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-SKU组档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU组档案")
    @TableId
    private Long skuGroupSid;

    /**
     * SKU组编码
     */
    @Excel(name = "颜色组编码")
    @ApiModelProperty(value = "SKU组编码")
    private String skuGroupCode;

    public void setSkuGroupCode(String skuGroupCode) {
        if (StrUtil.isNotBlank(skuGroupCode)) {
            skuGroupCode = skuGroupCode.replaceAll("\\s*", "");
        }
        this.skuGroupCode = skuGroupCode;
    }

    public void setSkuGroupName(String skuGroupName) {
        if (StrUtil.isNotBlank(skuGroupName)) {
            skuGroupName = skuGroupName.trim();
        }
        this.skuGroupName = skuGroupName;
    }

    /**
     * SKU组名称
     */
    @Excel(name = "颜色组名称")
    @ApiModelProperty(value = "SKU组名称")
    @NotEmpty(message = "名称不能为空")
    private String skuGroupName;

    /**
     * SKU类型编码
     */
    @Excel(name = "SKU属性类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU类型编码")
    private String skuType;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    @TableField(exist = false)
    private String customerName;

    /**
     * 上下装编码
     */
    @ApiModelProperty(value = "上下装编码")
    private String upDownSuit;

    /**
     * 客户
     */

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户")
    private Long customerSid;

    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "明细列表")
    @TableField(exist = false)
    private List<BasSkuGroupItem> itemList;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] skuGroupSidList;

    @ApiModelProperty(value = "上下装数组")
    @TableField(exist = false)
    private String[] upDownSuitList;

    @ApiModelProperty(value = "处理状态数组")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "客户id数组")
    @TableField(exist = false)
    private Long[] customerSidList;

    @ApiModelProperty(value = "sku类型数组")
    @TableField(exist = false)
    private String[] skuTypeList;

}

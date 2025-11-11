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
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;

/**
 * SKU档案对象 s_bas_sku
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_sku")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasSkuReport extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-SKU档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-SKU档案")
    @TableId
    private Long skuSid;

    public void setSkuCode(String skuCode) {
        if (StrUtil.isNotBlank(skuCode)) {
            skuCode = skuCode.replaceAll("\\s*", "");
        }
        this.skuCode = skuCode;
    }

    public void setSkuName(String skuName) {
        if (StrUtil.isNotBlank(skuName)) {
            skuName = skuName.trim();
        }
        this.skuName = skuName;
    }

    public void setSkuName2(String skuName2) {
        if (StrUtil.isNotBlank(skuName2)) {
            skuName2 = skuName2.trim();
        }
        this.skuName2 = skuName2;
    }

    /**
     * SKU编码
     */
    @Excel(name = "SKU编码")
    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    /**
     * SKU名称
     */
    @NotEmpty(message = "名称不能为空")
    @Excel(name = "SKU名称")
    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    /**
     * SKU名称2
     */
    @Excel(name = "SKU名称2")
    @ApiModelProperty(value = "SKU名称2")
    private String skuName2;

    /**
     * SKU名称3
     */
    @ApiModelProperty(value = "SKU名称3")
    private String skuName3;

    /**
     * SKU名称4
     */
    @ApiModelProperty(value = "SKU名称4")
    private String skuName4;

    /**
     * SKU名称5
     */
    @ApiModelProperty(value = "SKU名称5")
    private String skuName5;

    /**
     * 系统自增长ID-客户信息
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    @Excel(name = "SKU数值")
    @Digits(integer = 6, fraction = 2, message = "sku数值整数位上限为6位，小数位上限为2位")
    @ApiModelProperty(value = "sku数值")
    private BigDecimal skuNumeralValue;

    /**
     * SKU类型编码
     */
    @Excel(name = "SKU属性类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU类型编码")
    private String skuType;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 颜色标准编码
     */
    @ApiModelProperty(value = "颜色标准编码")
    private String colorStandard;

    /**
     * 标准色号
     */
    @ApiModelProperty(value = "标准色号")
    private String standardColorCode;

    /**
     * RGB色号
     */
    @ApiModelProperty(value = "RGB色号")
    private String rgbCode;

    /**
     * 色系编码
     */
    @ApiModelProperty(value = "色系编码")
    private String colorSeries;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 上下装/套装
     */
    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装")
    private String upDownSuit;

    @Excel(name = "备注(SKU)")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

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

    @ApiModelProperty(value = "类型数组")
    @TableField(exist = false)
    private String[] skuTypeList;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] skuSidList;

    @ApiModelProperty(value = "颜色标准数组")
    @TableField(exist = false)
    private Long[] colorStandardList;

    @ApiModelProperty(value = "色系数组")
    @TableField(exist = false)
    private Long[] colorSeriesList;

    @ApiModelProperty(value = "处理状态数组")
    @TableField(exist = false)
    private Long[] handleStatusList;

    /**
     * 客户编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String shortName;

    /**
     * 上下装/套装
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上下装/套装")
    private String[] upDownSuitList;

    /**
     * 系统自增长ID-客户信息
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long[] customerSidList;

}

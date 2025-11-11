package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 品类规划信息对象 s_dev_product_plan
 *
 * @author qhq
 * @date 2021-11-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_dev_product_plan")
public class DevProductPlan extends EmsBaseEntity{

        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-品类规划信息")
    private Long productPlanSid;

        @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] productPlanSidList;

        @Excel(name="品类规划编码",sort = 1)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品类规划编码")
    private Long productPlanCode;

        /** 产品季sid */
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

        @TableField(exist = false)
        @ApiModelProperty(value = "产品季sidList")
    private Long[] productSeasonSidList;

        /** 年份（数据字典的键值或配置档案的编码） */
        @ApiModelProperty(value = "年份（数据字典的键值或配置档案的编码）")
    private String year;

        /** 季节（数据字典的键值或配置档案的编码） */
        @Excel(name = "季节",dictType = "s_season",sort = 3)
        @ApiModelProperty(value = "季节（数据字典的键值或配置档案的编码）")
    private String seasonCode;

    @ApiModelProperty(value = "季节数组")
    @TableField(exist = false)
    private String[] seasonCodeList;

        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料分类sid（物料分类/商品分类/服务分类）")
    private Long materialClassSid;

        /** 公司档案sid */
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案sid")
    private Long companySid;

    @Excel(name = "公司",sort = 5)
        @TableField(exist = false)
        private String companyName;

        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    @Excel(name = "品牌",sort = 6)
    @TableField(exist = false)
        private String brandName;

        /** 公司品标sid */
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品标sid")
    private Long companyBrandMarkSid;

    @Excel(name = "品标",sort = 7)
    @TableField(exist = false)
        private String brandMarkName;

        /** 规划款数量 */
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "规划款数量")
    private Long planQuantity;

        /** 款式（数据字典的键值或配置档案的编码） */
        @Excel(name = "款式",dictType = "s_kuan_type",sort = 8)
        @ApiModelProperty(value = "款式（数据字典的键值或配置档案的编码）")
    private String kuanType;

        /** 风格（数据字典的键值或配置档案的编码） */
        @Excel(name = "风格",dictType = "s_style",sort = 9)
        @ApiModelProperty(value = "风格（数据字典的键值或配置档案的编码）")
    private String style;

        /** 系列（数据字典的键值或配置档案的编码） */
        @Excel(name = "系列",dictType = "s_series",sort = 10)
        @ApiModelProperty(value = "系列（数据字典的键值或配置档案的编码）")
    private String series;

        /** 内外下（数据字典的键值或配置档案的编码） */
        @Excel(name = "内外下",dictType = "s_iou_type",sort = 11)
        @ApiModelProperty(value = "内外下（数据字典的键值或配置档案的编码）")
    private String inOutUnder;

        /** 上市月份（数据字典的键值或配置档案的编码） */
        @Excel(name = "上市月份",dictType = "s_month",sort = 13)
        @ApiModelProperty(value = "上市月份（数据字典的键值或配置档案的编码）")
    private String onsaleMonth;

        /** 批次(周)（数据字典的键值或配置档案的编码） */
        @Excel(name = "批次(周)",sort = 12)
        @ApiModelProperty(value = "批次(周)（数据字典的键值或配置档案的编码）")
    private String batchWeek;

        /** 产品包sid */
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品包sid")
    private Long productPackageSid;

        /** 目标成本(元) */
        @Excel(name = "目标成本(元)",sort = 15)
        @ApiModelProperty(value = "目标成本(元)")
    private BigDecimal costTarget;

        /** 目标零售价(元) */
        @Excel(name = "目标零售价(元)",sort = 16)
        @ApiModelProperty(value = "目标零售价(元)")
    private BigDecimal costRetailPrice;

        /** 加价率（存值，即：不含百分号，如20%，就存0.2） */
        @Excel(name = "加价率",sort = 17)
        @ApiModelProperty(value = "加价率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal markupRate;

        /** 版型类型（数据字典的键值或配置档案的编码） */
        @Excel(name = "版型类型",dictType = "s_model_type",sort = 14)
        @ApiModelProperty(value = "版型类型（数据字典的键值或配置档案的编码）")
    private String modelType;

    @ApiModelProperty(value = "版型类型编码List")
    @TableField(exist = false)
    private String[] modelTypeList;

        /** 主推项 */
        @ApiModelProperty(value = "主推项")
    private String zhutuixiang;

        /** 营销推广波次 */
        @ApiModelProperty(value = "营销推广波次")
    private String tuiguangbociYx;

            /** 启用/停用状态（数据字典的键值或配置档案的编码） */
        @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用状态",dictType = "s_valid_flag",sort = 18)
        @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

        /** 处理状态（数据字典的键值或配置档案的编码） */
        @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态",dictType = "s_handle_status",sort = 19)
        @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

        private String[] handleStatusList;

        /** 创建人账号（用户账号） */
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号List")
    private String[] creatorAccountList;

        /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd",sort = 21)
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

        /** 更新人账号（用户账号） */
        @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

        /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd",sort = 23)
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

        /** 确认人账号（用户账号） */
        @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

        /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd",sort = 25)
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

        /** 数据源系统（数据字典的键值或配置档案的编码） */
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

        /** 附件 */
        @TableField(exist = false)
    @ApiModelProperty(value = "附件")
    private List<DevProductPlanAttach> athList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    @Excel(name="创建人",sort = 20)
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人名称")
    @Excel(name="更新人",sort = 22)
    private String updaterAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人名称")
    @Excel(name="确认人",sort = 24)
    private String confirmerAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    @Excel(name = "产品季",sort = 2)
    private String productSeasonName;

    @Excel(name = "商品分类",sort = 4)
    @TableField(exist = false)
    @ApiModelProperty(value = "商品分类")
    private String materialClassName;
}

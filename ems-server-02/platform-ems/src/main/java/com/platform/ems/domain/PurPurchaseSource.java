package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.base.HandleStatusInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购货源清单对象 s_pur_purchase_source
 */
@Accessors(chain = true)
@Data
@ApiModel
@TableName(value = "s_pur_purchase_source")
public class PurPurchaseSource extends EmsBaseEntity implements HandleStatusInfo {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    String clientId;

    /**
     * 系统ID-货源清单信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-货源清单信息")
    Long purchaseSourceSid;

    /**
     * 货源信息号
     */
    @Excel(name = "货源信息记录号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "货源信息记录号")
    Long sourceInfoId;

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value = "商品/物料编码")
    String materialCode;

    @TableField(exist = false)
    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "商品/物料名称")
    String materialName;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    String vendorName;


    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    String vendorCode;


    /**
     * 供方编码(物料&商品)
     */
    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码")
    String supplierProductCode;

    /**
     * 是否默认供应商
     */
    @Excel(name = "是否默认供应商",
           dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否默认供应商")
    String isDefault;


    /**
     * 有效期（起）
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(起)",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    Date startDate;

    /**
     * 有效期（止）
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(止)",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（止）")
    Date endDate;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    String companyName;

    /**
     * 最低起订量
     */
    @Excel(name = "最低起订量")
    @ApiModelProperty(value = "最低起订量")
    BigDecimal minQuantity;


    /**
     * 大货生产周期(天)
     */
    @Excel(name = "大货生产周期(天)")
    @ApiModelProperty(value = "大货生产周期(天)")
    Integer purchaseDays;


    /**
     * 开发周期(天)
     */
    @Excel(name = "开发周期(天)")
    @ApiModelProperty(value = "开发周期(天)")
    Integer devDays;


    /**
     * 供应商的上游供货方
     */
    @Excel(name = "供应商的上游供货方")
    @ApiModelProperty(value = "供应商的上游供货方")
    String preSupplierName;


    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用",
           dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    String status;


    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态",
           dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    String handleStatus;

    @Excel(name = "备注")
    String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    @TableField(exist = false)
    String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField
    @ApiModelProperty(value = "创建日期")
    Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人")
    @TableField(exist = false)
    String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改日期")
    Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    @TableField(exist = false)
    String confirmerAccountName;


    @Excel(name = "确认日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    Date confirmDate;


    /**
     * 系统ID-供应商档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-供应商档案")
    @NotNull(message = "供应商不能为空")
    Long vendorSid;

    /**
     * 系统ID-物料档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料档案")
    Long materialSid;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    Long companyBrandSid;

    /**
     * 系统ID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    Long companySid;

    /**
     * 采购组织编码
     */
    @ApiModelProperty(value = "采购组织编码")
    String purchaseOrg;


    /**
     * 合同号/协议号sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "合同号/协议号sid")
    Long ContractSid;


    /**
     * 创建人账号
     */
    String creatorAccount;


    /**
     * 更新人账号
     */
    @TableField
    @ApiModelProperty(value = "更新人账号")
    String updaterAccount;


    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    String confirmerAccount;


    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    String dataSourceSys;


    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    Integer pageSize;


    /**
     * 公司代码
     */
    @ApiModelProperty(value = "公司代码")
    String companyCode;


    /**
     * 公司品牌名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌名称")
    String brandName;

    /**
     * 公司品牌编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌编码")
    Long brandCode;


    /**
     * 采购合同号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购合同号")
    String purchaseContractCode;

    /**
     * 系统ID-供应商档案
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-供应商档案")
    Long[] vendorSidList;

    /**
     * 系统ID-公司档案list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    Long[] companySidList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    String[] handleStatusList;

    /**
     * 货源清单信息sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "货源清单信息sids")
    Long[] purchaseSourceSids;

    /**
     * 物料/商品/服务sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品/服务sids")
    List<String> materialSids;

    /**
     * 采购货源供方SKU编码对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购货源供方SKU编码对象")
    List<PurMaterialSkuVencode> purMaterialSkuVencodeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购货源供方SKU编码对象")
    List<PurPurchaseSource> purPurchaseSourceList;

    public boolean isTheDefault() {
        return ConstantsEms.YES.equals(isDefault);
    }
}

package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 服务采购验收单对象 s_pur_service_acceptance
 *
 * @author linhongwei
 * @date 2021-04-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_service_acceptance")
public class PurServiceAcceptance  extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统自增长ID-采购服务验收单 */
    @TableId
        @Excel(name = "系统自增长ID-采购服务验收单")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购服务验收单")
    private Long serviceAcceptanceSid;

    /** 采购服务验收单号 */
    @ApiModelProperty(value = "采购服务验收单号")
    private Long serviceAcceptanceCode;

    /** 单据类型编码 */
        @Excel(name = "单据类型编码")
        @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    /** 业务类型编码 */
        @Excel(name = "业务类型编码")
        @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    /** 单据日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /** 验收状态 */
        @Excel(name = "验收状态")
        @ApiModelProperty(value = "验收状态")
    private String acceptStatus;

    /** 预确认日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "预确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "预确认日期")
    private Date expectedAcceptDate;

    /** 验收人 */
        @Excel(name = "验收人")
        @ApiModelProperty(value = "验收人")
    private String accepter;

    /** 实际确认日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "实际确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际确认日期")
    private Date actualAcceptDate;

    /** 系统自增长ID-采购订单 */
        @Excel(name = "系统自增长ID-采购订单")
        @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private Long purchaseOrderSid;

    /** 处理状态 */
        @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态",dictType = "s_handle_status")
        @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
        @Excel(name = "创建人账号")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
        @Excel(name = "更新人账号")
        @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号 */
        @Excel(name = "确认人账号")
        @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
        @Excel(name = "数据源系统")
        @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "预确认日期开始")
    private Date expectedAcceptBeginTime;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "预确认日期结束")
    private Date expectedAcceptEndTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    /** 服务采购验收单sids */
    @TableField(exist = false)
    @ApiModelProperty(value = "服务采购验收单sids")
    private Long[] serviceAcceptanceSids;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-供应商信息")
    private Long vendorSid;

    /** 供应商编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    /** 供应商名称 */
    @TableField(exist = false)
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;

    /** 公司代码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private Long companyCode;

    @Excel(name = "公司名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织")
    @Excel(name = "采购组织")
    private String purchaseOrg;

    @TableField(exist = false)
    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    @ApiModelProperty(value = "采购员")
    @Excel(name = "采购员")
    @TableField(exist = false)
    private String buyer;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    private Long productSeasonSid;

    /** 产品季编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @Excel(name = "产品季名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "采购订单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料（商品/服务）sid")
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private Long materialCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    /** 客户编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @Excel(name = "客户名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /** 采购订单sids */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单sids")
    private List<Long> purchaseOrderSids;

    /** 单据类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /** 业务类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-供应商信息list")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司档案list")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织list")
    private String[] purchaseOrgList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-产品季档案list")
    private Long[] productSeasonSidList;

    /** 验收状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "验收状态list")
    private String[] acceptStatusList;

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 服务采购确认单-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "服务采购确认单-明细对象")
    private List<PurServiceAcceptanceItem> purServiceAcceptanceItemList;

    /**
     * 服务采购确认单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "服务采购确认单-附件对象")
    private List<PurServiceAcceptanceAttachment> purServiceAcceptanceAttachmentList;

    /**
     * 服务采购确认单-合作伙伴对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "服务采购确认单-合作伙伴对象")
    private List<PurServiceAcceptancePartner> purServiceAcceptancePartnerList;


}

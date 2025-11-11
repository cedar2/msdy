package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 服务销售验收单对象 s_sal_service_acceptance
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sal_service_acceptance")
public class SalServiceAcceptance  extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统自增长ID-销售服务验收单 */
    @TableId
    @Excel(name = "系统自增长ID-销售服务验收单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售服务验收单")
    private Long serviceAcceptanceSid;

    /** 销售服务验收单号 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售服务验收单号")
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

    /** 信用类型 */
    @Excel(name = "信用类型")
    @ApiModelProperty(value = "信用类型")
    private String creditType;

    /** 预验收日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "预验收日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "预验收日期")
    private Date expectedAcceptDate;

    /** 实际验收日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "实际验收日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际验收日期")
    private Date actualAcceptDate;

    /** 验收人 */
    @Excel(name = "验收人")
    @ApiModelProperty(value = "验收人")
    private String accepter;

    /** 系统自增长ID-销售订单 */
    @Excel(name = "系统自增长ID-销售订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单")
    private Long salesOrderSid;

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

    @ApiModelProperty(value = "预验收日期开始时间")
    @TableField(exist = false)
    private String expectedAcceptBeginTime;

    @ApiModelProperty(value = "预验收日期结束时间")
    @TableField(exist = false)
    private String expectedAcceptEndTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    /** 服务销售验收单sids */
    @TableField(exist = false)
    @ApiModelProperty(value = "服务销售验收单sids")
    private Long[] serviceAcceptanceSids;

    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    @Excel(name = "公司名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "系统自增长ID-客户信息")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @Excel(name = "客户名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @Excel(name = "产品季名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "销售订单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @Excel(name = "销售员")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @Excel(name = "销售渠道",dictType = "s_sale_channel")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道")
    private String businessChannel;

    @Excel(name = "销售部门")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门")
    private String saleDepartment;

    @Excel(name = "销售组")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组")
    private String saleGroup;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织编码")
    private String saleOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private Long materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）sid")
    private Long materialSid;

    /** 销售订单sids */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单sids")
    private List<Long> salesOrderSids;

    /** 单据类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /** 业务类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-客户信息list")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司档案list")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道list")
    private String[] businessChannelList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-产品季档案list")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织编码list")
    private String[] saleOrgList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售部门")
    private String[] saleDepartmentList;

    /** 验收状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "验收状态list")
    private String[] acceptStatusList;

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 服务销售验收单-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "服务销售验收单-明细对象")
    private List<SalServiceAcceptanceItem> salServiceAcceptanceItemList;

    /**
     * 服务销售验收单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "服务销售验收单-附件对象")
    private List<SalServiceAcceptanceAttachment> salServiceAcceptanceAttachmentList;

    /**
     * 服务销售验收单-合作伙伴对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "服务销售验收单-合作伙伴对象")
    private List<SalServiceAcceptancePartner> salServiceAcceptancePartnerList;

    @TableField(exist = false)
    @ApiModelProperty(value = "")
    private List<Long> saleDeductionSids;

}

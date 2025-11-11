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
 * 需求单对象 s_req_require_doc
 *
 * @author linhongwei
 * @date 2021-04-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_req_require_doc")
public class ReqRequireDoc  extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统自增长ID-需求单 */
    @TableId
    @Excel(name = "系统自增长ID-需求单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-需求单")
    private Long requireDocSid;

    /** 需求单号 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "需求单号")
    private Long requireDocCode;

    /** 单据类型编码 */
    @Excel(name = "单据类型编码")
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    /** 需求方类型（数据字典的键值）客户/供应商 */
    @Excel(name = "需求方类型（数据字典的键值）客户/供应商")
    @ApiModelProperty(value = "需求方类型（数据字典的键值）客户/供应商")
    private String requireOrgType;

    /** 业务渠道/销售渠道（数据字典的键值或配置档案的编码） */
    @Excel(name = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "业务渠道/销售渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    /** 单据日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /** 系统自增长ID-公司档案 */
    @Excel(name = "系统自增长ID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;

    /** 公司品牌sid */
    @Excel(name = "公司品牌sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /** 系统自增长ID-产品季档案 */
    @Excel(name = "系统自增长ID-产品季档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-产品季档案")
    private Long productSeasonSid;

    /** 需求方 */
    @Excel(name = "需求方")
    @ApiModelProperty(value = "需求方")
    private Long requireOrg;

    /** 需求部门 */
    @Excel(name = "需求部门")
    @ApiModelProperty(value = "需求部门")
    private Long requireDepartment;

    /** 收货人 */
    @Excel(name = "收货人")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    /** 收货人联系电话 */
    @Excel(name = "收货人联系电话")
    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    /** 收货地址 */
    @Excel(name = "收货地址")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    /** 供货模式 */
    @Excel(name = "供货模式")
    @ApiModelProperty(value = "供货模式")
    private String supplyType;

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

    @ApiModelProperty(value = "单据日期开始时间")
    @TableField(exist = false)
    private String documentBeginTime;

    @ApiModelProperty(value = "单据日期结束时间")
    @TableField(exist = false)
    private String documentEndTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    /** 需求单sids */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求单sids")
    private Long[] requireDocSids;

    /** 销售订单号 */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    /** 物料（商品/服务）编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    /** 部门编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    /** 部门名称 */
    @TableField(exist = false)
    @Excel(name = "部门名称")
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    /** 公司名称 */
    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /** 产品季名称 */
    @TableField(exist = false)
    @Excel(name = "产品季名称")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /** 生产订单号 */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    /**
     * 采购订单号
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    /** 单据类型编码list */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    /** 需求方list */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求方list")
    private Long[] requireOrgList;

    /** 系统自增长ID-公司档案list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司档案list")
    private Long[] companySidList;

    /** 需求部门list */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求部门list")
    private Long[] requireDepartmentList;

    /** 系统自增长ID-产品季档案list */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-产品季档案list")
    private Long[] productSeasonSidList;

    /** 供货模式list */
    @TableField(exist = false)
    @ApiModelProperty(value = "供货模式list")
    private String[] supplyTypeList;

    /** 处理状态list */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 需求单明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求单明细对象")
    private List<ReqRequireDocItem> reqRequireDocItemList;

    /**
     * 需求单附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求单附件对象")
    private List<ReqRequireDocAttachment> reqRequireDocAttachmentList;
}

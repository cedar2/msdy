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
import com.platform.ems.util.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 外发加工收货单对象 s_del_outsource_delivery_note
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_del_outsource_delivery_note")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelOutsourceDeliveryNote extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-外发加工收货单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-外发加工收货单")
    private Long deliveryNoteSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] deliveryNoteSidList;

    /**
     * 外发加工收货单号
     */
    @Excel(name = "外发加工收货单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "外发加工收货单号")
    private Long deliveryNoteCode;

    /**
     * 系统SID-供应商信息（外发加工商）
     */
    @NotNull(message = "加工商不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商信息（外发加工商）")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商信息（外发加工商）")
    private Long[] vendorSidList;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @Excel(name = "加工商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 系统SID-工厂
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    /**
     * 工厂名称
     */
    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 单据类型编码code
     */
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型list")
    private String[] documentTypeList;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    /**
     * 业务类型编码code
     */
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码code")
    private String[] businessTypeList;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    /**
     * 货运单号
     */
    @Length(max = 100, message = "货运单号长度不能超过100个字符")
    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @TableField(exist = false)
    @Excel(name = "货运方")
    @ApiModelProperty(value = "货运方名称")
    private String carrierName;

    /**
     * 收货人
     */
    @Length(max = 30, message = "收货人长度不能超过30个字符")
    @Excel(name = "收货人")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    /**
     * 单据日期
     */
    @NotNull(message = "单据日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 收货人联系电话
     */
    @Phone
    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    /**
     * 收货地址
     */
    @Length(max = 600, message = "收货地址长度不能超过600个字符")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    /**
     * 运输单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "运输单号")
    private Long shippingOrderCode;

    /**
     * 货运方（承运商）
     */
    @ApiModelProperty(value = "货运方（承运商）")
    private String carrier;

    @TableField(exist = false)
    @ApiModelProperty(value = "货运方（承运商）")
    private Long[] carrierList;

    @TableField(exist = false)
    @ApiModelProperty(value = "货运方编码")
    private String carrierCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /**
     * 供方送货单号（供方收货单号）
     */
    @ApiModelProperty(value = "供方送货单号（供方收货单号）")
    private String supplierDeliveryCode;

    /**
     * 预计到货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "预计到货日期")
    private Date expectedArrivalDate;

    /**
     * 收货状态（数据字典的键值）
     */
    @ApiModelProperty(value = "入库状态（数据字典的键值）")
    private String inStockStatus;

    /**
     * 入库人（用户帐号）
     */
    @ApiModelProperty(value = "入库人（用户帐号）")
    private String operator;


    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 物料类别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "物料类别（数据字典的键值或配置档案的编码）")
    private String materialCategory;

    /** 当前审批节点名称 */
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    /**
     * 当前审批人id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人id")
    private String approvalUserId;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 供应商简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String shortName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 系统SID-产品季档案
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long productSeasonSid;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 产品季名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 采购员（用户名称）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购员（用户名称）")
    private String buyer;

    /**
     * 采购组（数据字典的键值）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购组（数据字典的键值）")
    private String purchaseGroup;

    /**
     * 入库人名称
     */
    @TableField(exist = false)
    private String nickName;

    /**
     * 工厂编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "最大行号")
    private Long maxItemNum;

    /**
     * 外发加工收货单sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工收货单sids")
    private List<Long> deliveryNoteSids;

    /**
     * 外发加工收货单-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工收货单-明细对象")
    private List<DelOutsourceDeliveryNoteItem> delOutsourceDeliveryNoteItemList;

    /**
     * 外发加工收货单-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "外发加工收货单-附件对象")
    private List<DelOutsourceDeliveryNoteAttachment> delOutsourceDeliveryNoteAttachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;
}

package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

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

import javax.validation.constraints.Digits;

import lombok.experimental.Accessors;

/**
 * 申请单-明细对象 s_req_purchase_require_item
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_req_purchase_require_item")
public class ReqPurchaseRequireItem extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-申请单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-申请单明细")
    private Long purchaseRequireItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-申请单明细")
    private Long[] purchaseRequireItemSidList;

    /**
     * 系统自增长ID-申请单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-申请单")
    private Long purchaseRequireSid;

    @TableField(exist = false)
    @Excel(name = "采购申请单号")
    @ApiModelProperty(value = "申请单号")
    private String purchaseRequireCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型编码list")
    private String[] documentTypeList;

    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型编码")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型编码list")
    private String[] businessTypeList;

    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-公司档案")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-公司档案list")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "申请部门")
    private Long requireDepartmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "申请部门")
    private Long[] requireDepartmentSidList;

    @TableField(exist = false)
    @Excel(name = "申请部门")
    @ApiModelProperty(value = "申请部门")
    private String requireDepartmentName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    private Long materialSid;

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @TableField(exist = false)
    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料商品主图片路径")
    private String picturePath;

    /**
     * 系统自增长ID-商品条码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品条码")
    private Long barcodeSid;

    /**
     * 商品条码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品条码")
    private Long barcodeCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku")
    private Long sku1Sid;

    @ApiModelProperty(value = "SKU1编码")
    private String sku1Code;

    @TableField(exist = false)
    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku")
    private Long sku2Sid;

    @ApiModelProperty(value = "SKU2编码")
    private String sku2Code;

    @TableField(exist = false)
    @Excel(name = "SKU2名称")
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    @Digits(integer = 6, fraction = 4, message = "申请量整数位上限为6位，小数位上限为4位")
    @Excel(name = "申请量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "申请量")
    private BigDecimal quantity;

    @TableField(exist = false)
    @Excel(name = "已转采购量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "已转采购量")
    private BigDecimal haveReferQuantity;

    @TableField(exist = false)
    @Excel(name = "待执行量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待执行量")
    private BigDecimal daiExecuteQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "待执行量大于0")
    private String daiExecuteQuantityBigZero;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期起")
    private String demandDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期止")
    private String demandDateEnd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "最晚需求日期")
    private Date latestDemandDate;

    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @Excel(name = "商品/物料类别", dictType = "s_material_category")
    @ApiModelProperty(value = "商品/物料类别")
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品/物料类别")
    private String[] materialCategoryList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-产品季档案")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案list")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @Excel(name = "需求季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @TableField(exist = false)
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 申请方
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "申请方")
    private Long requireOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "申请方list")
    private Long[] requireOrgList;

    /**
     * 供货模式（数据字典的键值）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供货模式（数据字典的键值）")
    private String supplyType;

    /**
     * 供货模式（数据字典的键值）list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供货模式（数据字典的键值）list")
    private String[] supplyTypeList;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更改人")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改日期")
    private Date updateDate;

}

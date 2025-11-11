package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.TecBomItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 销售订单排产进度报表 汇总明细
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SalSalesOrderItemTotalResponse {

    @TableField(exist = false)
    @Excel(name = "商品条码")
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    /**
     * 销售量
     */
    @Digits(integer = 7,fraction = 3, message = "销售量整数位上限为7位，小数位上限为3位")
    @Excel(name = "销售量")
    @ApiModelProperty(value = "销售量")
    @NotNull(message = "销售量不能为空")
    private BigDecimal quantity;

    /**
     * 税率
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    /** 单位换算比例（价格单位/基本单位） */
    @Digits(integer = 3,fraction = 2, message = "单位换算比例整数位上限为3位，小数位上限为2位")
    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;

    /**
     * 销售价(不含税)
     */
    @Excel(name = "销售价(不含税)")
    @ApiModelProperty(value = "销售价(不含税)")
    private BigDecimal salePrice;

    /**
     * 销售价(含税)
     */
    @Excel(name = "销售价(含税)")
    @ApiModelProperty(value = "销售价(含税)")
    private BigDecimal salePriceTax;

    /**
     * 折扣类型代码
     */
    @Excel(name = "折扣")
    @ApiModelProperty(value = "折扣类型代码")
    private String discountType;

    /**
     * 免费标识
     */
    @Excel(name = "免费")
    @ApiModelProperty(value = "免费标识")
    private String freeFlag;

    /**
     * 需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    @Excel(name = "负责生产工厂")
    @ApiModelProperty(value = "负责生产工厂名称")
    private String producePlantName;

    /**
     * 合同交期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    /*
     * 发货状态
     */
    @Excel(name = "发货状态", dictType = "s_delivery_status")
    @ApiModelProperty(value = "发货状态")
    private String deliveryStatus;

    @Excel(name = "出入库状态", dictType = "s_in_out_store_status")
    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Integer itemNum;


    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;


    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long materialSid;

    @Excel(name = "sku1(颜色)")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "sku1(尺码)")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku1Type;

    @ApiModelProperty(value = "仓库编码")
    @TableField(exist = false)
    private String storehouseCode;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    @TableField(exist = false)
    private String storehouseName;

    @ApiModelProperty(value = "库位编码")
    @TableField(exist = false)
    private String locationCode;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    @TableField(exist = false)
    private String locationName;

    /**
     * 销售订单号
     */
    @Excel(name = "销售订单号")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 销售员名称
     */
    @TableField(exist = false)
    @Excel(name = "销售员")
    private String nickName;

    /**
     * 销售员
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePerson;


    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    /**
     * 销售合同号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "销售合同号(纸质合同)")
    private String paperSaleContractCode;

    /**
     * 供料方式
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;

    /**
     * 销售模式
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    /**
     * 计量单位名称
     */
    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    /**
     * 销售单位名称
     */
    @TableField(exist = false)
    @Excel(name = "销售单位")
    @ApiModelProperty(value = "销售单位名称")
    private String unitPriceName;

    /**
     * 折扣类型名称
     */
    @TableField(exist = false)
    @Excel(name = "折扣类型名称")
    @ApiModelProperty(value = "折扣类型名称")
    private String discountTypeName;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型名称")
    private String documentTypeName;

    /**
     * 业务类型名称
     */
    @TableField(exist = false)
    @Excel(name = "业务类型")
    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    /**
     * 物料类型名称
     */
    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    /**
     * 特殊业务类别编码code，如：客户寄售结算
     */
    @TableField(exist = false)
    @Excel(name = "特殊业务类别编码code，如：客户寄售结算")
    @ApiModelProperty(value = "特殊业务类别编码code，如：客户寄售结算")
    private String specialBusCategory;

    /**
     * 合同名称
     */
    @TableField(exist = false)
    @Excel(name = "销售合同/协议号")
    @ApiModelProperty(value = "合同名称")
    private String contractName;


    @TableField(exist = false)
    @ApiModelProperty(value = "已排产量")
    private BigDecimal alreadyQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "待排产量")
    private BigDecimal notQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产状态")
    private String quantityStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产状态")
    private String[] quantityStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "已生产完工量")
    private BigDecimal completeQuantity;

    /** 物料分类名称 */
    @TableField(exist = false)
    @Excel(name = "物料分类名称")
    @ApiModelProperty(value = "物料分类名称")
    private String nodeName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 系统自增长ID-商品sku
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku")
    private Long sku1Sid;
    /**
     * 系统自增长ID-商品sku
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品sku")
    private Long sku2Sid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-商品条码")
    private Long barcodeSid;

    /**
     * 系统自增长ID-销售订单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单明细")
    @TableId
    private Long salesOrderItemSid;

    /**
     * 系统自增长ID-销售订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-销售订单")
    private Long salesOrderSid;

    /**
     * 基本计量单位
     */
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @ApiModelProperty(value = "是否首杠")
    private String isMakeShougang;

    @ApiModelProperty(value = "是否做首批（数据字典的键值或配置档案的编码）")
    private String  isMakeShoupi;
}

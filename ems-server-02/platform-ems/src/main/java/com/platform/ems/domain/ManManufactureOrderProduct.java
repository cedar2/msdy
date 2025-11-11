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
import com.platform.ems.domain.dto.response.ItemSummary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 生产订单-产品明细对象 s_man_manufacture_order_product
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_order_product")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureOrderProduct extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产订单-产品明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单-产品明细")
    private Long manufactureOrderProductSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] manufactureOrderProductSidList;

    /**
     * 系统SID-生产订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-生产订单数组")
    private Long[] manufactureOrderSidList;

    @Excel(name = "合同交期")
    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期")
    private String contractDate;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期日期格式用来日期排序")
    private Date contractDatedate;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联销售订单的下单季")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联销售订单的下单季")
    private String productSeasonCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期开始")
    private String contractDateBeginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "合同交期结束")
    private String contractDateEndTime;

    @TableField(exist = false)
    @Excel(name = "计划完工日期(整单)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期(整单)")
    private Date planEndDateMo;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂(整单)")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：客户")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sid")
    private Long materialSid;

    @Excel(name = "商品编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @TableField(exist = false)
    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @Excel(name = "完工状态(商品)", dictType = "s_complete_status")
    @ApiModelProperty(value = "完工状态")
    private String completeStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：完工状态")
    private String[] completeStatusList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sku1")
    private Long sku1Sid;

    @Excel(name = "SKU1(颜色)")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1(颜色)")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1编码")
    private String sku1Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1类型")
    private String sku1Type;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品sku2")
    private Long sku2Sid;

    @Excel(name = "SKU2(尺码)")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2(尺码)")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2编码")
    private String sku2Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2类型")
    private String sku2Type;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    @Digits(integer = 8, fraction = 3, message = "计划产量整数位上限为8位，小数位上限为3位")
    @Excel(name = "计划产量(商品)")
    @ApiModelProperty(value = "计划产量/本次排产量")
    private BigDecimal quantity;

    @Excel(name = "已完工量(商品)")
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单商品明细报表：已完工量(商品)")
    private String completeSpQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单商品明细报表：待完工量(商品) = 已完工量 - 计划产量")
    private String daiQuantity;

    @Excel(name = "实裁量")
    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单商品明细报表：实裁量")
    private String isCaichuangQuantity;

    @Excel(name = "计划产量(整单)")
    @TableField(exist = false)
    @ApiModelProperty(value = "计划产量(整单)")
    private BigDecimal orderQuantity;

    @ApiModelProperty(value = "累计已完工量")
    private BigDecimal completeQuantity;

    @ApiModelProperty(value = "累计已入库量")
    private BigDecimal inStoreQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "未入库量")
    private BigDecimal notStoreQuantity;

    @ApiModelProperty(value = "入库状态（数据字典的键值或配置档案的编码）")
    private String inStoreStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "入库状态（多选）")
    private String[] inStoreStatusList;

    @Excel(name = "计划投产日期(商品)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划投产日期")
    private Date planStartDate;

    @Excel(name = "计划完工日期(商品)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    private Date planEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期开始")
    private String planEndDateBeginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期结束")
    private String planEndDateEndTime;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售订单")
    private Long salesOrderSid;

    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    @Excel(name = "下单状态(辅料)", dictType = "s_material_order_status")
    @ApiModelProperty(value = "辅料_采购下单状态")
    private String flCaigouxiadanStatus;

    @TableField(exist = false)
    @Excel(name = "下单状态(面料)", dictType = "s_material_order_status")
    @ApiModelProperty(value = "面料_采购下单状态")
    private String mlCaigouxiadanStatus;

    /**
     * 原材料_备料状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "原材料_备料状态（数据字典的键值或配置档案的编码）")
    private String yclBeiliaoStatus;

    /**
     * 原材料_需购状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "原材料_需购状态（数据字典的键值或配置档案的编码）")
    private String yclXugouStatus;

    /**
     * 原材料_领料状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "原材料_领料状态（数据字典的键值或配置档案的编码）")
    private String yclLingliaoStatus;

    /**
     * 原材料_齐套状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "原材料_齐套状态（数据字典的键值或配置档案的编码）")
    private String yclQitaoStatus;

    /**
     * 原材料_齐套说明（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "原材料_齐套说明（数据字典的键值或配置档案的编码）")
    private String yclQitaoRemark;

    /**
     * 原材料_采购下单状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "原材料_采购下单状态（数据字典的键值或配置档案的编码）")
    private String yclCaigouxiadanStatus;

    @ApiModelProperty(value = "计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    @Excel(name = "基本单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同基本计量单位")
    private String saleUnitBase;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售单位名称")
    private String saleUnitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价单位")
    private String unitPrice;

    @Excel(name = "销售单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售单位")
    private String unitPriceName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：销售员")
    private String[] salePersonList;

    @Excel(name = "销售员")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售订单明细")
    private Long salesOrderItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-销售订单明细")
    private Long[] salesOrderItemSidList;

    @Excel(name = "销售订单行号")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售订单行号")
    private String saleItemNum;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询;处理状态")
    private String[] handleStatusList;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "启停状态")
    private String status;

    /**
     * 行号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 实际开始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际投产日期")
    private Date actualStartDate;

    /**
     * 实际完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完工日期")
    private Date actualEndDate;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "预警：红色0绿色1黄橙2蓝色3")
    private String light;

    @Excel(name = "到期提醒天数")
    @ApiModelProperty(value = "即将到期预警天数(生产订单-商品) ")
    private Integer toexpireDaysScddSp;

    @TableField(exist = false)
    @ApiModelProperty(value = "即将到期预警天数(生产订单-事项) ")
    private Integer toexpireDaysScdd;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期")
    private String demandDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "最晚需求日期")
    private String latestDemandDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期开始")
    private String demandDateBeginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期结束")
    private String demandDateEndTime;

    /**
     * 吊牌零售价（元）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "吊牌零售价（元）")
    private BigDecimal retailPrice;

    /**
     * 销售量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售量")
    private BigDecimal saleQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "各尺码排产量小计")
    private BigDecimal quantitySum;

    /**
     * 各尺码排产量小计
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "各尺码排产量")
    private List<ItemSummary> itemSummaryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

}

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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 商品成本核算-工价成本明细对象 s_cos_product_cost_labor
 *
 * @author qhq
 * @date 2021-04-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_product_cost_labor")
public class CosProductCostLabor extends EmsBaseEntity {
    /**
     * 客户端口号
     */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-物料成本核算工价成本明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料成本核算工价成本明细")
    private Long productCostLaborSid;

    /**
     * 系统ID-物料成本核算
     */
    @Excel(name = "系统ID-物料成本核算")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料成本核算")
    private Long productCostSid;

    /**
     * 工价类型sid
     */
    @Excel(name = "工价类型sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工价类型sid")
    private Long laborTypeSid;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工序")
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序")
    private String processName;

    /**
     * 工价项sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工价项sid")
    private Long laborTypeItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工价项名称")
    private String laborTypeItemName;


    /**
     * 是否含税
     */
    @Excel(name = "是否含税")
    @ApiModelProperty(value = "是否含税")
    private String isIncludeTax;

    /**
     * 是否税率项（停用）
     */
    @Excel(name = "是否税率项")
    @ApiModelProperty(value = "是否税率项")
    private String isTaxRate;

    /**
     * 内部成本价(含税)
     */
    @Excel(name = "内部成本价(含税)")
    @ApiModelProperty(value = "内部成本价(含税)")
    private BigDecimal innerPriceTax;

    /**
     * 内部成本价(不含税)
     */
    @Excel(name = "内部成本价(不含税)")
    @ApiModelProperty(value = "内部成本价(不含税)")
    private BigDecimal innerPrice;

    /**
     * 报价(含税)
     */
    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    /**
     * 报价(不含税)
     */
    @Excel(name = "报价(不含税)")
    @ApiModelProperty(value = "报价(不含税)")
    private BigDecimal quotePrice;

    /**
     * 核定价(含税)
     */
    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    /**
     * 核定价(不含税)
     */
    @Excel(name = "核定价(不含税)")
    @ApiModelProperty(value = "核定价(不含税)")
    private BigDecimal checkPrice;

    /**
     * 确认价(含税)
     */
    @Excel(name = "确认价(含税)")
    @ApiModelProperty(value = "确认价(含税)")
    private BigDecimal confirmPriceTax;

    /**
     * 确认价(不含税)
     */
    @Excel(name = "确认价(不含税)")
    @ApiModelProperty(value = "确认价(不含税)")
    private BigDecimal confirmPrice;

    /**
     * 备注（内部成本价）
     */
    @Excel(name = "备注（内部成本价）")
    @ApiModelProperty(value = "备注（内部成本价）")
    private String remarkInner;

    /**
     * 备注（报价）
     */
    @Excel(name = "备注（报价）")
    @ApiModelProperty(value = "备注（报价）")
    private String remarkQuote;

    /**
     * 备注（核定价）
     */
    @Excel(name = "备注（核定价）")
    @ApiModelProperty(value = "备注（核定价）")
    private String remarkCheck;

    /**
     * 备注（确认价）
     */
    @Excel(name = "备注（确认价）")
    @ApiModelProperty(value = "备注（确认价）")
    private String remarkConfirm;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    private Long serialNum;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;


    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 是否其它项（数据字典的键值）
     */
    @Excel(name = "是否其它项（数据字典的键值）")
    @ApiModelProperty(value = "是否其它项（数据字典的键值）")
    private String isOther;

    /**
     * 是否税额项（数据字典的键值）
     */
    @Excel(name = "是否税额项（数据字典的键值）")
    @ApiModelProperty(value = "是否税额项（数据字典的键值）")
    private String isTax;

    /**
     * 是否管理费倍率项（数据字典的键值）
     */
    @Excel(name = "是否管理费倍率项（数据字典的键值）")
    @ApiModelProperty(value = "是否管理费倍率项（数据字典的键值）")
    private String isManageChargeRate;

    /**
     * 是否管理费金额项（数据字典的键值）
     */
    @Excel(name = "是否管理费金额项（数据字典的键值）")
    @ApiModelProperty(value = "是否管理费金额项（数据字典的键值）")
    private String isManageCharge;

    /**
     * 是否成品利润率项（数据字典的键值）
     */
    @Excel(name = "是否成品利润率项（数据字典的键值）")
    @ApiModelProperty(value = "是否成品利润率项（数据字典的键值）")
    private String isProfitRateProduct;

    /**
     * 是否成品利润额项（数据字典的键值）
     */
    @Excel(name = "是否成品利润额项（数据字典的键值）")
    @ApiModelProperty(value = "是否成品利润额项（数据字典的键值）")
    private String isProfitProduct;

    /**
     * 是否小计/总计项（数据字典的键值）
     */
    @Excel(name = "是否小计/总计项（数据字典的键值）")
    @ApiModelProperty(value = "是否小计/总计项（数据字典的键值）")
    private String isSubtotal;

    /**
     * 是否可编辑（数据字典的键值）
     */
    @Excel(name = "是否可编辑（数据字典的键值）")
    @ApiModelProperty(value = "是否可编辑（数据字典的键值）")
    private String isEdit;

    /**
     * 计算公式-内部价
     */
    @Excel(name = "计算公式-内部价")
    @ApiModelProperty(value = "计算公式-内部价")
    private String subtotalFormulaInner;

    /**
     * 计算公式-报价
     */
    @Excel(name = "计算公式-报价")
    @ApiModelProperty(value = "计算公式-报价")
    private String subtotalFormulaQuote;

    /**
     * 计算公式-核定价
     */
    @Excel(name = "计算公式-核定价")
    @ApiModelProperty(value = "计算公式-核定价")
    private String subtotalFormulaCheck;

    /**
     * 计算公式-确认价
     */
    @Excel(name = "计算公式-确认价")
    @ApiModelProperty(value = "计算公式-确认价")
    private String subtotalFormulaConfirm;

    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "是否甲供料物料（数据字典的键值）")
    @ApiModelProperty(value = "是否甲供料物料（数据字典的键值）")
    private String isPartyAmaterialFormula;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "工价类型名称")
    @TableField(exist = false)
    private String laborTypeName;

    @ApiModelProperty(value = "新建-工价成本模板对象-其他")
    @TableField(exist = false)
    private List<CosProductCostLaborOther> laborItemOtherList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品成本核算-工价成本其它项明细表")
    private CosProductCostLaborOther cosProductCostLaborOther;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @ApiModelProperty(value = "生产方式（数据字典的键值或配置档案的编码），自产、外发")
    @TableField(exist = false)
    private String productionMode;

    private String otherItemName;


    /**
     * 风险系数（存值，即：不含百分号，如20%，就存0.2）
     */

    @Excel(name = "确认风险系数（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "风险系数（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal confirmFengxianRate;


    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */

    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;


    /**
     * 报价服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）
     */

    @Excel(name = "报价服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）")
    @ApiModelProperty(value = "报价服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）")
    private BigDecimal quoteFuwuRate;


    /**
     * 核定服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）
     */

    @Excel(name = "核定服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）")
    @ApiModelProperty(value = "核定服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）")
    private BigDecimal checkFuwuRate;


    /**
     * 确认服务费率（存值，即：不含百分号，如20%，就存0.2）
     */

    @Excel(name = "确认服务费率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "确认服务费率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal confirmFuwuRate;


    /**
     * 风险金额
     */

    @Excel(name = "风险金额")
    @ApiModelProperty(value = "风险金额")
    private BigDecimal fengxianCostTax;


    /**
     * 报价服务费（冗余）
     */

    @Excel(name = "报价服务费（冗余）")
    @ApiModelProperty(value = "报价服务费（冗余）")
    private BigDecimal quoteFuwuCostTax;


    /**
     * 核定服务费（冗余）
     */

    @Excel(name = "核定服务费（冗余）")
    @ApiModelProperty(value = "核定服务费（冗余）")
    private BigDecimal checkFuwuCostTax;


    /**
     * 确认服务费
     */
    @Excel(name = "确认服务费")
    @ApiModelProperty(value = "确认服务费")
    private BigDecimal confirmFuwuCostTax;

    @ApiModelProperty(value = "是否可编辑项(风险系数)（数据字典的键值）")
    private String isEditFengxianxishu;

    @ApiModelProperty(value = "是否可编辑项(服务费率)（数据字典的键值）")
    private String isEditFuwufeilv;

    @ApiModelProperty(value = "是否可编辑项(税率)（数据字典的键值）")
    private String isEditShuilv;

    @ApiModelProperty(value = "物料采购类型(配置档案编码)")
    private String purchaseType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料采购类型(配置档案名称)")
    private String purchaseTypeName;

    @ApiModelProperty(value = "成本工价类型编码（数据字典的键值或配置档案的编码）")
    private String laborTypeCode;

    @ApiModelProperty(value = "成本工价项编码（数据字典的键值或配置档案的编码）")
    private String laborTypeItemCode;


    @ApiModelProperty(value = "风险金额(报价)")
    private String itemQuoteFengxianCostTax;

    @ApiModelProperty(value = "风险金额(成本价)")
    private String itemInnerFengxianCostTax;

    @ApiModelProperty(value = "核定风险金额(含税)")
    private String itemCheckFengxianCostTax ;

    @ApiModelProperty(value = " 风险金额(确认价)")
    private String itemConfirmFengxianCostTax ;

    @ApiModelProperty(value = " 服务费(报价)")
    private String itemQuoteFuwuCostTax ;

    @ApiModelProperty(value = "服务费(成本价)")
    private String itemInnerFuwuCostTax ;

    @ApiModelProperty(value = "核定服务费(含税)")
    private String itemCheckFuwuCostTax ;

    @ApiModelProperty(value = "服务费(确认价)")
    private String itemConfirmFuwuCostTax ;

}

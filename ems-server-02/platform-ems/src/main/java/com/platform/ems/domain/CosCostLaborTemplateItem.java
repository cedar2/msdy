package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

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


/**
 * 商品成本核算-工价成本模板-明细对象 s_cos_cost_labor_template_item
 *
 * @author qhq
 * @date 2021-04-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_cost_labor_template_item")
public class CosCostLaborTemplateItem extends EmsBaseEntity {
    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统ID-物料成本核算模板（明细）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料成本核算模板（明细）")
    private Long costLaborTemplateItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] costLaborTemplateItemSidList;
    /**
     * 系统ID-物料成本核算模板（主表）
     */
    @Excel(name = "系统ID-物料成本核算模板（主表）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料成本核算模板（主表）")
    private Long costLaborTemplateSid;

    /**
     * 系统SID-工价项sid
     */
    @NotEmpty(message = "工价项不可为空")
    @Excel(name = "系统SID-工价项sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工价项sid")
    private Long laborTypeItemSid;

    @TableField(exist = false)
    private BigDecimal innerPrice;

    @TableField(exist = false)
    private BigDecimal quotePrice;

    @TableField(exist = false)
    private BigDecimal confirmPrice;

    @TableField(exist = false)
    private BigDecimal checkPrice;

    @TableField(exist = false)
    private BigDecimal innerPriceTax;

    @TableField(exist = false)
    private BigDecimal quotePriceTax;

    @TableField(exist = false)
    private BigDecimal confirmPriceTax;

    @TableField(exist = false)
    private BigDecimal checkPriceTax;

    /**
     * 系统SID-工价类型sid
     */
    @NotEmpty(message = "工价类型不可为空")
    @Excel(name = "系统SID-工价类型sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工价类型sid")
    private Long laborTypeSid;

    /**
     * 工价/费用项名称
     */
    @Excel(name = "工价/费用项名称")
    @ApiModelProperty(value = "工价/费用项名称")
    private String laborTypeItemName;

    @Excel(name = "工价类型名称")
    @ApiModelProperty(value = "工价类型名称")
    @TableField(exist = false)
    private String laborTypeName;

    @ApiModelProperty(value = "工价类型编码")
    private String laborTypeCode;

    @ApiModelProperty(value = "工价项编码")
    private String laborTypeItemCode;

    /**
     * 工价/费用项名称
     */
    @Excel(name = "工价/费用项名称")
    @ApiModelProperty(value = "用来工价/费用项名称")
    @TableField(exist = false)
    private String itemName;

    @ApiModelProperty(value = "物料采购类型(配置档案编码)")
    private String purchaseType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料采购类型(配置档案名称)")
    private String purchaseTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料采购类型(编码查询多选)")
    private String[] purchaseTypeList;

    /**
     * 是否含税（数据字典的键值）
     */
    @Excel(name = "是否含税（数据字典的键值）")
    @ApiModelProperty(value = "是否含税（数据字典的键值）")
    private String isIncludeTax;

    /**
     * 是否可编辑（数据字典的键值）
     */
    @Excel(name = "是否可编辑（数据字典的键值）")
    @ApiModelProperty(value = "是否可编辑（数据字典的键值）")
    private String isEdit;

    /**
     * 是否成品利润率项（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否成品利润率项（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否成品利润率项（数据字典的键值或配置档案的编码）")
    private String isProfitRateProduct;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    private BigDecimal serialNum;

    /**
     * 是否成品利润额项（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否成品利润额项（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否成品利润额项（数据字典的键值或配置档案的编码）")
    private String isProfitProduct;

    /**
     * 是否税率项（数据字典的键值）
     */
    @Excel(name = "是否税率项（数据字典的键值）")
    @ApiModelProperty(value = "是否税率项（数据字典的键值）")
    private String isTaxRate;

    /**
     * 是否税额项（数据字典的键值）
     */
    @Excel(name = "是否税额项（数据字典的键值）")
    @ApiModelProperty(value = "是否税额项（数据字典的键值）")
    private String isTax;

    /**
     * 是否物料小计（含税）（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否物料小计（含税）（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否物料小计（含税）（数据字典的键值或配置档案的编码）")
    private String isMaterialFormula;

    /**
     * 是否其它项（数据字典的键值）
     */
    @Excel(name = "是否其它项（数据字典的键值）")
    @ApiModelProperty(value = "是否其它项（数据字典的键值）")
    private String isOther;

    @ApiModelProperty(value = "是否可编辑项(风险系数)（数据字典的键值）")
    private String isEditFengxianxishu;

    @ApiModelProperty(value = "是否可编辑项(服务费率)（数据字典的键值）")
    private String isEditFuwufeilv;

    @ApiModelProperty(value = "是否可编辑项(税率)（数据字典的键值）")
    private String isEditShuilv;

    /**
     * 计算公式-内部价
     */
    @Excel(name = "计算公式-内部价")
    @ApiModelProperty(value = "计算公式-内部价")
    private String subtotalFormulaInner;

    /**
     * 是否甲供料物料小计（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否甲供料物料小计（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否甲供料物料小计（数据字典的键值或配置档案的编码）")
    private String isPartyAMaterialFormula;

    /**
     * 计算公式-报价
     */
    @Excel(name = "计算公式-报价")
    @ApiModelProperty(value = "计算公式-报价")
    private String subtotalFormulaQuote;

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

    /**
     * 系统SID-工序（加工项）
     */
    @Excel(name = "系统SID-工序（加工项）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工序（加工项）")
    private Long processSid;

    /**
     * 是否后台计算项（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否后台计算项（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否后台计算项（数据字典的键值或配置档案的编码）")
    private String isSubtotal;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "默认值")
    private BigDecimal defaultValue;

    @ApiModelProperty(value ="备注")
    private String remark;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
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
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品成本核算-工价成本其它项明细表")
    private CosProductCostLaborOther  cosProductCostLaborOther;

    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value ="工序名称")
    private String processName;

    @TableField(exist = false)
    @ApiModelProperty(value ="工序编码")
    private String processCode;

    @ApiModelProperty(value = "新建-工价成本模板对象-其他")
    @TableField(exist = false)
    private List<CosProductCostLaborOther> laborItemOtherList;

    @ApiModelProperty(value = "生产方式（数据字典的键值或配置档案的编码），自产、外发")
    @TableField(exist = false)
    private String productionMode;
}

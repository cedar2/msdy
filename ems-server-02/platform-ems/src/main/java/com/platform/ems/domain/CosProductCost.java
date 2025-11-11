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
 * 商品成本核算主对象 s_cos_product_cost
 *
 * @author qhq
 * @date 2021-04-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_product_cost")
public class CosProductCost extends EmsBaseEntity {
    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-物料成本核算 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料成本核算")
    private Long productCostSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] productCostSidList;

    /** 物料编码(成品/半成品) */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料编码(成品/半成品)")
    private Long materialSid;

    /** 物料SKU1档案(成品/半成品) */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料SKU1档案(成品/半成品)")
    private Long sku1Sid;

    /** 物料SKU2档案(成品/半成品) */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料SKU2档案(成品/半成品)")
    private Long sku2Sid;

    @TableField(exist = false)
    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    /** 商品名称 */
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /** 价格维度（数据字典的键值或配置档案的编码） */
    @NotEmpty(message="价格维度不能为空")
    @Excel(name = "价格维度", dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度（数据字典的键值或配置档案的编码）")
    private String priceDimension;

    /** 供料方式（数据字典的键值） */
    @Excel(name = "甲供料方式", dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "供料方式（数据字典的键值）")
    private String rawMaterialMode;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "颜色")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @TableField(exist = false)
    @Excel(name = "商品类型")
    @ApiModelProperty(value = "商品类型")
    private String materialTypeName;

    @TableField(exist = false)
    @Excel(name = "下单季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @TableField(exist = false)
    @Excel(name = "商品分类")
    @ApiModelProperty(value = "商品分类名称")
    private String materialClassName;

    @TableField(exist = false)
    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @TableField(exist = false)
    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;

    @TableField(exist = false)
    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装")
    private String upDownSuit;

    @TableField(exist = false)
    @Excel(name = "供方样衣号")
    @ApiModelProperty(value = "供方样衣号")
    private String sampleCodeVendor;

    @TableField(exist = false)
    @Excel(name = "快速编码")
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @TableField(exist = false)
    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌")
    private String companyBrandName;

    @TableField(exist = false)
    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    /** 当前审批节点名称 */
    @TableField(exist = false)
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    /** 当前审批人 */
    @TableField(exist = false)
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    /** 处理状态（数据字典的键值） */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /** 更新时间 */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 业务类型 */
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /** 供应商编码 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码")
    private Long vendorSid;

    /** 客户编码 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户编码")
    private Long customerSid;

    /** 价格录入方式(特殊工艺)（数据字典的键值） */
    @ApiModelProperty(value = "价格录入方式(特殊工艺)（数据字典的键值）")
    private String specialCraftPriceEnterMode;

    /** 业务模式：采购模式/销售模式（数据字典的键值或配置档案的编码） */
    @ApiModelProperty(value = "业务模式：采购模式/销售模式（数据字典的键值或配置档案的编码）")
    private String businessMode;

    /** 价格录入方式(生产费)（数据字典的键值） */
    @ApiModelProperty(value = "价格录入方式(生产费)（数据字典的键值）")
    private String productPriceEnterMode;

    /** 价格录入方式(其它费)（数据字典的键值） */
    @ApiModelProperty(value = "价格录入方式(其它费)（数据字典的键值）")
    private String otherPriceEnterMode;

    /** 产品季sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /** 价格录入方式(协议价)（数据字典的键值） */
    @ApiModelProperty(value = "价格录入方式(协议价)（数据字典的键值）")
    private String contractPriceEnterMode;

    /** 价格录入方式(协议价)（数据字典的键值） */
    @ApiModelProperty(value = "价格录入方式(协议价)（数据字典的键值）")
    private String priceEnterMode;

    /** 成本价总计(含税)-标准/采卖模式 */
    @ApiModelProperty(value = "成本价总计(含税)")
    private BigDecimal innerPriceTax;

    /** 成本价总计(不含税)-标准/采卖模式 */
    @ApiModelProperty(value = "成本价总计(不含税)")
    private BigDecimal innerPrice;

    /** 报价总计(含税)-标准/采卖模式 */
    @ApiModelProperty(value = "报价总计(含税)")
    private BigDecimal quotePriceTax;

    @ApiModelProperty(value = "报价总计(不含税)")
    private BigDecimal quotePrice;

    /** 核定价总计(含税)-标准/采卖模式 */
    @ApiModelProperty(value = "核定价总计(含税)")
    private BigDecimal checkPriceTax;

    /** 核定价总计(不含税)-标准/采卖模式 */
    @ApiModelProperty(value = "核定价总计(不含税)")
    private BigDecimal checkPrice;

    @ApiModelProperty(value = "确认价总计(含税)")
    private BigDecimal confirmPriceTax;

    @ApiModelProperty(value = "确认价总计(不含税)")
    private BigDecimal confirmPrice;

    @ApiModelProperty(value = "协议价(含税)")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "协议价(不含税)")
    private BigDecimal price;

    @ApiModelProperty(value = "产前核算标准成本(含税)")
    private BigDecimal standardCostTax;

    @ApiModelProperty(value = "产前核算标准成本(不含税)")
    private BigDecimal standardCost;

    /** 协议价计量单位（数据字典的键值） */
    @ApiModelProperty(value = "协议价计量单位（数据字典的键值）")
    private String unitPrice;

    /** 货币（数据字典的键值） */
    @ApiModelProperty(value = "货币（数据字典的键值）")
    private String currency;

    /** 货币单位（数据字典的键值） */
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    /** 税率（存值，即：不含百分号，如20%，就存0.2） */
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /** 单位换算比例（协议价单位/基本单位） */
    @ApiModelProperty(value = "单位换算比例（协议价单位/基本单位）")
    private BigDecimal unitConversionRate;

    /** 成本核算版本号 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "成本核算版本号")
    private Long costVersionId;

    /** 成本核算版本号（上一版本号） */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "成本核算版本号（上一版本号）")
    private Long costVersionIdPre;

    /** 系统ID-物料清单档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料清单档案")
    private Long bomSid;

    /** 说明（报价） */
    @ApiModelProperty(value = "说明（报价）")
    private String remarkInner;

    /** 说明（报价） */
    @ApiModelProperty(value = "说明（报价）")
    private String remarkQuote;

    /** 说明（核定价） */
    @ApiModelProperty(value = "说明（核定价）")
    private String remarkCheck;

    /** 说明（确认价） */
    @ApiModelProperty(value = "说明（确认价）")
    private String remarkConfirm;

    /** 启用/停用状态（数据字典的键值） */
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /** 说明（协议价） */
    @ApiModelProperty(value = "说明（协议价）")
    private String remarkAgree;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 更新人账号 */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /** 确认人账号 */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "商品档案明细-商品code查询")
    @TableField(exist = false)
    private BasMaterial material;

    @ApiModelProperty(value = "商品BOM明细-商品code查询")
    @TableField(exist = false)
    private List<TecBomItem> bomList;

    @ApiModelProperty(value = "子表-bom物料对象")
    @TableField(exist = false)
    private List<CosProductCostMaterial> costMaterialList;

    @ApiModelProperty(value = "子表-工价成本明细对象")
    @TableField(exist = false)
    private List<CosProductCostLabor> costLaborList;

    @ApiModelProperty(value = "新建-工价成本模板对象")
    @TableField(exist = false)
    private List<CosCostLaborTemplateItem> laborItemList;

    @ApiModelProperty(value = "新建-工价成本模板对象-其他")
    @TableField(exist = false)
    private List<CosProductCostLaborOther> laborItemOtherList;

    @ApiModelProperty(value = "子表-附件明细对象")
    @TableField(exist = false)
    private List<CosProductCostAttachment> costAttachmentList;
    @TableField(exist = false)
    private CosCostLaborTemplate  cosCostLaborTemplate;

    @ApiModelProperty(value = "流程ID")
    private String instanceId;

    /** 流程状态 0：普通记录 1：待审批记录 2：审批结束记录*/
    @ApiModelProperty(value = "流程状态")
    private String processType;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String node;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批人")
    private String approval;

    @TableField(exist = false)
    @ApiModelProperty(value = "上下套装")
    private String[] upDownSuitList;

    @TableField(exist = false)
    @ApiModelProperty(value = "上下套装")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "设计师")
    private String designerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "产品季sid")
    @TableField(exist = false)
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "客方商品编码")
    private String customerProductCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "风格")
    private String style;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型")
    private String modelName;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产工艺")
    private String productTechniqueTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品类别")
    private String materialCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "客方样衣号")
    private String sampleCodeCustomer;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "公司")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] companySidList;

    /** 提交人 */
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatusList;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    @ApiModelProperty(value = "校验-是否跳过插入 Y 是")
    @TableField(exist = false)
    private String skipInsert;

    @ApiModelProperty(value = "图片路径")
    @TableField(exist = false)
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "bom主表颜色")
    private String bomHeadSkuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：创建人")
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：设计师")
    private String[] designerAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "bom主表颜色")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomHeadSkuSid;

}

package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
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
 * 商品道序-明细对象 s_pay_product_process_step_item
 *
 * @author c
 * @date 2021-09-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_product_process_step_item")
public class PayProductProcessStepItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品道序明细信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序明细信息")
    private Long stepItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序明细信息(商品道序完成量台账需要用，省的转)")
    private Long processStepItemSid;

    @TableField(exist = false)
    private String delFlagBiangz;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] stepItemSidList;

    /**
     * 系统SID-商品道序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序")
    private Long productProcessStepSid;

    /**
     * 类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "类别", dictType = "s_process_step_category")
    @ApiModelProperty(value = "类别（数据字典的键值或配置档案的编码）")
    private String stepCategory;

    /**
     * 道序编码
     */
    @Excel(name = "道序编码")
    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    /**
     * 道序名称
     */
    @Excel(name = "道序名称")
    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    /**
     * 道序sid
     */
    @Excel(name = "道序sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "道序sid")
    private Long processStepSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序sids")
    private List<Long> processStepSids;

    /**
     * 工价(元)
     */
    @NotNull(message = "明细行工价(元)不能为空")
    @Digits(integer = 5, fraction = 4, message = "工价(元)整数位上限为5位，小数位上限为4位")
    @Excel(name = "工价(元)")
    @ApiModelProperty(value = "工价(元)")
    private BigDecimal price;

    @TableField(exist = false)
    @ApiModelProperty(value = "工价(元)变更前")
    private BigDecimal priceBgq;

    /**
     * 倍率(道序)
     */
    @NotNull(message = "明细行倍率(道序)不能为空")
    @Digits(integer = 2, fraction = 3, message = "倍率(道序)整数位上限为2位，小数位上限为3位")
    @Excel(name = "倍率(道序)")
    @ApiModelProperty(value = "倍率(道序)")
    private BigDecimal priceRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "倍率(道序)变更前")
    private BigDecimal priceRateBgq;

    /**
     * 工价标准(元)
     */
    @Excel(name = "工价标准(元)")
    @ApiModelProperty(value = "工价标准(元)")
    private BigDecimal standardPrice;

    /**
     * 所属生产工序sid
     */
    @Excel(name = "所属生产工序sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属生产工序sid")
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产工序sid")
    private Long[] processSidList;

    /**
     * 工序的最后一道道序（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "工序的最后一道道序（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "工序的最后一道道序（数据字典的键值或配置档案的编码）")
    private String isFinal;

    /**
     * 作业单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "作业计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "作业计量单位（数据字典的键值或配置档案的编码）")
    private String taskUnit;

    @TableField(exist = false)
    @ApiModelProperty(value = "作业计量单位名称")
    private String taskUnitName;

    /**
     * 序号
     */
    @NotNull(message = "序号不能为空")
    @Digits(integer = 4, fraction = 2, message = "序号整数位上限为4位")
    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    @TableField(exist = false)
    @ApiModelProperty(value = "导入所在的行号")
    private int exportNum;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String[] creatorAccountList;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 工序编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产工序编码")
    private String processCode;

    /**
     * 工序名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产工序名称")
    private String processName;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单sid")
    private String manufactureOrderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid(整单)")
    private String orderPlantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组sid")
    private String workCenterSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序工厂sid")
    private String processPlantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产订单工序sid")
    private String manufactureOrderProcessSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @TableField(exist = false)
    private List<Long> materialSids;

    @TableField(exist = false)
    private List<Long> processSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "工价*倍率")
    private BigDecimal actualPrice;

    @TableField(exist = false)
    private String materialCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品档案")
    private Long materialSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品档案")
    private Long productSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String[] departmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String[] productPriceTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String[] jixinWangongTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @TableField(exist = false)
    @ApiModelProperty(value = "当天计薪量")
    private BigDecimal completeQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "当天计薪量（系统自动生成）")
    private BigDecimal completeQuantitySys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完成量校验参考工序sid")
    private Long quantityReferProcessSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成量校验参考工序编码")
    private String quantityReferProcessCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成量校验参考工序名称")
    private String quantityReferProcessName;

    @TableField(exist = false)
    @ApiModelProperty(value = "参考工序所引用数量类型（数据字典的键值：s_quantity_type_refer_process）")
    private String quantityTypeReferProcess;

    @TableField(exist = false)
    @ApiModelProperty(value = "参考工序校验量")
    private String quantityReferProcess;

    @TableField(exist = false)
    @ApiModelProperty(value = "已计薪量")
    private BigDecimal cumulativeQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价上限(元)")
    private BigDecimal limitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "工价倍率(商品)")
    private BigDecimal productPriceRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(元)")
    private BigDecimal priceTotal;

}

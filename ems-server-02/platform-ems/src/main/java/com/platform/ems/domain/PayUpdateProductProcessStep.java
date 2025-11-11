package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.ems.util.data.KeepFourDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 商品道序变更-主对象 s_pay_update_product_process_step
 *
 * @author chenkw
 * @date 2022-11-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_update_product_process_step")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayUpdateProductProcessStep extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品道序变更
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序变更")
    private Long updateProductProcessStepSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] updateProductProcessStepSidList;

    /**
     * 系统SID-商品道序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序")
    private Long productProcessStepSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-商品道序")
    private Long[] productProcessStepSidList;

    /**
     * 系统SID-工厂
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    /**
     * 系统SID-工厂 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 工厂名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 工厂简称
     */
    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 商品工价类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    /**
     * 商品工价类型 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String[] productPriceTypeList;

    /**
     * 计薪完工类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    /**
     * 计薪完工类型 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String[] jixinWangongTypeList;

    /**
     * 操作部门编码
     */
    @ApiModelProperty(value = "操作部门编码")
    private String department;

    /**
     * 操作部门编码 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门编码")
    private String[] departmentList;

    /**
     * 操作部门名称
     */
    @TableField(exist = false)
    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    /**
     * 系统SID-商品档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品档案")
    private Long productSid;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String productCode;


    /**
     * 变更版本号
     */
    @Excel(name = "变更版本号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "变更版本号")
    private Long updateVersionId;

    /**
     * 变更说明
     */
    @Excel(name = "变更说明")
    @ApiModelProperty(value = "变更说明")
    private String updateRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "对应商品编码在商品档案中的主图")
    private String picture;

    @TableField(exist = false)
    @ApiModelProperty(value = "副图片路径（多图）")
    private String picturePathSecond;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径（副图）")
    private String[] picturePathList;

    /**
     * 商品名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String productName;

    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 我司样衣号
     */
    @Excel(name = "我司样衣号")
    @TableField(exist = false)
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    /**
     * 商品工价上限(元)
     */
    @Excel(name = "商品工价上限(元)(倍率后)(变更后)")
    @ApiModelProperty(value = "商品工价上限(元)")
    private BigDecimal limitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价上限(元) (保留有效小数位)")
    private String limitPriceToString;

    /**
     * 商品工价上限(元)
     */
    @ApiModelProperty(value = "商品工价上限(元)变更前")
    private BigDecimal limitPriceBgq;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价上限(元) 变更前(保留有效小数位)")
    private String limitPriceBgqToString;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率后)")
    private BigDecimal totalPriceAfter;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率后) (保留有效小数位)")
    private String totalPriceAfterToString;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率后)变更前")
    private BigDecimal totalPriceAfterBgq;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率后)变更前(保留有效小数位)")
    private String totalPriceAfterBgqToString;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率前)")
    private BigDecimal totalPriceBefore;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率前)(保留有效小数位)")
    private String totalPriceBeforeToString;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率前)")
    private BigDecimal totalPriceBeforeBgq;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率前)(保留有效小数位)")
    private String totalPriceBeforeBgqToString;

    /**
     * 工价倍率(商品)
     */
    @ApiModelProperty(value = "工价倍率(商品)")
    private BigDecimal productPriceRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "工价倍率(商品)")
    private BigDecimal productPriceRateBgq;

    /**
     * 道序工价小计(倍率后)
     */
    @Excel(name = "道序工价小计(倍率后)(变更后)")
    @ApiModelProperty(value = "道序工价小计(倍率后)")
    private BigDecimal totalPriceBlh;

    @ApiModelProperty(value = "道序工价小计(倍率后)变更前")
    private BigDecimal totalPriceBlhBgq;

    /**
     * 道序工价小计(倍率前)
     */
    @Excel(name = "道序工价小计(倍率前)(变更后)")
    @ApiModelProperty(value = "道序工价小计(倍率前)")
    private BigDecimal totalPriceBlq;

    @ApiModelProperty(value = "道序工价小计(倍率前)变更前")
    private BigDecimal totalPriceBlqBgq;

    /**
     * 货币（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "货币（数据字典的键值或配置档案的编码）")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;

    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;


    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改日期")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 商品道序变更-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序变更-明细对象")
    private List<PayUpdateProductProcessStepItem> updateItemList;

    /**
     * 商品道序变更-明细对象 被删除列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序变更-明细对象 被删除列表")
    private List<PayUpdateProductProcessStepItem> deleteItemList;

    /**
     * 商品道序变更-明细对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序变更-明细对象")
    private List<PayUpdateProductProcessStepItem> payProductProcessStepItemList;

    /**
     * 商品道序变更-附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序变更-附件对象")
    private List<PayUpdateProductProcessStepAttach> attachmentList;

}

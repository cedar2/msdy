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
import com.platform.ems.util.data.KeepFourDecimalsSerialize;
import com.platform.ems.util.data.KeepThreeDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品道序-主对象 s_pay_product_process_step
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_product_process_step")
public class PayProductProcessStep extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品道序
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序")
    private Long productProcessStepSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] productProcessStepSidList;

    /**
     * 系统SID-商品档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品档案")
    private Long productSid;

    /**
     * 系统SID-工厂
     */
    @NotNull(message = "工厂不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂(多选)")
    private Long[] plantSidList;

    /**
     * 工厂名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 工厂简称
     */
    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String shortName;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 商品工价类型（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "商品工价类型不能为空")
    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @NotEmpty(message = "计薪完工类型不能为空")
    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String[] jixinWangongTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型（多选）")
    private String[] productPriceTypeList;

    @NotBlank(message = "操作部门不能为空")
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @Excel(name = "操作部门")
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "对应商品编码在商品档案中的主图")
    private String picture;

    @TableField(exist = false)
    @ApiModelProperty(value = "副图片路径（多图）")
    private String picturePathSecond;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径（副图）")
    private String[] picturePathList;

    @Excel(name = "我司样衣号")
    @TableField(exist = false)
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（多选）")
    private String[] departmentList;

    /**
     * 快速编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    /**
     * 商品工价上限(元)
     */
    @NotNull(message = "商品工价上限(元)不能为空")
    @Digits(integer = 6, fraction = 4, message = "商品工价上限(元)整数位上限为6位，小数位上限为4位")
    @ApiModelProperty(value = "商品工价上限(元)")
    private BigDecimal limitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价上限(元)变更前")
    private BigDecimal limitPriceBgq;

    @TableField(exist = false)
    @Excel(name = "商品工价上限(倍率后)")
    @ApiModelProperty(value = "商品工价上限(元) (保留有效小数位)")
    private String limitPriceToString;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率后)")
    private BigDecimal totalPriceAfter;

    @Excel(name = "道序工价小计(倍率后)")
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率后) (保留有效小数位)")
    private String totalPriceAfterToString;

    @JsonSerialize(using = KeepFourDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率前)")
    private BigDecimal totalPriceBefore;

    @Excel(name = "道序工价小计(倍率前)")
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计(倍率前)(保留有效小数位)")
    private String totalPriceBeforeToString;

    /**
     * 工价倍率(商品)
     */
    @Digits(integer = 2, fraction = 3, message = "工价倍率(商品)整数位上限为2位，小数位上限为3位")
    @ApiModelProperty(value = "工价倍率(商品)")
    private BigDecimal productPriceRate;

    @Digits(integer = 5, fraction = 4, message = "道序工价小计整数位上限为5位，小数位上限为4位")
    @ApiModelProperty(value = "道序工价小计（倍率后）")
    private BigDecimal totalPriceBlh;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计（倍率后）变更前")
    private BigDecimal totalPriceBlhBgq;

    @Digits(integer = 5, fraction = 4, message = "道序工价小计整数位上限为5位，小数位上限为4位")
    @ApiModelProperty(value = "道序工价小计（倍率前）")
    private BigDecimal totalPriceBlq;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计（倍率前）变更前")
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

    @ApiModelProperty(value = "是否变更中（数据字典的键值或配置档案的编码）")
    private String isUpdate;

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
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

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
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;


    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @ApiModelProperty(value = "工价校验标识")
    @TableField(exist = false)
    private String verify;

    @ApiModelProperty(value = "工价校验提示信息")
    @TableField(exist = false)
    private String verifyHint;

    @TableField(exist = false)
    @ApiModelProperty(value = "工价小计(倍率后)")
    private BigDecimal totalPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "校验提示")
    private String msg;

    /**
     * 商品道序-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序-明细对象")
    private List<PayProductProcessStepItem> payProductProcessStepItemList;

    /**
     * 商品道序-附件对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序-附件对象")
    private List<PayProductProcessStepAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产工序sid")
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序变更的校验verifyPrice接口传BG")
    private String isUpdatePps;

}

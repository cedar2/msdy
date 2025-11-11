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
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 商品计件结算信息对象 s_pay_product_jijian_settle_infor
 *
 * @author chenkw
 * @date 2022-07-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pay_product_jijian_settle_infor")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayProductJijianSettleInfor extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-商品计件结算信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品计件结算信息")
    private Long jijianSettleInforSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] jijianSettleInforSidList;

    /**
     * 所属年月（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月（数据字典的键值或配置档案的编码）")
    private String yearmonth;

    /**
     * 工厂sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    /**
     * 工厂编码code
     */
    @ApiModelProperty(value = "工厂编码code ")
    private String plantCode;

	@TableField(exist = false)
	@ApiModelProperty(value = "工厂sid")
	private Long[] plantSidList;

	@TableField(exist = false)
	@ApiModelProperty(value = "工厂名称 ")
	private String plantName;

	@TableField(exist = false)
	@Excel(name = "工厂")
	@ApiModelProperty(value = "工厂简称 ")
	private String plantShortName;

    /**
     * 班组sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组sid")
    private Long workCenterSid;

    /**
     * 班组编码code
     */
    @ApiModelProperty(value = "班组编码code")
    private String workCenterCode;

	@TableField(exist = false)
	@ApiModelProperty(value = "班组sid")
	private Long[] workCenterSidList;

	@Excel(name = "班组")
	@TableField(exist = false)
	@ApiModelProperty(value = "班组名称 ")
	private String workCenterName;

    /**
     * 操作部门（数据字典的键值或配置档案的编码）
     */

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @TableField(exist = false)
    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String[] departmentList;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatchToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号是否精确查询")
    private String isPaichanPre;

    /**
     * 结算数量
     */
    @ApiModelProperty(value = "结算数量")
    private BigDecimal settleQuantity;

    @Excel(name = "结算数")
    @TableField(exist = false)
    @ApiModelProperty(value = "结算数量")
    private String settleQuantityToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算数累计(已确认)")
    private BigDecimal settleQuantityCheck;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算数累计(已确认)")
    private String settleQuantityCheckToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算数累计(含保存)")
    private BigDecimal settleQuantityTotal;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算数累计(含保存)")
    private String settleQuantityTotalToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算数累计(汉本月)")
    private BigDecimal settleQuantityMonth;

    @TableField(exist = false)
    @ApiModelProperty(value = "结算数累计(汉本月)")
    private String settleQuantityMonthToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal shicaiQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否维护实裁量")
    private String isHaveShicaiQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private String shicaiQuantityToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "未结算数累计(已确认)")
    private BigDecimal weiSettleQuantityCheck;

    @TableField(exist = false)
    @ApiModelProperty(value = "未结算数累计(已确认)")
    private String weiSettleQuantityCheckToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "未结算数累计(含保存)")
    private BigDecimal weiSettleQuantityTotal;

    @TableField(exist = false)
    @ApiModelProperty(value = "未结算数累计(含保存)")
    private String weiSettleQuantityTotalToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计（倍率前）")
    private BigDecimal totalPriceBlq;

    @Excel(name = "商品工价小计（倍率前）")
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计（倍率前）")
    private String totalPriceBlqToString;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码code ")
    private String customerCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户sid")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称 ")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称 ")
    private String customerShortName;

    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 商品工价类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String[] productPriceTypeList;

    /**
     * 计薪完工类型（数据字典的键值或配置档案的编码）
     */
	@Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @TableField(exist = false)
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String[] jixinWangongTypeList;

    /**
     * 商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品sid")
    private Long[] productSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计（倍率后）")
    private BigDecimal totalPriceBlh;

    @Excel(name = "商品工价小计（倍率后）")
    @TableField(exist = false)
    @ApiModelProperty(value = "道序工价小计（倍率后）")
    private String totalPriceBlhToString;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @Excel(name = "计薪申报状态", dictType = "s_jixin_report_status")
    @ApiModelProperty(value = "计薪申报状态（数据字典的键值或配置档案的编码）")
    private String jixinStatus;

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
    @JsonFormat(timezone = "GMT+8" , pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建日期" , width = 30, dateFormat = "yyyy-MM-dd")
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
	@ApiModelProperty(value = "更改人昵称")
	@TableField(exist = false)
	private String updaterAccountName;

	/**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8" , pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期" , width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
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
    @JsonFormat(timezone = "GMT+8" , pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "确认日期" , width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}

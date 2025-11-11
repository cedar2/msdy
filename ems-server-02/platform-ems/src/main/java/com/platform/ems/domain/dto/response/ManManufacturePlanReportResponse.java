package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产计划报表/生产进度报表 响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufacturePlanReportResponse implements Serializable {

    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long serialNum;

    @Excel(name = "工序生产方式（数据字典的键值或配置档案的编码），自产、外发")
    @ApiModelProperty(value = "工序生产方式（数据字典的键值或配置档案的编码），自产、外发")
    private String productionMode;

    @Excel(name = "生产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产批次号")
    private Long productionBatchNum;

    @Excel(name = "是否标志阶段完成的工序（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否标志阶段完成的工序（数据字典的键值或配置档案的编码）")
    private String isStageComplete;

    @Excel(name = "是否标志成品完工的工序（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否标志成品完工的工序（数据字典的键值或配置档案的编码）")
    private String isProduceComplete;

    @Excel(name = "是否开始")
    @ApiModelProperty(value = "是否开始")
    private String startFlag;

    @Excel(name = "是否完成")
    @ApiModelProperty(value = "是否完成")
    private String endFlag;

    @Excel(name = "完成状态（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "完成状态（数据字典的键值或配置档案的编码）")
    private String endStatus;

    @Excel(name = "当前完成量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前完成量")
    private Long currentCompleteQuantity;

    @Digits(integer = 8, fraction = 3, message = "计划产量整数位上限为8位，小数位上限为3位")
    @Excel(name = "产量（此工厂/此工作中心/此道工序负责生产的产量）")
    @ApiModelProperty(value = "产量（此工厂/此工作中心/此道工序负责生产的产量）")
    private BigDecimal quantity;

    @Excel(name = "进度说明")
    @ApiModelProperty(value = "进度说明")
    private String comment;

    @Excel(name = "是否取消")
    @ApiModelProperty(value = "是否取消")
    private String cancelFlag;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际开始日期")
    private Date actualStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @Excel(name = "负责人")
    @ApiModelProperty(value = "负责人")
    private String director;

    @Excel(name = "工序行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工序行号")
    private Long itemNum;

    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "工序编码")
    private String processCode;

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "工厂编码(整单)")
    private String plantCode;

    @Excel(name = "工厂名称(整单)")
    @ApiModelProperty(value = "工厂名称(整单)")
    private String plantName;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @Excel(name = "物料（商品/服务）名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @ApiModelProperty(value = "工厂编码(工序)")
    private String processPlantCode;

    @TableField(exist = false)
    @Excel(name = "工厂名称(工序)")
    @ApiModelProperty(value = "工厂名称(工序)")
    private String processPlantName;

    @ApiModelProperty(value = "工作中心编码")
    private String workCenterCode;

    @Excel(name = "工作中心名称")
    @ApiModelProperty(value = "工作中心名称")
    private String workCenterName;

    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @Excel(name = "计量单位名称")
    @ApiModelProperty(value = "计量单位名称")
    private String unitBaseName;

    @Excel(name = "销售订单工序名称")
    @ApiModelProperty(value = "销售订单工序名称")
    private String orderProcessName;

    @Excel(name = "工序名称")
    @ApiModelProperty(value = "计量单位名称")
    private String orderProcessCode;


}

package com.platform.ems.device.log.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 生产排程信息接口日志对象 s_man_scheduling_info_log
 *
 * @author chenkw
 * @date 2023-05-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_scheduling_info_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManSchedulingInfoLog extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产排程信息日志
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产排程信息日志")
    private Long schedulingInfoLogSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] schedulingInfoLogSidList;

    /**
     * 系统SID-生产排程信息
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产排程信息")
    private Long schedulingInfoSid;

    /**
     * 生产排程信息编号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产排程信息编号")
    private Long schedulingInfoCode;

    /**
     * 系统SID-商品档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品档案")
    private Long productSid;

    /**
     * 商品编码(款号)
     */
    @ApiModelProperty(value = "商品编码(款号)")
    private String productCode;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String productName;

    /**
     * 商品维度(数据字典键值)
     */
    @ApiModelProperty(value = "商品维度(数据字典键值)")
    private String productDimension;

    /**
     * 系统SID-SKU1
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU1")
    private Long sku1Sid;

    /**
     * SKU1编码
     */
    @ApiModelProperty(value = "SKU1编码")
    private String sku1Code;

    /**
     * SKU1名称
     */
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    /**
     * SKU1类型
     */
    @ApiModelProperty(value = "SKU1类型")
    private String sku1Type;

    /**
     * 系统SID-SKU2
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU2")
    private Long sku2Sid;

    /**
     * SKU2编码
     */
    @ApiModelProperty(value = "SKU2编码")
    private String sku2Code;

    /**
     * SKU2名称
     */
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    /**
     * SKU2类型
     */
    @ApiModelProperty(value = "SKU2类型")
    private String sku2Type;

    /**
     * 系统SID-道序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-道序")
    private Long processStepSid;

    /**
     * 道序编码
     */
    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    /**
     * 道序名称
     */
    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 公司编码
     */
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 系统SID-工厂档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂档案")
    private Long plantSid;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 工厂名称
     */
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 工厂简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 操作部门编码(配置档案)
     */
    @ApiModelProperty(value = "操作部门编码(配置档案)")
    private String departmentCode;

    /**
     * 系统SID-班组
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-班组")
    private Long workCenterSid;

    /**
     * 班组编码
     */
    @ApiModelProperty(value = "班组编码")
    private String workCenterCode;

    /**
     * 班组名称
     */
    @ApiModelProperty(value = "班组名称")
    private String workCenterName;

    /**
     * 系统SID-工位
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工位")
    private Long workstationSid;

    /**
     * 工位编码
     */
    @ApiModelProperty(value = "工位编码")
    private String workstationCode;

    /**
     * 工位名称
     */
    @ApiModelProperty(value = "工位名称")
    private String workstationName;

    /**
     * 系统SID-工序(所属生产工序)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工序(所属生产工序)")
    private Long processSid;

    /**
     * 所属生产工序编码
     */
    @ApiModelProperty(value = "所属生产工序编码")
    private String processCode;

    /**
     * 所属生产工序名称
     */
    @ApiModelProperty(value = "所属生产工序名称")
    private String processName;

    /**
     * 系统SID-商品道序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序")
    private Long productProcessStepSid;

    /**
     * 系统SID-商品道序明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序明细")
    private Long productProcessStepItemSid;

    /**
     * 商品道序明细序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序明细序号")
    private Long sort;

    /**
     * 整单生产方式（数据字典的键值或配置档案的编码），自产、外发
     */
    @ApiModelProperty(value = "整单生产方式（数据字典的键值或配置档案的编码），自产、外发")
    private String productionMode;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Integer paichanBatch;

    /**
     * 分配量
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "分配量")
    private Integer fenpeiQuantity;

    /**
     * 完成量
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完成量")
    private Integer wanchengQuantity;

    /**
     * 数量类别(正常/返修)
     */
    @ApiModelProperty(value = "数量类别(正常/返修)")
    private String quantityCategory;

    /**
     * 商品工价类型(数据字典键值)
     */
    @ApiModelProperty(value = "商品工价类型(数据字典键值)")
    private String productPriceType;

    /**
     * 计薪完工类型(数据字典键值)
     */
    @ApiModelProperty(value = "计薪完工类型(数据字典键值)")
    private String jixinWangongType;

    /**
     * 完成状态(数据字典键值)
     */
    @ApiModelProperty(value = "完成状态(数据字典键值)")
    private String completeStatus;

    /**
     * 请求类别(首发,更新,删除)
     */
    @ApiModelProperty(value = "请求类别(首发,更新,删除)")
    private String requestCategory;

    /**
     * 推送人账号（用户账号）
     */
    @ApiModelProperty(value = "推送人账号（用户账号）")
    private String sendAccount;

    /**
     * 推送/请求时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "推送/请求时间")
    private Date sendDate;

    /**
     * 发起系统
     */
    @ApiModelProperty(value = "发起系统")
    private String dataSent;

    /**
     * 目标系统
     */
    @ApiModelProperty(value = "目标系统")
    private String dataTarget;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}

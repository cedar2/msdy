package com.platform.ems.domain;

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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 生产排程信息对象 s_man_scheduling_info
 *
 * @author chenkw
 * @date 2023-05-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_scheduling_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManSchedulingInfo extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产排程信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产排程信息")
    private Long schedulingInfoSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] schedulingInfoSidList;

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
    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String productCode;

    /**
     * 商品维度(数据字典键值)
     */
    @Excel(name = "商品维度")
    @ApiModelProperty(value = "商品维度(数据字典键值)")
    private String productDimension;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String productName;

    /**
     * 系统SID-SKU1
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-SKU1")
    private Long sku1Sid;

    /**
     * SKU1编码
     */
    @Excel(name = "SKU1编码")
    @ApiModelProperty(value = "SKU1编码")
    private String sku1Code;

    /**
     * SKU1名称
     */
    @Excel(name = "SKU1名称")
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    /**
     * SKU1类型
     */
    @Excel(name = "SKU1类型")
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
    @Excel(name = "SKU2编码")
    @ApiModelProperty(value = "SKU2编码")
    private String sku2Code;

    /**
     * SKU2名称
     */
    @Excel(name = "SKU2名称")
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    /**
     * SKU2类型
     */
    @Excel(name = "SKU2类型")
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
     * 系统SID-公司
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    /**
     * 公司编码
     */
    @Excel(name = "公司编码")
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    /**
     * 公司名称
     */
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司简称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    /**
     * 系统SID-工厂
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    /**
     * 工厂编码
     */
    @Excel(name = "工厂编码")
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 工厂名称
     */
    @Excel(name = "工厂名称")
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
    @Excel(name = "操作部门")
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
    @Excel(name = "班组编码")
    @ApiModelProperty(value = "班组编码")
    private String workCenterCode;

    /**
     * 班组名称
     */
    @Excel(name = "班组名称")
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
    @Excel(name = "工位编码")
    @ApiModelProperty(value = "工位编码")
    private String workstationCode;

    /**
     * 工位名称
     */
    @Excel(name = "工位名称")
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
    @Excel(name = "所属生产工序编码")
    @ApiModelProperty(value = "所属生产工序编码")
    private String processCode;

    /**
     * 所属生产工序名称
     */
    @Excel(name = "所属生产工序名称")
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
    @Excel(name = "商品道序明细序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序明细序号")
    private Long sort;

    /**
     * 整单生产方式（数据字典的键值或配置档案的编码），自产、外发
     */
    @Excel(name = "整单生产方式，自产、外发")
    @ApiModelProperty(value = "整单生产方式（数据字典的键值或配置档案的编码），自产、外发")
    private String productionMode;

    /**
     * 分配量
     */
    @Excel(name = "分配量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "分配量")
    private Integer fenpeiQuantity;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Integer paichanBatch;

    /**
     * 商品工价类型(数据字典键值)
     */
    @Excel(name = "商品工价类型")
    @ApiModelProperty(value = "商品工价类型(数据字典键值)")
    private String productPriceType;

    /**
     * 计薪完工类型(数据字典键值)
     */
    @Excel(name = "计薪完工类型")
    @ApiModelProperty(value = "计薪完工类型(数据字典键值)")
    private String jixinWangongType;

    /**
     * 生产计划开始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "生产计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "生产计划开始日期")
    private Date planStartDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产计划开始日期 起")
    private String planStartDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产计划开始日期 止")
    private String planStartDateEnd;

    /**
     * 生产计划完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "生产计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "生产计划完成日期")
    private Date planEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产计划完成日期 起")
    private String planEndDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产计划完成日期 止")
    private String planEndDateEnd;

    /**
     * 生产实际完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "生产实际完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "生产实际完成日期")
    private Date actulEndDate;

    /**
     * 完成状态(数据字典键值)
     */
    @Excel(name = "完成状态")
    @ApiModelProperty(value = "完成状态(数据字典键值)")
    private String completeStatus;

    /**
     * 处理状态（数据字典的键值）
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

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
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
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
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品道序明细")
    List<PayProductProcessStepItem> processStepItemList;

}

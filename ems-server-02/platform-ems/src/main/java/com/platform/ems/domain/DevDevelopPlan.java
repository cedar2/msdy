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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 开发计划对象 s_dev_develop_plan
 *
 * @author chenkw
 * @date 2022-12-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_dev_develop_plan")
public class DevDevelopPlan extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-开发计划
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-开发计划")
    private Long developPlanSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] developPlanSidList;

    /**
     * 年度（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @Excel(name = "年度")
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String year;

    /**
     * 年度（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String[] yearList;

    /**
     * 公司sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    /**
     * 公司sid (多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid")
    private Long[] companySidList;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司简称
     */
    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 品牌编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "品牌编码")
    private String brandCode;

    /**
     * 品牌编码 （多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "品牌编码 （多选）")
    private String[] brandCodeList;

    /**
     * 品牌名称
     */
    @TableField(exist = false)
    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    /**
     * 大类sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类sid")
    private Long bigClassSid;

    /**
     * 大类sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "大类sid 多选")
    private Long[] bigClassSidList;

    /**
     * 大类code
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "大类code")
    private String bigClassCode;

    /**
     * 大类
     */
    @TableField(exist = false)
    @Excel(name = "大类")
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    /**
     * 中类sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类sid")
    private Long middleClassSid;

    /**
     * 中类sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "中类sid 多选")
    private Long[] middleClassSidList;

    /**
     * 中类code
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "中类code")
    private String middleClassCode;

    /**
     * 中类
     */
    @TableField(exist = false)
    @Excel(name = "中类")
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    /**
     * 小类sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类sid")
    private Long smallClassSid;

    /**
     * 小类sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "小类sid 多选")
    private Long[] smallClassSidList;

    /**
     * 小类code
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "小类code")
    private String smallClassCode;

    /**
     * 小类
     */
    @Excel(name = "小类")
    @TableField(exist = false)
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    /**
     * 开发计划号
     */
    @NotBlank(message = "开发计划号不能为空")
    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    /**
     * 开发计划名称
     */
    @NotBlank(message = "开发计划名称不能为空")
    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    /**
     * 开发级别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "开发级别", dictType = "s_develop_level")
    @ApiModelProperty(value = "开发级别（数据字典的键值或配置档案的编码）")
    private String developLevel;

    /**
     * 开发级别
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发级别（数据字典的键值或配置档案的编码）")
    private String developLevelName;

    /**
     * 开发级别（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发级别（多选）")
    private String[] developLevelList;

    /**
     * 开发类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "开发类型", dictType = "s_develop_type")
    @ApiModelProperty(value = "开发类型（数据字典的键值或配置档案的编码）")
    private String developType;

    /**
     * 开发类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发类型")
    private String developTypeName;

    /**
     * 开发类型（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发类型（多选）")
    private String[] developTypeList;

    /**
     * 开发计划负责人SID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发计划负责人SID")
    private Long leaderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "开发计划负责人SID")
    private Long[] leaderSidList;

    @ApiModelProperty(value = "开发计划负责人CODE")
    private String leaderCode;

    @TableField(exist = false)
    @Excel(name = "开发计划负责人")
    @ApiModelProperty(value = "开发计划负责人")
    private String leaderName;

    /**
     * 产品季sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 产品季sid (多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid")
    private Long[] productSeasonSidList;

    /**
     * 产品季编码
     */
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 产品季名称
     */
    @Excel(name = "产品季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 目标成本（元）
     */
    @Excel(name = "目标成本(元)")
    @Digits(integer = 8, fraction = 5, message = "目标成本（元）整数位上限为13位，小数位上限为5位")
    @ApiModelProperty(value = "目标成本（元）")
    private BigDecimal costTarget;

    /**
     * 目标零售价（元）
     */
    @Excel(name = "目标零售价(元)")
    @Digits(integer = 8, fraction = 5, message = "目标零售价（元）整数位上限为13位，小数位上限为5位")
    @ApiModelProperty(value = "目标零售价（元）")
    private BigDecimal retailPriceTarget;

    /**
     * 所属年月
     */
    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    /**
     * 所属年月 （多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月 （多选）")
    private String[] yearmonthList;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 加价率，如是2%，则存储值为：0.02
     */
    @Digits(integer = 3, fraction = 3, message = "加价率整数位上限为3位，小数位上限为3位")
    @ApiModelProperty(value = "加价率，如是2%，则存储值为：0.02")
    private BigDecimal markUpRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "加价率%")
    private String markUpRateString;

    /**
     * 品类规划sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品类规划sid")
    private Long categoryPlanSid;

    /**
     * 品类规划编号
     */
    @Excel(name = "品类规划编号")
    @ApiModelProperty(value = "品类规划编号")
    private String categoryPlanCode;

    /**
     * 品类规划明细sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品类规划明细sid")
    private Long categoryPlanItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "品类规划明细sid")
    private Long[] categoryPlanItemSidList;

    /**
     * 品类规划行号
     */
    @ApiModelProperty(value = "品类规划行号")
    private Integer categoryPlanItemNum;

    /**
     * 商品款号（变体前）sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品款号（变体前）sid")
    private Long productSidInitial;

    /**
     * 商品款号（变体前）code
     */
    @Excel(name = "商品款号/SPU号(变体前)")
    @ApiModelProperty(value = "商品款号（变体前）code")
    private String productCodeInitial;

    /**
     * 样品号（变体前）
     */
    @ApiModelProperty(value = "样品号（变体前）")
    private String sampleCodeInitial;

    /**
     * 计划类型（数据字典的键值或配置档案的编码）
     */
    @TableField(exist = false)
    @Excel(name = "计划类型", dictType = "s_plan_type")
    @ApiModelProperty(value = "计划类型（数据字典的键值或配置档案的编码）")
    private String planType;

    /**
     * 计划类型（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划类型（多选）")
    private String[] planTypeList;

    /**
     * 作废说明
     */
    @ApiModelProperty(value = "作废说明")
    private String cancelRemark;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;


    /**
     * 处理状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
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
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
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
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件对象")
    private List<DevDevelopPlanAttach> attachmentList;

    /**
     * 项目信息
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目信息")
    private List<PrjProject> projectList;

    /**
     * 组别 品类规划
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String groupType;

    /**
     * 款式 品类规划
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "款式（数据字典的键值或配置档案的编码）")
    private String kuanType;

    /**
     * 系列 品类规划
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系列（数据字典的键值或配置档案的编码）")
    private String series;

    /**
     * 排序规则
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "排序规则")
    private String sortRule;

}

package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 品类规划明细报表查询返回
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevCategoryPlanItemFormResponse {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long categoryPlanItemSid;

    @ApiModelProperty(value = "品类规划行号")
    private Integer categoryPlanItemNum;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-品类规划")
    private Long categoryPlanSid;

    @Excel(name = "品类规划编号")
    @ApiModelProperty(value = "品类规划编号")
    private String categoryPlanCode;

    @Excel(name = "年度", dictType = "s_year")
    @ApiModelProperty(value = "年度")
    private String year;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司（单选）")
    private Long companySid;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司")
    private String companyShortName;

    @Excel(name = "组别(明细)", dictType = "s_product_group")
    @ApiModelProperty(value = "组别")
    private String groupType;

    @Excel(name = "大类")
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    @Excel(name = "中类")
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    @Excel(name = "小类")
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    @Excel(name = "品牌")
    @ApiModelProperty(value = "公司品牌")
    private String brandName;

    @Excel(name = "规划款数量", cellType = Excel.ColumnType.NUMERIC)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "规划款数量")
    private Long planQuantity;

    @Excel(name = "开发计划负责人")
    @ApiModelProperty(value = "下一步负责人名称")
    private String nextReceiverName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发计划SID")
    private Long developPlanSid;

    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    @Excel(name = "开发级别", dictType = "s_develop_level")
    @ApiModelProperty(value = "开发级别")
    private String developLevel;

    @Excel(name = "开发类型", dictType = "s_develop_type")
    @ApiModelProperty(value = "开发类型")
    private String developType;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "目标成本（元）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "目标成本（元）")
    private BigDecimal costTarget;

    @Excel(name = "目标零售价（元）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "目标零售价（元）")
    private BigDecimal retailPriceTarget;

    @ApiModelProperty(value = "加价率%")
    private String markUpRateString;

    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @Excel(name = "款式")
    @ApiModelProperty(value = "款式")
    private String kuanType;

    @Excel(name = "系列")
    @ApiModelProperty(value = "系列")
    private String series;

    @Excel(name = "计划类型", dictType = "s_plan_type")
    @ApiModelProperty(value = "计划类型")
    private String planType;

    @Excel(name = "规划说明")
    @ApiModelProperty(value = "规划说明")
    private String planRemark;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;
}

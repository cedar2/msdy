package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.ManProcessStepCompleteRecordItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 道序完成台账-按款显示的请求实体
 *
 * @author chenkw
 * @date 2022-10-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManProcessStepCompleteRecordTableRequest {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序完成量台账单")
    private Long stepCompleteRecordSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序完成量台账单号")
    private Long stepCompleteRecordCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @ApiModelProperty(value = "工厂名称")
    private String plantShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @ApiModelProperty(value = "完成日期")
    private String completeDate;

    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @ApiModelProperty(value = "录入方式(数据字典)")
    private String enterMode;

    @ApiModelProperty(value = "登记人账号sid")
    private Long reporter;

    @ApiModelProperty(value = "登记人名称")
    private String reporterName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "登记日期")
    private Date reportDate;

    @ApiModelProperty(value = "申报周期（数据字典的键值或配置档案的编码）")
    private String reportCycle;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @ApiModelProperty(value = "更新人名称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @ApiModelProperty(value = "确认人名称")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @ApiModelProperty(value = "商品道序完成量台账-明细对象")
    private List<ManProcessStepCompleteRecordItem> stepCompleteRecordItemList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所选明细行的商品sid")
    private Long productSid;

    @ApiModelProperty(value = "所选明细行的商品编码")
    private String productCode;

    @ApiModelProperty(value = "所选明细行的商品名称")
    private String productName;

    @ApiModelProperty(value = "所选明细行的排产批次号")
    private Integer paichanBatch;

    @ApiModelProperty(value = "排产批次号是否精确查询")
    private String isPaichanPre;

}

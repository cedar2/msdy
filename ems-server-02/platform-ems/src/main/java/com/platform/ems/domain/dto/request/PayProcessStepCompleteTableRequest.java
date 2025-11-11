package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 计薪量申报-按款显示的请求实体
 *
 * @author chenkw
 * @date 2022-07-28
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayProcessStepCompleteTableRequest {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-计薪量申报单")
    private Long stepCompleteSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计薪量申报单号")
    private Long stepCompleteCode;

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

    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @ApiModelProperty(value = "录入方式(数据字典)")
    private String enterMode;

    @ApiModelProperty(value = "申报人账号sid")
    private Long reporter;

    @ApiModelProperty(value = "申报人名称")
    private String reporterName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "申报日期(YYYYMMDD)")
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

    @ApiModelProperty(value = "计薪量申报-明细对象")
    private List<PayProcessStepCompleteItem> payProcessStepCompleteItemList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所选明细行的商品sid")
    private Long productSid;

    @ApiModelProperty(value = "所选明细行的商品编码")
    private String productCode;

    @ApiModelProperty(value = "所选明细行的商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所选明细行的排产批次号")
    private Long paichanBatch;

    @ApiModelProperty(value = "排产批次号是否精确查询")
    private String isPaichanPre;

}

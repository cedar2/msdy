package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.ems.domain.PayProcessStepComplete;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 计薪量申报-按款显示的返回实体
 *
 * @author chenkw
 * @date 2022-07-28
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PayProcessStepCompleteTableResponse {

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

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @ApiModelProperty(value = "结算数累计(含本月)")
    private BigDecimal settleQuantityMonth;

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

    /**
     * 前端要定义一个多余的字段去判断，low的一批
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "勾选")
    private String gouxuan;

    @ApiModelProperty(value = "form表单中的明细对象")
    private List<PayProcessStepCompleteItem> payProcessStepCompleteItemList;

    @ApiModelProperty(value = "商品道序明细(道序序号和名称)")
    private List<PayProcessStepCompleteTableStepResponse> stepItemList;

    @ApiModelProperty(value = "计薪量申报-按款显示的返回实体 - 每行的员工")
    private List<PayProcessStepCompleteTableStaffResponse> staffList;

}

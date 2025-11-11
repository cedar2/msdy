package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表中心生产管理生产月进度 ManDayProgressMonthForm
 *
 * @author chenkaiwen
 * @date 2022-08-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayProgressMonthForm {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "生产月进度报表——所属年月")
    private String yearmonth;

    @ApiModelProperty(value = "生产月进度报表——所属年月日")
    private String yearmonthday;

    @ApiModelProperty(value = "生产月进度报表——所属年月的上一个月")
    private String lastYearmonthday;

    @ApiModelProperty(value = "生产日进度报表——日期起")
    private String yearmonthdayBegin;

    @ApiModelProperty(value = "生产日进度报表——日期至")
    private String yearmonthdayEnd;

    @ApiModelProperty(value = "是否查询所选范围")
    private String isRange;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单明细")
    private Long dayManufactureProgressItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单")
    private Long dayManufactureProgressSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单-工序")
    private Long manufactureOrderProcessSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @ApiModelProperty(value = "系统SID-物料&商品sku1 多选")
    private Long[] sku1SidList;

    @ApiModelProperty(value = "SKU1类型（数据字典的键值或配置档案的编码）")
    private String sku1Type;

    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @ApiModelProperty(value = "分配量")
    private BigDecimal quantityFenpei;

    @ApiModelProperty(value = "是否标志阶段完成的工序")
    private String isStageComplete;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期/汇报日期")
    private Date documentDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @ApiModelProperty(value = "工作中心/班组sid（多选）")
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @ApiModelProperty(value = "工厂sid（多选）")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    @ApiModelProperty(value = "系统自增长ID-工序（多选）")
    private Long[] processSidList;

    @ApiModelProperty(value = "工序名称")
    private String processName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date processPlanEndDate;

    @ApiModelProperty(value = "完成量累计(区间前)")
    private BigDecimal totalCompleteQuantityBefore;

    @ApiModelProperty(value = "完成量累计(区间前+区间内)")
    private BigDecimal totalCompleteQuantityAdd;

    @ApiModelProperty(value = "完成量累计(区间内)")
    private BigDecimal totalCompleteQuantityIn;

    @ApiModelProperty(value = "生产订单工序计划完成量/分配量")
    private BigDecimal processQuantity;

    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @ApiModelProperty(value = "本月计划完成量")
    private BigDecimal monthPlanQuantity;

    @ApiModelProperty(value = "本月累计完成量")
    private BigDecimal monthTotalCompleteQuantity;

    @ApiModelProperty(value = "上月累计完成量")
    private BigDecimal lastMonthTotalCompleteQuantity;

    @ApiModelProperty(value = "已完成总量")
    private BigDecimal totalCompleteQuantity;

    @ApiModelProperty(value = "当前计划完成总量")
    private BigDecimal currentTotalPlanQuantity;

    @ApiModelProperty(value = "未完成量")
    private BigDecimal weiCompleteQuantity;

    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @ApiModelProperty(value = "计划产量(整单)")
    private BigDecimal orderQuantity;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "操作部门SID")
    private Long departmentSid;

    @ApiModelProperty(value = "操作部门SID（多选）")
    private Long[] departmentSidList;

    @ApiModelProperty(value = "操作部门编码")
    private String departmentCode;

    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "日期对应的量")
    private List<ManDayProgressMonthFormDay> dayList;

    @ApiModelProperty(value ="数据权限过滤参数")
    private Map<String, Object> params;

    @ApiModelProperty(value ="创建日期开始时间")
    private String beginTime;

    @ApiModelProperty(value ="创建日期结束时间")
    private String endTime;

    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    public Integer getPageBegin() {
        if (pageSize != null && pageNum != null){
            return pageSize*(pageNum-1);
        }else {
            return pageBegin;
        }
    }

    public void setPageBegin(Integer pageBegin) {
        if (pageSize != null && pageNum != null){
            this.pageBegin = this.pageSize*(this.pageNum-1);
        }else {
            this.pageBegin = pageBegin;
        }
    }

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

}

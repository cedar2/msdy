package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 计件工资明细报表
 * PaySalaryWageFormResponse
 *
 * @author chenkaiwen
 * @date 2022-06-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaySalaryWageFormResponse {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序明细信息")
    private Long stepCompleteItemSid;

    @ApiModelProperty(value = "系统SID-商品道序明细信息")
    private List<Long> stepCompleteItemSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品道序明细信息")
    private Long processStepItemSid;

    @Excel(name = "所属年月")
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂")
    private Long plantSid;

    @ApiModelProperty(value = "工厂")
    private Long[] plantSidList;

    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂")
    private String plantName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组")
    private Long workCenterSid;

    @ApiModelProperty(value = "班组")
    private Long[] workCenterSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "隶属班组")
    private Long staffWorkCenterSid;

    @ApiModelProperty(value = "隶属班组")
    private Long[] staffWorkCenterSidList;

    @Excel(name = "班组(隶属)")
    @ApiModelProperty(value = "隶属班组")
    private String staffWorkCenterName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工SID")
    private Long staffSid;

    @ApiModelProperty(value = "员工SID")
    private Long[] staffSidList;

    @Excel(name = "员工姓名")
    @ApiModelProperty(value = "员工名称")
    private String staffName;

    @ApiModelProperty(value = "员工和班组的sid拼接")
    private String staffAndWorkSid;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品SID")
    private Long productSid;

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "款号名")
    private String materialCodeName;

    @Excel(name = "排产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String department;

    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String[] departmentList;

    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    @Excel(name = "道序序号")
    @ApiModelProperty(value = "序号")
    private String sortToString;

    @Excel(name = "道序名称")
    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    @ApiModelProperty(value = "工序SID")
    private Long processSid;

    @ApiModelProperty(value = "工序编码")
    private String processCode;

    @ApiModelProperty(value = "工序名称")
    private String processName;

    @ApiModelProperty(value = "计薪量")
    private BigDecimal completeQuantity;

    @Excel(name = "计薪量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "计薪量")
    private String completeQuantityToString;

    @ApiModelProperty(value = "道序工价")
    private BigDecimal price;

    @Excel(name = "道序工价", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "道序工价")
    private String priceToString;

    @ApiModelProperty(value = "工价倍率")
    private BigDecimal priceRate;

    @Excel(name = "工价倍率", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "工价倍率")
    private String priceRateToString;

    @ApiModelProperty(value = "完工调价倍率(道序)")
    private BigDecimal wangongPriceRate;

    @Excel(name = "调价率", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "调价率")
    private String wangongPriceRateToString;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "金额(元)")
    private BigDecimal money;

    @Excel(name = "金额(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "金额(元)")
    private String moneyToString;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态(多选)")
    private String[] handleStatusList;

    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型")
    private String productPriceType;

    @ApiModelProperty(value = "商品工价类型")
    private String[] productPriceTypeList;

    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型")
    private String jixinWangongType;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "申报日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "申报日期")
    private Date reportDate;

    @ApiModelProperty(value = "申报日期(起)")
    private String reportDateBegin;

    @ApiModelProperty(value = "申报日期(止)")
    private String reportDateEnd;

    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计薪量申报单SID")
    private Long stepCompleteSid;

    @Excel(name = "计薪量申报单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计薪量申报单号")
    private Long stepCompleteCode;

    @Excel(name = "员工号")
    @ApiModelProperty(value = "员工编码")
    private String staffCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "道序编码")
    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    @Excel(name = "班组(结算)")
    @ApiModelProperty(value = "班组")
    private String workCenterName;

    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value ="汇总维度")
    private String dimension;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "列表排序方式")
    private String sortType;
}

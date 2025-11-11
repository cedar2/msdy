package com.platform.ems.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 道序完成台账明细对象
 *
 * @author 韦德权
 * @date 2022-10-31
 */
@Data
@Accessors(chain = true)
@ApiModel
public class StepFinishDetail extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "完成日期")
    private Date completeDate;

    /**
     * 工厂sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    /**
     * 工厂sid 多选
     */
    @ApiModelProperty(value = "工厂sid多选")
    private Long[] plantSidList;

    /**
     * 工厂简称
     */
    @Excel(name = "工厂(工序)")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 完成日期 起
     */
    @ApiModelProperty(value = "完成日期 起")
    private String completeDateBegin;

    /**
     * 完成日期 至
     */
    @ApiModelProperty(value = "完成日期 至")
    private String completeDateEnd;

    /**
     * 操作部门
     */
    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门")
    private String departmentName;

    @ApiModelProperty(value = "操作部门")
    private String department;

    @ApiModelProperty(value = "操作部门")
    private String[] departmentList;

    /**
     * 班组名称
     */
    @Excel(name = "班组(隶属)")
    @ApiModelProperty(value = "班组名称")
    private String staffWorkCenterName;

    /**
     * 班组（隶属）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组sid")
    private Long staffWorkCenterSid;

    /**
     * 班组（隶属）sid 多选
     */
    @ApiModelProperty(value = "班组sid多选")
    private Long[] staffWorkCenterSidList;

    /**
     * 员工姓名
     */
    @Excel(name = "员工姓名")
    @ApiModelProperty(value = "员工姓名")
    private String workerName;

    /**
     * 员工账号sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工账号sid")
    private Long workerSid;

    @ApiModelProperty(value = "员工账号sid")
    private Long[] workerSidList;

    /**
     * 商品编码
     */
    @Excel(name = "商品编号")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Integer paichanBatch;

    /**
     * 商品道序序号
     */
    @Excel(name = "道序序号")
    @ApiModelProperty(value = "序号(商品道序)")
    private BigDecimal sort;

    /**
     * 完成量(当天)
     */
    @Excel(name = "完成量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "完成量(当天)")
    private BigDecimal completeQuantity;

    /**
     * 道序名称
     */
    @Excel(name = "道序名称")
    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String productName;

    /**
     * 处理状态（数据字典的键值）
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）s_handle_status")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（数据字典的键值）s_handle_status")
    private String[] handleStatusList;

    /**
     * 商品工价类型（数据字典的键值）
     */
    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值）s_product_price_type")
    private String productPriceType;

    @ApiModelProperty(value = "商品工价类型（数据字典的键值）s_product_price_type")
    private String[] productPriceTypeList;

    /**
     * 计薪完工类型（数据字典的键值）
     */
    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值）s_jixin_wangong_type")
    private String jixinWangongType;

    @ApiModelProperty(value = "计薪完工类型（数据字典的键值）s_jixin_wangong_type")
    private String[] jixinWangongTypeList;

    /**
     * 登记日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "登记日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "登记日期")
    private Date reportDate;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    /**
     * 商品道序完成量台账单号
     */
    @Excel(name = "道序完成台账编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品道序完成量台账单号")
    private Long stepCompleteRecordCode;

    /**
     * 员工号
     */
    @Excel(name = "员工号")
    @ApiModelProperty(value = "员工号")
    private String workerCode;

    /**
     * 道序编码
     */
    @Excel(name = "道序编码")
    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    /**
     * 班组（结算）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组sid")
    private Long workCenterSid;

    /**
     * 班组（结算）sid 多选
     */
    @ApiModelProperty(value = "班组sid多选")
    private Long[] workCenterSidList;
    /**
     * 班组(结算)
     */
    @Excel(name ="班组(结算)")
    @ApiModelProperty(value = "员工班组")
    private String workCenterName;

    /**
     * 创建人账号（用户名称）
     */
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 登记人
     */
    @Excel(name = "登记人")
    @ApiModelProperty(value = "登记人")
    private String reporterName;

    @ApiModelProperty(value = "登记人")
    private String reporter;


}

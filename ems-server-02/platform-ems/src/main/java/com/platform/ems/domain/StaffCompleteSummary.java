package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
/**
 * 员工完成量明细汇总对象
 *
 * @author 韦德权
 * @date 2022-11-1
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffCompleteSummary extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 工厂sid
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    /**
     * 完成日期 起
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "完成日期 起")
    private String completeDateBegin;

    /**
     * 完成日期 至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "完成日期 至")
    private String completeDateEnd;

    /**
     * 工厂简称
     */
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 操作部门
     */
    @Excel(name = "操作部门")
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private String department;

    @ApiModelProperty(value = "操作部门")
    private String[] departmentList;

    /**
     * 班组（结算）
     */
    @Excel(name ="班组(结算)")
    @ApiModelProperty(value = "员工班组")
    private String workCenterName;

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

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品编码")
    private Long productSid;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @ApiModelProperty(value = "排产批次号")
    private Integer paichanBatch;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号精确查询传Y")
    private String paichanBatchIs;

    /**
     * 商品道序序号
     */
    @Excel(name = "道序序号")
    @ApiModelProperty(value = "序号(商品道序)")
    private BigDecimal sort;

    /**
     * 完成量(累计)
     */
    @Excel(name = "累计完成量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "累计完成量")
    private BigDecimal completeQuantity;

    /**
     * 道序名称
     */
    @Excel(name = "道序名称")
    @ApiModelProperty(value = "道序名称")
    private String processStepName;

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
     * 商品名称
     */
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String productName;

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
     * 班组（隶属）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "班组sid")
    private Long staffWorkCenterSid;

    /**
     * 班组（隶属）
     */
    @Excel(name = "班组(隶属)")
    @ApiModelProperty(value = "班组隶属")
    private String staffWorkCenterName;

    /**
     * 班组（隶属）sid 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "班组sid多选")
    private Long[] staffWorkCenterSidList;

}

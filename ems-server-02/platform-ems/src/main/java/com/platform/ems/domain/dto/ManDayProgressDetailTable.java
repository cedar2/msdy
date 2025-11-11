package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 生产日进度报表 查看详情 行转列 ManDayProgressDetailTable
 *
 * @author chenkaiwen
 * @date 2022-11-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayProgressDetailTable {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

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
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @ApiModelProperty(value = "系统SID-物料&商品sku1 多选")
    private Long[] sku1SidList;

    @ApiModelProperty(value = "SKU1类型（数据字典的键值或配置档案的编码）")
    private String sku1Type;

    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    @ApiModelProperty(value = "系统自增长ID-工序（多选）")
    private Long[] processSidList;

    @ApiModelProperty(value = "工序名称")
    private String processName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @ApiModelProperty(value = "工作中心/班组sid（多选）")
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @ApiModelProperty(value = "分配量(裁片)")
    private BigDecimal quantityFenpei;

    @ApiModelProperty(value = "每行的数据")
    private List<ManDayProgressDetailTableItem> itemList;

    @ApiModelProperty(value = "日期")
    private List<String> daysList;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;
}
